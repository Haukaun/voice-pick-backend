package no.ntnu.bachelor.voicepick.features.pluck.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import no.ntnu.bachelor.voicepick.features.pluck.models.PluckListLocation;

public interface PluckListLocationRepository extends JpaRepository<PluckListLocation, Long> {

    Optional<PluckListLocation> findFirstByName(String location);

    List<PluckListLocation> findByName(String name);
}
