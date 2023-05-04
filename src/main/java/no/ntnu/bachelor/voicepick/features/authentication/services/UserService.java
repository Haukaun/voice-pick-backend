package no.ntnu.bachelor.voicepick.features.authentication.services;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.features.authentication.exceptions.UnauthorizedException;
import no.ntnu.bachelor.voicepick.features.authentication.models.Role;
import no.ntnu.bachelor.voicepick.features.authentication.models.RoleType;
import no.ntnu.bachelor.voicepick.features.authentication.repositories.RoleRepository;
import no.ntnu.bachelor.voicepick.features.pluck.models.PluckList;
import no.ntnu.bachelor.voicepick.features.pluck.repositories.PluckListRepository;
import no.ntnu.bachelor.voicepick.models.ProfilePicture;
import no.ntnu.bachelor.voicepick.repositories.ProfilePictureRepository;
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
    private final RoleRepository roleRepository;
    private final ProfilePictureRepository profilePictureRepository;

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

        var optionalRole = this.roleRepository.findByType(RoleType.USER);
        Role role;
        if (optionalRole.isEmpty()) {
            role = new Role(RoleType.USER);
            this.roleRepository.save(role);
        } else {
            role = optionalRole.get();
        }

        user.addRole(role);
        userRepository.save(user);
    }

    /**
     * Adds a role to a user
     *
     * @param uuid id of the user to update
     * @param roleType the type of role to add
     */
    public void addRole(String uuid, RoleType roleType) {
        var optionalUser = this.userRepository.findByUuid(uuid);
        if (optionalUser.isEmpty()) {
            throw new EntityNotFoundException("Could not find user with uuid: " + uuid);
        }

        var optionalRole = this.roleRepository.findByType(roleType);
        Role role;
        if (optionalRole.isEmpty()) {
            role = new Role(roleType);
            this.roleRepository.save(role);
        } else {
            role = optionalRole.get();
        }

        var user = optionalUser.get();
        user.addRole(role);

        this.userRepository.save(user);
    }

    public void removeRole(String uuid, RoleType roleType) {
        var optionalUser = this.userRepository.findByUuid(uuid);
        if (optionalUser.isEmpty()) {
            throw new EntityNotFoundException("Could not find user with uuid: " + uuid);
        }

        var user = optionalUser.get();
        var optionalRole = this.roleRepository.findByType(roleType);

        optionalRole.ifPresent(user::removeRole);
        this.userRepository.save(user);
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
     * Updates the profile picture of a user
     *
     * @param uuid of the user to be updated
     * @param pictureName name of the picture to update
     */
    public void updateProfilePicture(String uuid, String pictureName) {

        var optionalUser = this.userRepository.findByUuid(uuid);
        if (optionalUser.isEmpty()) {
            throw new EntityNotFoundException("Could not find user with id: " + uuid);
        }

        var optionalPicture = this.profilePictureRepository.findByName(pictureName);
        if (optionalPicture.isEmpty()) {
            throw new EntityNotFoundException("Could not find profile picture with name: " + pictureName);
        }

        var user = optionalUser.get();
        var picture = optionalPicture.get();

        picture.addUser(user);

        this.userRepository.save(user);
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
        var user = optionalUser.get();
        List<PluckList> pluckLists = pluckListRepository.findByUser(user);
        for (PluckList pluckList : pluckLists) {
            pluckList.setUser(null);
            pluckListRepository.save(pluckList);
        }
        var warehouse = user.getWarehouse();
        if (warehouse != null) {
            warehouse.removeUser(user);
            warehouseRepository.save(warehouse);
        }
        var profilePicture = user.getProfilePicture();
        if (profilePicture != null) {
            profilePicture.removeUser(user);
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

