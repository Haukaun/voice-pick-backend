package no.ntnu.bachelor.voicepick.controllers;

import jakarta.persistence.EntityNotFoundException;
import no.ntnu.bachelor.voicepick.models.LocationEntity;
import no.ntnu.bachelor.voicepick.services.LocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.dtos.AddLocationRequest;

import java.util.Set;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationController {

  private final LocationService locationService;

  /**
   * Endpoint for getting entities stored at a location
   *
   * @param id of the location to get entities for
   * @return {@code 200 OK} with a list of all entities stored if everything is ok.
   * {@code 404 NOT_FOUND} if no location was found with the id provided
   */
  @GetMapping("/{id}")
  public ResponseEntity<Set<LocationEntity>> getLocationEntities(@PathVariable Long id) {
    ResponseEntity<Set<LocationEntity>> response;

    try {
      response = new ResponseEntity<>(this.locationService.getLocationEntities(id), HttpStatus.OK);
    } catch (EntityNotFoundException e) {
      response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    return response;
  }

  /**
   * Endpoint for adding a new location
   * 
   * @param request a request body containing information about the location
   * @return {@code 200 OK} if added, {@code 405 METHOD_NOT_ALLOWED} if request
   *         body is incorrect
   */
  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN', 'LEADER')")
  public ResponseEntity<String> addLocation(@RequestBody AddLocationRequest request) {
    ResponseEntity<String> response;

    try {
      this.locationService.addLocation(request.getCode(), request.getControlDigits());
      response = new ResponseEntity<>(HttpStatus.OK);
    } catch (IllegalArgumentException | EntityExistsException e) {
      response = new ResponseEntity<>(e.getMessage(), HttpStatus.METHOD_NOT_ALLOWED);
    }

    return response;
  }
}
