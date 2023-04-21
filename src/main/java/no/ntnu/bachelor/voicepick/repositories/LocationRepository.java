package no.ntnu.bachelor.voicepick.repositories;

import no.ntnu.bachelor.voicepick.models.Location;
import no.ntnu.bachelor.voicepick.models.LocationType;
import no.ntnu.bachelor.voicepick.models.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface LocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findByCodeAndWarehouse(String code, Warehouse warehouse);
    List<Location> findLocationsByWarehouseAndLocationTypeAndEntitiesEmpty(Warehouse warehouse, LocationType locationType);

    Optional<Location> findByCodeAndWarehouseAndLocationType(String code, Warehouse warehouse, LocationType locationType);
    List<Location> findByWarehouseAndLocationType(Warehouse warehouse, LocationType locationType);
    Set<Location> findByWarehouseId(Long id);
    List<Location> findLocationsByLocationType(LocationType locationType);


}
