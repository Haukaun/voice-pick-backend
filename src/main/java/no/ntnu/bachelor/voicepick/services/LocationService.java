package no.ntnu.bachelor.voicepick.services;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.exceptions.IllegalEntityException;
import no.ntnu.bachelor.voicepick.features.pluck.models.PluckList;
import no.ntnu.bachelor.voicepick.features.pluck.repositories.PluckListRepository;
import no.ntnu.bachelor.voicepick.models.Location;
import no.ntnu.bachelor.voicepick.models.LocationEntity;
import no.ntnu.bachelor.voicepick.models.Product;
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
    public Optional<Location> getLocationByCode(String code) {
        return this.locationRepository.findByCode(code);
    }

    /**
     * Returns a list of all locations associated with a product
     *
     * @return a list of all locations associated with a product
     */
    public List<Location> getAllProductLocations() {
        return this.locationRepository.findByProduct();
    }

    /**
     * Returns all locations that is associated with a pluck list
     * plus all locations that has no association (aka locations that are empty)
     *
     * @return a list of all locations associated with a pluck list
     */
    public List<Location> getAvailablePluckListLocation() {
        return this.locationRepository.findByPluckList();
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
     * @param code of the location
     * @param controlDigits of the location
     */
    public void addLocation(String code, int controlDigits) {
        var optionalLocation = this.locationRepository.findByCode(code);
        if (optionalLocation.isPresent()) {
            throw new EntityExistsException("Location with code (" + code + ") already exists");
        }
        this.locationRepository.save(new Location(code, controlDigits));
    }

    /**
     * Deletes all locations with the code given from the repository
     *
     * @param code of the locations to delete
     */
    public void deleteLocation(String code) {
        Optional<Location> optionalLocation = locationRepository.findByCode(code);
        if (optionalLocation.isEmpty()) {
            throw new EntityNotFoundException("Location with code: " + code + " was not found.");
        }

        var location = optionalLocation.get();
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
        this.getAll().forEach(location -> this.deleteLocation(location.getCode()));
    }

}
