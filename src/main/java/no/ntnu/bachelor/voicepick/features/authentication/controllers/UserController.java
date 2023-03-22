package no.ntnu.bachelor.voicepick.features.authentication.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.features.authentication.models.User;
import no.ntnu.bachelor.voicepick.features.authentication.services.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /*
     * Creates a new user
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

    }

    /*
     * Returns a list of all users
     */
    @GetMapping()
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } 
    }


    //Get user details by email
    @GetMapping("/email")
    public ResponseEntity<User> getUserByEmail(String email) {
        try {
            User user = userService.getUserByEmail(email);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } 
    }
    


}
