package no.ntnu.bachelor.voicepick.features.pluck.services;

import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.features.pluck.models.CargoCarrier;
import no.ntnu.bachelor.voicepick.features.pluck.repositories.CargoCarrierRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CargoCarrierService {

  private final CargoCarrierRepository repository;

  /**
   * Adds a cargo carrier to the repository
   *
   * @param cargoCarrier to be added
   */
  public void add(CargoCarrier cargoCarrier) {
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
}
