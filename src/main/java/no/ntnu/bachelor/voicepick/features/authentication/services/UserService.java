package no.ntnu.bachelor.voicepick.features.authentication.services;


import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import no.ntnu.bachelor.voicepick.features.authentication.models.User;
import no.ntnu.bachelor.voicepick.features.authentication.repositories.UserRepository;

@Service
public class UserService {
    
    private final UserRepository userRepository;

    /*
     * Constructor
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Creates a new user
     *
     * @param user to be added
     */
    public void createUser(User user) {
        // Check if the user already exists in the application database
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalStateException("User already exists");
        }
        // Save the user in the application database
        userRepository.save(user);
    }

    /**
     * Returns a user with the given email
     *
     * @param email of the user to find
     * @return an optional with the user if found, if not the optional is empty
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Returns a user with the given id
     *
     * @param id of the user to find
     * @return an optional with the user if found, if not the optional is empty
     */
    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    /**
     * Returns a list of all users
     *
     * @return a list of user
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Deletes a user with the given id
     *
     * @param id of the user to delete
     */
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    /**
     * Deletes a user based on email
     *
     * @param email of the user to delete
     */
    public void deleteUserByEmail(String email) {
        this.getUserByEmail(email).ifPresent(this.userRepository::delete);
    }
}
