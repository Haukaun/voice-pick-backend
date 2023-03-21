package no.ntnu.bachelor.voicepick.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.dtos.AddLocationRequest;
import no.ntnu.bachelor.voicepick.services.ProductLocationService;

@RestController
@RequestMapping("/productlocations")
@RequiredArgsConstructor
public class ProductLocationController {

  private final ProductLocationService locationService;

  /**
   * Endpoint for adding a new location
   * 
   * @param location a request body containing information about the location
   * @return {@code 200 OK} if added, {@code 404 METHOD_NOT_ALLOWED} if request
   *         body is incorrect
   */
  @PostMapping
  public ResponseEntity<String> addProductLocation(@RequestBody AddLocationRequest location) {
    ResponseEntity<String> response;

    try {
      this.locationService.addLocation(location);
      response = new ResponseEntity<>(HttpStatus.OK);
    } catch (IllegalArgumentException | EntityExistsException e) {
      response = new ResponseEntity<>(e.getMessage(), HttpStatus.METHOD_NOT_ALLOWED);
    }

    return response;
  }
}
