package no.ntnu.bachelor.voicepick.features.pluck.repositories;

import no.ntnu.bachelor.voicepick.features.pluck.models.CargoCarrier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CargoCarrierRepository extends JpaRepository<CargoCarrier, Long> {

  Optional<CargoCarrier> findByIdentifier(Long identifier);

}
