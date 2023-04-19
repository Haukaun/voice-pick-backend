package no.ntnu.bachelor.voicepick.controllers;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.dtos.AddWarehouseDto;
import no.ntnu.bachelor.voicepick.dtos.EmailDto;
import no.ntnu.bachelor.voicepick.features.authentication.dtos.VerificationCodeInfo;
import no.ntnu.bachelor.voicepick.features.authentication.exceptions.UnauthorizedException;
import no.ntnu.bachelor.voicepick.features.authentication.models.User;
import no.ntnu.bachelor.voicepick.features.authentication.services.UserService;
import no.ntnu.bachelor.voicepick.services.WarehouseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

  private final WarehouseService warehouseService;
  private final UserService userService;

  /**
   * Sends a email to the recipient with an invitation code to
   * the warehouse of the sender.
   * @param recipient the email of the user to be invited.
   * @return ResponseEntity with proper error message and HttpStatus.
   */
  @PreAuthorize("hasRole('LEADER')")
  @PostMapping("/invite")
  public ResponseEntity<String> sendInviteMail(@RequestBody EmailDto recipient) {
    User currentUser = userService.getCurrentUser();
    return warehouseService.inviteToWarehouse(currentUser, recipient);
  }

  /**
   * Adds a user to the warehouse related to the entered verificationCodeInfo, if
   * it is valid information.
   * @param verificationCodeInfo the verification code information to join with.
   * @return 200 OK if success, 404 NOT FOUND if it doesn't find the join code, warehouse, or the
   * authenticated user. 401 UNAUTHORIZED if the user isn't authorized.
   */
  @PostMapping("/join")
  public ResponseEntity<String> joinWarehouse(@RequestBody VerificationCodeInfo verificationCodeInfo) {
    ResponseEntity<String> response;
      try {
        User currentUser = userService.getCurrentUser();
        warehouseService.joinWarehouse(verificationCodeInfo, currentUser);
        response = new ResponseEntity<>(HttpStatus.OK);
      } catch (EntityNotFoundException e) {
        response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
      } catch (UnauthorizedException e) {
        response = new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
      }
    return response;
  }

  /**
   * Creates a warehouse with the given name and address in request body.
   * @param addWarehouseDto name and address of the warehouse to add.
   * @return 200 OK if warehouse was created and user added, or 404 NOT FOUND
   * if the user is not found.
   */
  @PostMapping
  public ResponseEntity<String> createWarehouse(@RequestBody AddWarehouseDto addWarehouseDto) {
    ResponseEntity<String> response;
    try {
      User currentUser = userService.getCurrentUser();
      warehouseService.createWarehouse(currentUser, addWarehouseDto);
      response = new ResponseEntity<>(HttpStatus.OK);
    } catch (EntityNotFoundException e) {
      response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
    return response;
  }

}
