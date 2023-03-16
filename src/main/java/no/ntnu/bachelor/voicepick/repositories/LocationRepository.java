package no.ntnu.bachelor.voicepick.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import no.ntnu.bachelor.voicepick.models.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
  Optional<Location> findFirstByName(String location);

  List<Location> findByName(String name);
}
