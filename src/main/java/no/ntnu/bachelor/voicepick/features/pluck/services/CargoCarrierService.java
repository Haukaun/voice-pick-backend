package no.ntnu.bachelor.voicepick.features.pluck.services;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.features.pluck.models.CargoCarrier;
import no.ntnu.bachelor.voicepick.features.pluck.repositories.CargoCarrierRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CargoCarrierService {

  private final CargoCarrierRepository repository;

  /**
   * Adds a cargo carrier to the repository. If a cargo carrier with the same identifier already exists an
   * {@code EntityExistsException} is thrown
   *
   * @param cargoCarrier to be added
   * @throws EntityExistsException if a cargo carrier with the same identifier is found
   */
  public void add(CargoCarrier cargoCarrier) throws EntityExistsException {
    Optional<CargoCarrier> foundCargoCarrier = this.repository.findByIdentifier(cargoCarrier.getIdentifier());

    if (foundCargoCarrier.isPresent()) {
      throw new EntityExistsException("Cargo carrier with identifier (" + cargoCarrier.getIdentifier() + ") already exists");
    }

    this.repository.save(cargoCarrier);
  }

  /**
   * Returns a list of all available cargo carriers
   *
   * @return a list with cargo carriers
   */
  public List<CargoCarrier> findAll() {
    return this.repository.findAll();
  }

  /**
   * Deletes a cargo carrier from the repository
   *
   * @param cargoCarrier to be deleted
   */
  public void delete(CargoCarrier cargoCarrier) {
    this.repository.delete(cargoCarrier);
  }

  /**
   * Deletes all the cargo carries passed in as parameter
   *
   * @param cargoCarriers a list of all cargo carriers to be deleted
   */
  public void deleteAll(List<CargoCarrier> cargoCarriers) {
    this.repository.deleteAll(cargoCarriers);
  }
}
