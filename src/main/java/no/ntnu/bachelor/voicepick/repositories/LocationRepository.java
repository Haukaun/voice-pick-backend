package no.ntnu.bachelor.voicepick.repositories;

import no.ntnu.bachelor.voicepick.models.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findByCode(String code);

    @Query("SELECT DISTINCT l FROM Location l LEFT JOIN l.entities e WHERE e.id IS NULL " +
            "UNION " +
            "SELECT DISTINCT l FROM Location l JOIN l.entities e WHERE TYPE(e) = PluckList")
    List<Location> findByPluckList();

    @Query("SELECT DISTINCT l FROM Location l LEFT JOIN l.entities e WHERE e.id IS NULL " +
            "UNION " +
            "SELECT DISTINCT l FROM Location l JOIN l.entities e WHERE TYPE(e) = Product")
    List<Location> findByProduct();

    void deleteById(String code);

}
