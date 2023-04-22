package no.ntnu.bachelor.voicepick.features.pluck.services;

import jakarta.persistence.EntityNotFoundException;
import no.ntnu.bachelor.voicepick.features.pluck.dtos.PluckDto;
import no.ntnu.bachelor.voicepick.features.pluck.dtos.UpdatePluckRequest;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.features.pluck.models.Pluck;
import no.ntnu.bachelor.voicepick.features.pluck.repositories.PluckRepository;

/**
 * A service class that exposes method for the pluck entity
 */
@Service
@RequiredArgsConstructor
public class PluckService {

  public final PluckRepository repository;

  /**
   * Saves a pluck to the repository
   * 
   * @param pluck the pluck to save
   */
  public Pluck savePluck(Pluck pluck) {
    return this.repository.save(pluck);
  }

  /**
   * Updates a pluck
   *
   * @param id of the pluck to update
   * @param dto an object containing the updated fields of the pluck
   */
  public void updatePluck(Long id, UpdatePluckRequest dto) {
    var optionalPluck = this.repository.findById(id);

    if (optionalPluck.isEmpty()) {
      throw new EntityNotFoundException("Could not find pluck with id: " + id);
    }

    var pluck = optionalPluck.get();

    pluck.setAmountPlucked(dto.getAmountPlucked());
    pluck.setConfirmedAt(dto.getConfirmedAt());
    pluck.setPluckedAt(dto.getPluckedAt());

    this.repository.save(pluck);
  }

}
