package no.ntnu.bachelor.voicepick.features.pluck.services;

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

}
