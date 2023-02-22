package no.ntnu.bachelor.voicepick.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.dtos.AddLocationRequest;
import no.ntnu.bachelor.voicepick.models.Location;
import no.ntnu.bachelor.voicepick.repositories.LocationRepository;

/**
 * A service class for the location model
 */
@Service
@RequiredArgsConstructor
public class LocationService {

  private final LocationRepository repository;

  /**
   * Adds a location to the repository
   * 
   * @param location to add
   */
  public void addLocation(AddLocationRequest location) throws IllegalArgumentException {
    if (location.getLocation() == null)
      throw new IllegalArgumentException("Location cannot be null");
    if (location.getControlDigits() == null)
      throw new IllegalArgumentException("Control digits cannot be null");

    var _location = new Location(location.getLocation(), location.getControlDigits());

    this.repository.save(_location);
  }

  /**
   * Returns a location object based on a location string
   * 
   * @param location the location string
   * @return a location object
   * @throws EntityNotFoundException when a location with the given location
   *                                 string is not found
   */
  public Location getLocation(String location) throws EntityNotFoundException {
    Optional<Location> result = this.repository.findFirstByLocation(location);

    if (result.isEmpty()) {
      throw new EntityNotFoundException("No location found with location: " + location);
    }

    return result.get();
  }

}
