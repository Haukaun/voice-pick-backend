package no.ntnu.bachelor.voicepick.repositories;

import no.ntnu.bachelor.voicepick.models.ProfilePicture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfilePictureRepository extends JpaRepository<ProfilePicture, Long> {

    Optional<ProfilePicture> findByName(String name);

}
