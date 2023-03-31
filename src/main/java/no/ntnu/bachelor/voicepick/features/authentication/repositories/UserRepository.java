package no.ntnu.bachelor.voicepick.features.authentication.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import no.ntnu.bachelor.voicepick.features.authentication.models.User;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);

    Optional<User> findById(String uid);
}
