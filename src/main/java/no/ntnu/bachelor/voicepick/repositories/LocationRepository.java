package no.ntnu.bachelor.voicepick.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import no.ntnu.bachelor.voicepick.models.ProductLocation;

public interface LocationRepository extends JpaRepository<ProductLocation, Long> {
  Optional<ProductLocation> findFirstByLocation(String location);
}
