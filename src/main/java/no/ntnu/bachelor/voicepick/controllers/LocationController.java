package no.ntnu.bachelor.voicepick.controllers;

import jakarta.persistence.EntityNotFoundException;
import no.ntnu.bachelor.voicepick.dtos.LocationDto;
import no.ntnu.bachelor.voicepick.dtos.LocationEntityDto;
import no.ntnu.bachelor.voicepick.features.authentication.services.UserService;
import no.ntnu.bachelor.voicepick.mappers.LocationEntityMapper;
import no.ntnu.bachelor.voicepick.mappers.LocationMapper;
import no.ntnu.bachelor.voicepick.services.LocationService;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import java.util.Set;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationController {

  private final LocationService locationService;

  private final UserService userService;

  private final LocationMapper locationMapper = Mappers.getMapper(LocationMapper.class);
  private final LocationEntityMapper locationEntityMapper = Mappers.getMapper(LocationEntityMapper.class);


  /**
   * Endpoint for adding a new location
   * 
   * @param location a request body containing information about the location
   * @return {@code 200 OK} if added, {@code 405 METHOD_NOT_ALLOWED} if request
   *         body is incorrect
   */
  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN', 'LEADER')")
  public ResponseEntity<String> addLocation(@RequestBody LocationDto location) {
    ResponseEntity<String> response;
    try {
      this.locationService.addLocation(locationMapper.toLocation(location), userService.getCurrentUser().getWarehouse());
      response = new ResponseEntity<>(HttpStatus.OK);
    } catch (IllegalArgumentException | EntityExistsException e) {
      response = new ResponseEntity<>(e.getMessage(), HttpStatus.METHOD_NOT_ALLOWED);
    }

    return response;
  }

  /**
   * Endpoint for deleting location
   *
   * @param code a path-variable containing the location code
   * @return {@code 200 OK} if removed, {@code 405 METHOD_NOT_ALLOWED} if request
   *         body is incorrect
   */
  @DeleteMapping("/{code}")
  @PreAuthorize("hasAnyRole('ADMIN', 'LEADER')")
  public ResponseEntity<String> deleteLocation(@PathVariable("code") String code) {
    ResponseEntity<String> response;
    try {
      this.locationService.deleteSpecificLocation(code, userService.getCurrentUser().getWarehouse());
      response = new ResponseEntity<>(HttpStatus.OK);
    } catch (EntityNotFoundException e) {
      response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    } catch (IllegalArgumentException e){
      response = new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    return response;
  }

  /**
   * Endpoint for getting all location in a warehouse, used to populate location list
   *
   * @return a set of locations {@code 200 OK} if locations found, {@code 405 METHOD_NOT_ALLOWED} if request
   *         body is incorrect
   */
  @GetMapping()
  public ResponseEntity<Set<LocationDto>> getAllLocationsInWarehouse() {
    ResponseEntity<Set<LocationDto>> response;
    try {
      var locations = locationMapper.toLocationDto(this.locationService.getAllLocationsInWarehouse(userService.getCurrentUser().getWarehouse().getId()));
      response = new ResponseEntity<>(locations, HttpStatus.OK);
    } catch (EntityNotFoundException e) {
      response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    return response;
  }


  /**
   * Endpoint for updating a location
   *
   * @param locationCode a path-variable containing the location code
   * @param location a request body containing information about the location
   * @return {@code 200 OK} if updated, {@code 405 METHOD_NOT_ALLOWED} if request
   *         body is incorrect
   */
  @PatchMapping("/{locationCode}")
  @PreAuthorize("hasAnyRole('ADMIN', 'LEADER')")
  public ResponseEntity<String> updateLocation(@PathVariable("locationCode") String locationCode, @RequestBody LocationDto location) {
    ResponseEntity<String> response;
    try {
      this.locationService.updateLocation(locationCode, location);
      response = new ResponseEntity<>(HttpStatus.OK);
    } catch (EntityExistsException e) {
      response = new ResponseEntity<>(e.getMessage(), HttpStatus.METHOD_NOT_ALLOWED);
    }
    return response;
  }


  /**
   * Endpoint for getting all entities stored at a specific location
   *
   * @param code path-variable containing the location code
   * @return a set of entities {@code 200 OK} if entities in location found, {@code 405 METHOD_NOT_ALLOWED} if request
   *         body is incorrect
   */
  @GetMapping("/{code}/entities")
  @PreAuthorize("hasAnyRole('ADMIN', 'LEADER')")
  public ResponseEntity<Set<LocationEntityDto>> getEntitiesInLocation(@PathVariable String code) {
    ResponseEntity<Set<LocationEntityDto>> response;
    try {
      var warehouse = userService.getCurrentUser().getWarehouse();
      var entities = locationService.getEntitiesInLocation(code, warehouse);

      if (entities.isEmpty()){
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
      response = new ResponseEntity<>(locationEntityMapper.toLocationEntityDto(entities), HttpStatus.OK);

    } catch (EntityNotFoundException e) {
      response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    return response;
  }
}
