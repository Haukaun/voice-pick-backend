package no.ntnu.bachelor.voicepick.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.dtos.AddWarehouseDto;
import no.ntnu.bachelor.voicepick.dtos.EmailDto;
import no.ntnu.bachelor.voicepick.exceptions.InvalidInviteCodeException;
import no.ntnu.bachelor.voicepick.features.authentication.dtos.VerificationCodeInfo;
import no.ntnu.bachelor.voicepick.features.authentication.exceptions.UnauthorizedException;
import no.ntnu.bachelor.voicepick.features.authentication.models.RoleType;
import no.ntnu.bachelor.voicepick.features.authentication.models.User;
import no.ntnu.bachelor.voicepick.features.authentication.services.AuthService;
import no.ntnu.bachelor.voicepick.features.authentication.services.UserService;
import no.ntnu.bachelor.voicepick.mappers.UserMapper;
import no.ntnu.bachelor.voicepick.mappers.WarehouseMapper;
import no.ntnu.bachelor.voicepick.services.WarehouseService;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Set;

@RestController
@RequestMapping("/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

  private final WarehouseMapper warehouseMapper = Mappers.getMapper(WarehouseMapper.class);
  private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);
  private final WarehouseService warehouseService;
  private final UserService userService;

  private final AuthService authService;

  /**
   * Sends a email to the recipient with an invitation code to
   * the warehouse of the sender.
   * @param recipient the email of the user to be invited.
   * @return ResponseEntity with proper error message and HttpStatus.
   */
  @PreAuthorize("hasRole('LEADER')")
  @PostMapping("/invite")
  public ResponseEntity<String> sendInviteMail(@RequestBody EmailDto recipient) {
    ResponseEntity<String> response;
    try {
      User currentUser = userService.getCurrentUser();
      response = warehouseService.inviteToWarehouse(currentUser, recipient);
    } catch (EntityNotFoundException e) {
      response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    } catch (UnauthorizedException e) {
      response = new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    } catch (RuntimeException e) {
      response = new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return response;
  }

  /**
   * Adds a user to the warehouse related to the entered verificationCodeInfo, if
   * it is valid information.
   * @param verificationCodeInfo the verification code information to join with.
   * @return 200 OK if success, 404 NOT FOUND if it doesn't find the join code, warehouse, or the
   * authenticated user. 401 UNAUTHORIZED if the user isn't authorized.
   */
  @PostMapping("/join")
  public ResponseEntity<Object> joinWarehouse(@RequestBody VerificationCodeInfo verificationCodeInfo) {
    ResponseEntity<Object> response;
      try {
        User currentUser = userService.getCurrentUser();
        var warehouse = warehouseService.joinWarehouse(verificationCodeInfo, currentUser);
        response = new ResponseEntity<>(warehouseMapper.toWarehouseDto(warehouse), HttpStatus.OK);
      } catch (EntityNotFoundException e) {
        response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
      } catch (UnauthorizedException e) {
        response = new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
      } catch (InvalidInviteCodeException e) {
        response = new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
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
  public ResponseEntity<Object> createWarehouse(@RequestBody AddWarehouseDto addWarehouseDto) {
    ResponseEntity<Object> response;
    try {
      User currentUser = userService.getCurrentUser();
      var warehouse = warehouseService.createWarehouse(currentUser, addWarehouseDto);
      authService.addRole(currentUser.getUuid(), RoleType.LEADER);
      response = new ResponseEntity<>(warehouseMapper.toWarehouseDto(warehouse), HttpStatus.OK);
    } catch (EntityNotFoundException e) {
      response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    } catch (JsonProcessingException e) {
      response = new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return response;
  }

  /**
   * Gets all users in a warehouse.
   * @param userId the id of the user to remove.
   * @return 204 OK if user is successfully removed from the warehouse.
   */
  @PreAuthorize("hasAnyRole('ADMIN', 'LEADER')")
  @DeleteMapping("/users/{id}")
  public ResponseEntity<String> removeUserFromWarehouse(@PathVariable("id") String userId) {
    ResponseEntity<String> response;
    try {
      User currentUser = userService.getCurrentUser();
      var warehouse = currentUser.getWarehouse();
      warehouseService.removeUserFromWarehouse(warehouse, userId);
      response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (EntityNotFoundException e) {
      response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
    return response;
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'LEADER')")
  @GetMapping("/users")
  public ResponseEntity<Object> getUsersInWarehouse() {
    ResponseEntity<Object> response;
    var currentUser = userService.getCurrentUser();
    Set<User> usersInWarehouse;
    try {
      usersInWarehouse = warehouseService.findAllUsersInWarehouse(warehouseService.findWarehouseByUser(currentUser).orElse(null));
      response = new ResponseEntity<>(userMapper.toUserDto(usersInWarehouse), HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      response = new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
    return response;
  }



}
