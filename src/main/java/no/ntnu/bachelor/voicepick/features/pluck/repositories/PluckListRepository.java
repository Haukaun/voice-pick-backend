package no.ntnu.bachelor.voicepick.features.pluck.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import no.ntnu.bachelor.voicepick.features.authentication.models.User;
import no.ntnu.bachelor.voicepick.features.pluck.models.PluckList;

public interface PluckListRepository extends JpaRepository<PluckList, Long> {

    public List<PluckList> findByUser(User user);

}
