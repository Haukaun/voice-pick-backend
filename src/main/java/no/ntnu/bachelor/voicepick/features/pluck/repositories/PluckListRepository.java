package no.ntnu.bachelor.voicepick.features.pluck.repositories;

import no.ntnu.bachelor.voicepick.features.authentication.models.User;
import no.ntnu.bachelor.voicepick.models.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

import no.ntnu.bachelor.voicepick.features.pluck.models.PluckList;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PluckListRepository extends JpaRepository<PluckList, Long> {

  List<PluckList> findByUser(User user);
  Optional<PluckList> findByIdAndWarehouse(Long id, Warehouse warehouse);

  @Query("SELECT COUNT(PluckList) FROM PluckList p WHERE p.finishedAt IS NOT NULL AND p.user.uuid = :uuid")
  Integer countCompletedPluckList(@Param("uuid") String uuid);

}
