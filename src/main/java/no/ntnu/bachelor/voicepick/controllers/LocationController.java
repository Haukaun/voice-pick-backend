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
import no.ntnu.bachelor.voicepick.services.LocationService;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationController {

  private final LocationService locationService;

  /**
   * Endpoint for adding a new location
   * 
   * @param location a request body containing information about the location
   * @return {@code 200 OK} if added, {@code 404 METHOD_NOT_ALLOWED} if request
   *         body is incorrect
   */
  @PostMapping
  public ResponseEntity<String> addLocation(@RequestBody AddLocationRequest location) {
    ResponseEntity<String> response;

    try {
      this.locationService.addLocation(location);
      response = new ResponseEntity<String>(HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      response = new ResponseEntity<String>(e.getMessage(), HttpStatus.METHOD_NOT_ALLOWED);
    } catch (EntityExistsException e) {
      response = new ResponseEntity<String>(e.getMessage(), HttpStatus.METHOD_NOT_ALLOWED);
    }

    return response;
  }

}
