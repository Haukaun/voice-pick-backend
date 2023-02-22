package no.ntnu.bachelor.voicepick.features.pluck.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import no.ntnu.bachelor.voicepick.features.pluck.models.PluckList;

public interface PluckListRepository extends JpaRepository<PluckList, Long> {

}
