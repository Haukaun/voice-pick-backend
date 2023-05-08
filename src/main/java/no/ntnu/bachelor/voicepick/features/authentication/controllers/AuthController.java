package no.ntnu.bachelor.voicepick.features.authentication.controllers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import no.ntnu.bachelor.voicepick.dtos.EmailDto;
import no.ntnu.bachelor.voicepick.features.authentication.dtos.*;
import no.ntnu.bachelor.voicepick.features.authentication.exceptions.InvalidPasswordException;
import no.ntnu.bachelor.voicepick.features.authentication.exceptions.ResetPasswordException;
import no.ntnu.bachelor.voicepick.features.authentication.exceptions.UnauthorizedException;
import no.ntnu.bachelor.voicepick.features.authentication.models.RoleType;
import no.ntnu.bachelor.voicepick.features.authentication.services.UserService;
import no.ntnu.bachelor.voicepick.features.smtp.models.Email;
import no.ntnu.bachelor.voicepick.features.smtp.services.EmailSender;
import no.ntnu.bachelor.voicepick.mappers.UserMapper;
import org.apache.coyote.Response;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.features.authentication.services.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final UserService userService;
  private final EmailSender emailSender;

  private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

  @PostMapping("/login")
  @Operation(summary = "Login")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Login successful", content = {
      @Content(mediaType = "application/json")
    }),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
  })
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
  @Operation(summary = "Signup")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Signup successful", content = @Content),
    @ApiResponse(responseCode = "400", description = "Something went wrong", content = @Content),
    @ApiResponse(responseCode = "405", description = "Method not allowed", content = @Content)
  })
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
  @Operation(summary = "Signout")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Signout successful", content = @Content),
    @ApiResponse(responseCode = "400", description = "Something went wrong", content = @Content)
  })
  public ResponseEntity<String> signout(@RequestBody TokenRequest request) {
    if (this.authService.signOut(request)) {
      return new ResponseEntity<>("Signed out successfully", HttpStatus.OK);
    } else {
      return new ResponseEntity<>("Something went wrong! Could not sign you out.", HttpStatus.BAD_REQUEST);
    }
  }

  @PostMapping("/refresh")
  public ResponseEntity<Object> refresh(@RequestBody TokenRequest request) {
    ResponseEntity<Object> response;
    try {
      response = new ResponseEntity<>(this.authService.refresh(request), HttpStatus.OK);
    } catch (HttpClientErrorException e) {
      response = new ResponseEntity<>(e.getMessage(), e.getStatusCode());
    } catch (Exception e) {
      response = new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return response;
  }

  @PostMapping("/introspect")
  @Operation(summary = "Introspect")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Token is active", content = @Content),
    @ApiResponse(responseCode = "401", description = "Token is not active", content = @Content)
  })
  public ResponseEntity<String> introspect(@RequestBody TokenRequest request) {
    if (this.authService.introspect(request)) {
      return new ResponseEntity<>("Token is active", HttpStatus.OK);
    } else {
      return new ResponseEntity<>("Token is not active", HttpStatus.UNAUTHORIZED);
    }
  }

  @DeleteMapping("/users")
  @Transactional
  @Operation(summary = "Delete user")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "User deleted", content = @Content),
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
    @ApiResponse(responseCode = "400", description = "Something went wrong", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
  })
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
    } finally {
      response = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    return response;
  }

  @PostMapping("/reset-password")
  @Operation(summary = "Reset password")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Password reset successful", content = @Content),
    @ApiResponse(responseCode = "400", description = "Something went wrong", content = @Content),
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
  })
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
  @Operation(summary = "Change password")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Password changed successfully", content = {
      @Content(mediaType = "application/json")
    }),
    @ApiResponse(responseCode = "400", description = "Something went wrong", content = @Content),
    @ApiResponse(responseCode = "403", description = "Invalid password", content = @Content),
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
  })
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
  @Operation(summary = "Send verification code to email")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Verification code sent", content = @Content),
    @ApiResponse(responseCode = "400", description = "Something went wrong", content = @Content),
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
  })
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
  @Operation(summary = "Check verification code")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Verification code is valid", content = @Content),
    @ApiResponse(responseCode = "400", description = "Verification code is invalid", content = @Content),
    @ApiResponse(responseCode = "500", description = "Something went wrong", content = @Content)
  })
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

  @PreAuthorize("hasAnyRole('ADMIN', 'LEADER')")
  @PostMapping("/users/{uuid}/roles/leader")
  @Operation(summary = "Add leader role")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Role added", content = {
      @Content(mediaType = "application/json")
    }),
    @ApiResponse(responseCode = "400", description = "Something went wrong", content = @Content),
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
  })
  public ResponseEntity<Object> addLeaderRole(@PathVariable("uuid") String uuid) {
    ResponseEntity<Object> response;
    try {
      var requestingUser = this.userService.getCurrentUser();
      var user = authService.tryAddRole(requestingUser.getUuid(), uuid, RoleType.LEADER);
      response = new ResponseEntity<>(userMapper.toUserDto(user), HttpStatus.OK);
    } catch (JsonProcessingException e) {
       response = new ResponseEntity<>("Failed to add role", HttpStatus.BAD_REQUEST);
    }
    return response;
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'LEADER')")
  @DeleteMapping("/users/{uuid}/roles/leader")
  @Operation(summary = "Remove leader role")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Role removed", content = {
      @Content(mediaType = "application/json")
    }),
    @ApiResponse(responseCode = "400", description = "Something went wrong", content = @Content),
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
  })
  public ResponseEntity<Object> removeLeaderRole(@PathVariable("uuid") String uuid) {
    ResponseEntity<Object> response;
    try {
      var requestingUser = this.userService.getCurrentUser();
      var user = authService.tryRemoveRole(requestingUser.getUuid(), uuid, RoleType.LEADER);
      response = new ResponseEntity<>(userMapper.toUserDto(user), HttpStatus.OK);
    } catch (JsonProcessingException e) {
      response = new ResponseEntity<>("Could not remove role.", HttpStatus.BAD_REQUEST);
    }
    return response;
  }
}
