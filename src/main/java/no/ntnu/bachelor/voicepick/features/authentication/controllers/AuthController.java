package no.ntnu.bachelor.voicepick.features.authentication.controllers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import no.ntnu.bachelor.voicepick.dtos.EmailDto;
import no.ntnu.bachelor.voicepick.features.authentication.dtos.*;
import no.ntnu.bachelor.voicepick.features.authentication.exceptions.InvalidPasswordException;
import no.ntnu.bachelor.voicepick.features.authentication.exceptions.ResetPasswordException;
import no.ntnu.bachelor.voicepick.features.authentication.models.Role;
import no.ntnu.bachelor.voicepick.features.authentication.models.RoleType;
import no.ntnu.bachelor.voicepick.features.smtp.models.Email;
import no.ntnu.bachelor.voicepick.features.smtp.services.EmailSender;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.features.authentication.services.AuthService;

import java.util.concurrent.Future;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final EmailSender emailSender;

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
    ResponseEntity<LoginResponse> response;

    try {
      var loginResponse = this.authService.login(request);
      response = new ResponseEntity<>(loginResponse, HttpStatus.OK);
    } catch (Exception e) {
      response = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    return response;
  }

  @PostMapping("/signup")
  public ResponseEntity<String> signup(@RequestBody SignupRequest request) {
    ResponseEntity<String> response;

    try {
      this.authService.signup(request);
      response = new ResponseEntity<>("User created successfully", HttpStatus.OK);
    } catch (HttpClientErrorException e) {
      response = new ResponseEntity<>(e.getStatusCode());
    } catch (IllegalArgumentException e) {
      response = new ResponseEntity<>(e.getMessage(), HttpStatus.METHOD_NOT_ALLOWED);
    } catch (Exception e) {
      response = new ResponseEntity<>("Something went wrong! Please try again.", HttpStatus.BAD_REQUEST);
    }
    return response;
  }

  @PostMapping("/signout")
  public ResponseEntity<String> signout(@RequestBody TokenRequest request) {
    if (this.authService.signOut(request)) {
      return new ResponseEntity<>("Signed out successfully", HttpStatus.OK);
    } else {
      return new ResponseEntity<>("Something went wrong! Could not sign you out.", HttpStatus.BAD_REQUEST);
    }
  }

  @PostMapping("/introspect")
  public ResponseEntity<String> introspect(@RequestBody TokenRequest request) {
    if (this.authService.introspect(request)) {
      return new ResponseEntity<>("Token is active", HttpStatus.OK);
    } else {
      return new ResponseEntity<>("Token is not active", HttpStatus.UNAUTHORIZED);
    }
  }

  @DeleteMapping("/users")
  @Transactional
  public ResponseEntity<String> delete() {
    ResponseEntity<String> response;

    try {
      this.authService.delete();
      response = new ResponseEntity<>(HttpStatus.OK);
    } catch (EntityNotFoundException e) {
      response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } catch (HttpClientErrorException e) {
      response = new ResponseEntity<>(e.getStatusCode());
    } catch (Exception e) {
      response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    return response;
  }

  @PostMapping("/reset-password")
  public ResponseEntity<String> sendPasswordMail(@RequestBody EmailDto recipient) {
    ResponseEntity<String> response;

    try {
      var uuid = this.authService.getUserId(recipient.getEmail());
      var password = this.authService.forgotPassword(uuid);
      var email = new Email(recipient, Email.Subject.RESET_PASSWORD, password);
      var futureResult = emailSender.sendMail(email);
      response = emailSender.getResultFromFuture(futureResult);
    } catch (JsonProcessingException e) {
      response = new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (ResetPasswordException e) {
      response = new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (EntityNotFoundException e) {
      response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    return response;
  }

  /**
   * Change password of a user
   *
   * @param uuid of the user to change the password of
   * @param request the request containing information of the current password and the new password
   * @return a new login response containing updated tokens
   */
  @PostMapping("/users/{uuid}/change-password")
  public ResponseEntity<Object> changePassword(@PathVariable String uuid, @RequestBody ChangePasswordRequest request) {
    ResponseEntity<Object> response;

    try {
      var loginResponse = this.authService.changePassword(uuid, request.getEmail(), request.getCurrentPassword(), request.getNewPassword());
      response = new ResponseEntity<>(loginResponse, HttpStatus.OK);
    } catch (JsonProcessingException | ResetPasswordException e) {
      response = new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (InvalidPasswordException e) {
      response = new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
    }

    return response;
  }

  @PostMapping("/verify-email")
  public ResponseEntity<String> sendRegistrationMail(@RequestBody EmailDto recipient) {
    ResponseEntity<String> response = null;

    String uuid = null;
    try {
      uuid = this.authService.getUserId(recipient.getEmail());
    } catch (JsonProcessingException e) {
      response = new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    if (response == null) {
      var verificationCode = this.authService.generateEmailVerificationCode(uuid);

      var email = new Email(recipient, Email.Subject.COMPLETE_REGISTRATION, verificationCode);
      var futureResult = emailSender.sendMail(email);

      response = emailSender.getResultFromFuture(futureResult);
    }

    return response;
  }


  @PostMapping("/check-verification-code")
  public ResponseEntity<Boolean> checkVerificationCode(@RequestBody VerificationCodeInfo verificationCode) {
    ResponseEntity<Boolean> response = null;

    var code = verificationCode.getVerificationCode();
    String uuid = null;
    try {
      uuid = this.authService.getUserId(verificationCode.getEmail());
    } catch (JsonProcessingException e) {
      response = new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    if (response == null) {
      var validCode = this.authService.validateEmailVerificationCode(uuid, code);
      if (validCode) {
        try {
          this.authService.setEmailVerified(uuid, true);
          response = new ResponseEntity<>(true, HttpStatus.OK);
        } catch (JsonProcessingException e) {
          response = new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
      } else {
        response = new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
      }
    }

    return response;
  }

  @PostMapping("/users/{id}/roles/leader")
  public ResponseEntity<String> addLeaderRole(@PathVariable("id") String id) {
    ResponseEntity<String> response;
    try {
      authService.addRole(id, RoleType.LEADER);
      response = new ResponseEntity<>(HttpStatus.OK);
    } catch (JsonProcessingException e) {
       response = new ResponseEntity<>("Failed to add role", HttpStatus.BAD_REQUEST);
    }
    return response;
  }

}
