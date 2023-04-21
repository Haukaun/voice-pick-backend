package no.ntnu.bachelor.voicepick.features.authentication.services;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.features.authentication.exceptions.UnauthorizedException;
import no.ntnu.bachelor.voicepick.features.pluck.models.PluckList;
import no.ntnu.bachelor.voicepick.features.pluck.repositories.PluckListRepository;
import no.ntnu.bachelor.voicepick.repositories.WarehouseRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import no.ntnu.bachelor.voicepick.features.authentication.models.User;
import no.ntnu.bachelor.voicepick.features.authentication.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PluckListRepository pluckListRepository;
    private final WarehouseRepository warehouseRepository;

    /**
     * Saves a user to the repository
     *
     * @param user to save
     * @throws EntityExistsException if user already exists
     */
    public void createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new EntityExistsException("User with uid (" + user.getId() + ") already exists.");
        }
        userRepository.save(user);
    }

    /**
     * Returns the current user authenticated
     *
     * @return current user from security context
     * @throws EntityNotFoundException if no user with the uuid is found in the database.
     * @throws UnauthorizedException if auth is null.
     */
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            return userRepository.findByUuid(auth.getName()).orElseThrow(EntityNotFoundException::new);
        } else {
            throw new UnauthorizedException("Auth is null.");
        }
    }

    /**
     * Returns a used based on id
     *
     * @param id of the user to find
     * @return an empty optional if no user is found, or the user if found
     */
    public Optional<User> getUserByUuid(String id) {
        return userRepository.findByUuid(id);
    }

    /**
     * Returns all users in the repository
     *
     * @return list of all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Deletes a user based on id
     *
     * @param id of the user to delete
     * @throws EntityNotFoundException if user with given uuid is not found.
     */
    public void deleteUser(String id) {
        Optional<User> optionalUser = getUserByUuid(id);
        if (optionalUser.isEmpty()) {
            throw new EntityNotFoundException("User with id (" + id + ") can't be deleted because it does not exist.");
        }
        List<PluckList> pluckLists = pluckListRepository.findByUser(optionalUser.get());
        for (PluckList pluckList : pluckLists) {
            pluckList.setUser(null);
            pluckListRepository.save(pluckList);
        }
        var warehouse = optionalUser.get().getWarehouse();
        if (warehouse != null) {
            warehouse.removeUser(optionalUser.get());
            warehouseRepository.save(warehouse);
        }
        userRepository.deleteByUuid(id);
    }



    /**
     * Deletes all user stored in the repository
     */
    public void deleteAll() {
        var users = this.userRepository.findAll();
        users.forEach(user -> this.deleteUser(user.getUuid()));
    }

    /**
     * Returns a user based on mail
     *
     * @param email of the user to search for
     * @return empty optional if no user is found, or a user if found
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}

