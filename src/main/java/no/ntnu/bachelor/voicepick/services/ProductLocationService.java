package no.ntnu.bachelor.voicepick.services;

import java.util.List;
import java.util.Optional;

import no.ntnu.bachelor.voicepick.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.dtos.AddLocationRequest;
import no.ntnu.bachelor.voicepick.models.ProductLocation;
import no.ntnu.bachelor.voicepick.repositories.ProductLocationRepository;

/**
 * A service class for the location model
 */
@Service
@RequiredArgsConstructor
public class ProductLocationService {

  private final ProductLocationRepository repository;

  private final ProductRepository productRepository;

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

    var locationToSave = new ProductLocation(location.getName(), location.getControlDigits());

    this.repository.save(locationToSave);
  }

  /**
   * Saves the location
   *
   * @param location to be saved
   */
  public void save(ProductLocation location) {
    this.repository.save(location);
  }

  /**
   * Returns a location object based on a location string
   * 
   * @param location the location string
   * @return a optional of the location
   */
  public Optional<ProductLocation> getLocation(String location) {
    return this.repository.findFirstByName(location);
  }

  /**
   * Returns all locations stored in the repository
   * 
   * @return a list of all locations
   */
  public List<ProductLocation> getAll() {
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
      var product = location.getProduct();
      if (product != null) {
        product.setLocation(null);
        this.productRepository.save(product);
      }
      location.setProduct(null);
    });

    this.repository.deleteAll(locationsFound);
  }

}
