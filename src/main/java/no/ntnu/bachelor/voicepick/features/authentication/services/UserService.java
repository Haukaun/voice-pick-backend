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

    /*
     * Creates a new user
     */
    public User createUser(User user) {
        // Check if the user already exists in the application database
    if (userRepository.findByEmail(user.getEmail()).isPresent()) {
        throw new IllegalStateException("User already exists");
    }
    // Save the user in the application database
    return userRepository.save(user);
    }

    /*
     * Returns a user with the given email
     */
    public User getUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            throw new IllegalArgumentException("User does not exist");
        }
        return user.get();
    }

    /*
     * Returns a user with the given id
     */
    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    /*
     * Returns a list of all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /*
     * Deletes a user with the given id
     */
    public void deleteUser(String id) {
        boolean exists = userRepository.existsById(id);
        if (!exists) {
            throw new IllegalStateException("User with id " + id + " does not exist");
        }
        userRepository.deleteById(id);
    }
}
