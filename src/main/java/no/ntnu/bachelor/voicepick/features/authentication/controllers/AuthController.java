package no.ntnu.bachelor.voicepick.features.authentication.controllers;

import jakarta.persistence.EntityNotFoundException;
import no.ntnu.bachelor.voicepick.features.authentication.dtos.*;
import no.ntnu.bachelor.voicepick.features.smtp.models.Email;
import no.ntnu.bachelor.voicepick.features.smtp.services.EmailSender;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.features.authentication.services.AuthService;
import no.ntnu.bachelor.voicepick.features.authentication.utils.JwtUtil;

import java.util.concurrent.Future;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final EmailSender emailSender;
  private final JwtUtil jwtUtil;

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
    ResponseEntity<LoginResponse> response;

    try {
      LoginResponse loginResponse = this.authService.login(request);
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
  public String sendPasswordMail(@RequestBody EmailDto recipient) {
    Email email = new Email(recipient, Email.Subject.RESET_PASSWORD);
    Future<String> futureResult = emailSender.sendMail(email);
    return emailSender.getResultFromFuture(futureResult);
  }

  @PostMapping("/invite-code")
  public String sendInviteMail(@RequestBody EmailDto recipient) {
    Email email = new Email(recipient, Email.Subject.INVITE_CODE);
    Future<String> futureResult = emailSender.sendMail(email);
    return emailSender.getResultFromFuture(futureResult);
  }

  @PostMapping("/verify-email")
  public String sendRegistrationMail(@RequestBody EmailDto recipient) {
    Email email = new Email(recipient, Email.Subject.COMPLETE_REGISTRATION);
    Future<String> futureResult = emailSender.sendMail(email);
    return emailSender.getResultFromFuture(futureResult);
  }


  @PostMapping("/check-verification-code")
  public ResponseEntity<Boolean> checkVerificationCode(@RequestBody VerificationCodeInfo verificationCode) {
    ResponseEntity<Boolean> response;

    if (Email.containsVerificationCode(verificationCode.getVerificationCode())) {
      response = new ResponseEntity<>(true, HttpStatus.OK);
      // Update the emailVerified attribute in Keycloak
      try{
        String userId = authService.getUserId(verificationCode.getEmail());
        authService.setEmailVerified(userId, true);
      } catch (JsonProcessingException e) {
        response = new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
      } 
    } else {
      response = new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
    }
    return response;
  }

  @PostMapping("/email-verified")
  public ResponseEntity<String> getEmailVerified(@RequestBody TokenRequest token) {
    ResponseEntity<String> response;

    try {
      String emailVerified = jwtUtil.getEmailVerified(token.getToken());
      response = new ResponseEntity<>(emailVerified, HttpStatus.OK);
    } catch (JsonProcessingException e) {
      response = new ResponseEntity<>("Something went wrong", HttpStatus.BAD_REQUEST);
    }
    return response;
  }

}
