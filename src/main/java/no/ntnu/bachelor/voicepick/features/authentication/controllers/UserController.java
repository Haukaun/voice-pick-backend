package no.ntnu.bachelor.voicepick.features.authentication.controllers;

import java.util.List;

import no.ntnu.bachelor.voicepick.dtos.EmailDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.features.authentication.models.User;
import no.ntnu.bachelor.voicepick.features.authentication.services.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /*
     * Returns a list of all users
     */
    @GetMapping()
    public ResponseEntity<List<User>> getAllUsers() {
        ResponseEntity<List<User>> response;

        try {
            List<User> users = userService.getAllUsers();
            response = new ResponseEntity<>(users, HttpStatus.OK);
        } catch (IllegalStateException e) {
            response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return response;
    }

    /**
     * Returns a user based on email
     *
     * @param request a dto containing the email
     * @return a user if found
     */
    @GetMapping("/email")
    public ResponseEntity<User> getUserByEmail(@RequestBody EmailDto request) {
        ResponseEntity<User> response;

        var optionalUser = userService.getUserByEmail(request.getEmail());
        response = optionalUser
            .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));

        return response;
    }
}
