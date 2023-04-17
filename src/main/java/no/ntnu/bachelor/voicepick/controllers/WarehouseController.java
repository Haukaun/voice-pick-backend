package no.ntnu.bachelor.voicepick.controllers;

import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.dtos.AddWarehouseDto;
import no.ntnu.bachelor.voicepick.dtos.EmailDto;
import no.ntnu.bachelor.voicepick.features.authentication.models.User;
import no.ntnu.bachelor.voicepick.features.authentication.services.AuthService;
import no.ntnu.bachelor.voicepick.features.authentication.services.UserService;
import no.ntnu.bachelor.voicepick.models.Warehouse;
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

  private final AuthService authService;

  @PreAuthorize("hasRole('LEADER')")
  @PostMapping("/invite-code")
  public ResponseEntity<String> sendInviteMail(@RequestBody EmailDto recipient) {
    User currentUser = userService.getCurrentUser();
    return warehouseService.inviteToWarehouse(currentUser, recipient);
  }

  @PostMapping
  public ResponseEntity<Warehouse> createWarehouse(@RequestBody AddWarehouseDto request) {
    ResponseEntity<Warehouse> response;
    User currentUser = userService.getCurrentUser();
    Warehouse warehouse = warehouseService.createWarehouse(currentUser, request);
    response = new ResponseEntity<>(warehouse, HttpStatus.OK);
    return response;
  }

}
