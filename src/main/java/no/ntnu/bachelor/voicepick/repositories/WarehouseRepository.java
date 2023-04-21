package no.ntnu.bachelor.voicepick.repositories;

import no.ntnu.bachelor.voicepick.features.authentication.models.User;
import no.ntnu.bachelor.voicepick.models.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
  Optional<Warehouse> findWarehouseByUsersContaining(User user);
  Optional<Warehouse> findByName(String name);

}
