package no.ntnu.bachelor.voicepick.services;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.exceptions.IllegalEntityException;
import no.ntnu.bachelor.voicepick.features.pluck.models.PluckList;
import no.ntnu.bachelor.voicepick.features.pluck.repositories.PluckListRepository;
import no.ntnu.bachelor.voicepick.models.*;
import no.ntnu.bachelor.voicepick.repositories.LocationRepository;
import no.ntnu.bachelor.voicepick.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final ProductRepository productRepository;
    private final PluckListRepository pluckListRepository;

    public List<Location> getAll() {
        return this.locationRepository.findAll();
    }

    /**
     * Returns a location based on the location code given
     *
     * @param code, the location code of the location. Typically like {@code H09}
     * @return an optional with the location found. If no location was found,
     * an empty optional is returned
     */
    public Optional<Location> getLocationByCodeAndWarehouse(String code, Warehouse warehouse) {
        return this.locationRepository.findByCodeAndWarehouse(code, warehouse);
    }

    public Optional<Location> getLocationByCodeAndWarehouseAndLocationType(String code, Warehouse warehouse, LocationType locationType) {
        return this.locationRepository.findByCodeAndWarehouseAndLocationType(code, warehouse, locationType);
    }

    /**
     * Returns a list of all locations associated with a product
     *
     * @return a list of all locations associated with a product
     */
    public List<Location> getAllProductLocations() {
        return this.locationRepository.findLocationsByLocationType(LocationType.PRODUCT);
    }

    public List<Location> getAvailableProductLocationsInWarehouse(Warehouse warehouse) {
        return this.locationRepository.findLocationsByWarehouseAndLocationTypeAndEntitiesEmpty(warehouse, LocationType.PRODUCT);
    }

    public List<Location> getAvailablePluckListLocationsInWarehouse(Warehouse warehouse) {
        return this.locationRepository.findByWarehouseAndLocationType(warehouse, LocationType.PLUCK_LIST);
    }

    /**
     * Returns a set of all entities stored at a specific location
     *
     * @param id of the location
     * @return a set of all entities stored at the location with the id provided
     */
    public Set<LocationEntity> getLocationEntities(Long id) {
        var optionalLocation = this.locationRepository.findById(id);
        if (optionalLocation.isEmpty()) {
            throw new EntityNotFoundException("Could not find location with id: " + id);
        }

        return optionalLocation.get().getEntities();
    }

    /**
     * Adds a location to the repository
     *
     * @param location to add
     * @param warehouse the location should be added to
     */
    public void addLocation(Location location, Warehouse warehouse) {
        if (warehouse == null) {
            throw new EntityNotFoundException("Could not create location because user does not belong to a warehouse");
        }

        var optionalLocation = this.locationRepository.findByCodeAndWarehouse(location.getCode(), warehouse);
        if (optionalLocation.isPresent()) {
            throw new EntityExistsException("Location with code (" + location.getCode() + ") already exists");
        }
        warehouse.addLocation(location);
        this.locationRepository.save(location);
    }

    /**
     * Deletes all locations with the code given from the repository
     *
     * @param id of the locations to delete
     */
    public void deleteLocation(Long id) {
        Optional<Location> optionalLocation = locationRepository.findById(id);
        if (optionalLocation.isEmpty()) {
            throw new EntityNotFoundException("Location with code: " + id + " was not found.");
        }

        var location = optionalLocation.get();

        location.removeWarehouse();
        this.clearLocationEntities(location);

        this.locationRepository.delete(location);
    }

    /**
     * Clears all relation to a location
     *
     * @param location to clear for relations
     */
    private void clearLocationEntities(Location location) {
        var entities = location.getEntities();
        for (var entity : entities) {
            entity.setLocation(null);
            if (entity instanceof Product product) {
                productRepository.save(product);
            } else if (entity instanceof PluckList pluckList) {
                pluckListRepository.save(pluckList);
            } else {
                throw new IllegalEntityException("Could not clear location as it is mapped to an entity of type: " + entity.getClass());
            }
        }
    }

    /**
     * Deletes all location stored in the repository
     */
    public void deleteAll() {
        this.getAll().forEach(location -> this.deleteLocation(location.getId()));
    }

}
