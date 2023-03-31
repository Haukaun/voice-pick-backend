package no.ntnu.bachelor.voicepick.features.pluck.repositories;

import no.ntnu.bachelor.voicepick.features.authentication.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import no.ntnu.bachelor.voicepick.features.pluck.models.PluckList;

import java.util.List;

public interface PluckListRepository extends JpaRepository<PluckList, Long> {

  List<PluckList> findByUser(User user);

}
