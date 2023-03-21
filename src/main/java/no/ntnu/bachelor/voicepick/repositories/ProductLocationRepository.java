package no.ntnu.bachelor.voicepick.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import no.ntnu.bachelor.voicepick.models.ProductLocation;

public interface ProductLocationRepository extends JpaRepository<ProductLocation, Long> {
  Optional<ProductLocation> findFirstByName(String location);

  List<ProductLocation> findByName(String name);
}
