package no.ntnu.bachelor.voicepick.features.pluck.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.dtos.AddLocationRequest;
import no.ntnu.bachelor.voicepick.features.pluck.models.PluckListLocation;
import no.ntnu.bachelor.voicepick.features.pluck.repositories.PluckListLocationRepository;
import no.ntnu.bachelor.voicepick.features.pluck.repositories.PluckListRepository;

@Service
@RequiredArgsConstructor
public class PluckListLocationService {
    
    private final PluckListLocationRepository repository;

    private final PluckListRepository pluckListRepository;

  /**
   * Adds a location to the repository
   * 
   * @param location to add
   */
  public void addLocation(AddLocationRequest location) throws IllegalArgumentException, EntityExistsException {

    if (location.getName() == null)
      throw new IllegalArgumentException("Location cannot be null");

    var result = this.getLocation(location.getName());
    if (result.isPresent()) {
      throw new EntityExistsException("Location with serial: " + location.getName() + " already exists");
    }

    var locationToSave = new PluckListLocation(location.getName(), location.getControlDigits());

    this.repository.save(locationToSave);
  }

  /**
   * Saves the location
   *
   * @param location to be saved
   */
  public void save(PluckListLocation location) {
    this.repository.save(location);
  }

  /**
   * Returns the location with the name given, can be empty
   * @param location the location string
   * @return a optional of the location
   */

  public Optional<PluckListLocation> getLocation(String location) {
    return this.repository.findFirstByName(location);
  }

  /**
   * Returns all locations stored in the repository
   * 
   * @return a list of all locations
   */
  public List<PluckListLocation> getAll() {
    return this.repository.findAll();
  }

  /**
   * Deletes all the location with the name given
   *
   * @param name of the location to delete by
   */
  public void deleteAll(String name) {
    var locationsFound = this.repository.findByName(name);

    locationsFound.forEach(location -> {
      var pluckList = location.getPluckList();
      if (pluckList != null) {
        pluckList.setLocation(null);
        this.pluckListRepository.save(pluckList);
      }
      location.setPluckList(null);
    });

    this.repository.deleteAll(locationsFound);
  }
}
