package no.ntnu.bachelor.voicepick.services;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.models.Location;
import no.ntnu.bachelor.voicepick.models.LocationEntity;
import no.ntnu.bachelor.voicepick.repositories.LocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class LocationService {

    private final LocationRepository locationRepository;

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
     * Returns a list of all locations associated with a pluck list
     *
     * @return a list of all locations associated with a pluck list
     */
    public List<Location> getAllPluckListLocations() {
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
            throw new EntityExistsException("Location with code " + code + "already exists");
        }

        this.locationRepository.save(new Location(code, controlDigits));
    }

    public void deleteLocation(String code) {
        var optionalLocation = this.locationRepository.findByCode(code);
        if (optionalLocation.isEmpty()) {
            throw new EntityNotFoundException("Could not find location with code: " + code);
        }
        this.locationRepository.deleteById(optionalLocation.get().getId());
    }

}
