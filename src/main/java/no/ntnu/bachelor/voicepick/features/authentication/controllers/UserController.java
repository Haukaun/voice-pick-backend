package no.ntnu.bachelor.voicepick.features.authentication.controllers;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import no.ntnu.bachelor.voicepick.dtos.EmailDto;
import no.ntnu.bachelor.voicepick.features.authentication.dtos.ProfilePictureDto;
import no.ntnu.bachelor.voicepick.features.authentication.exceptions.UnauthorizedException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.features.authentication.models.User;
import no.ntnu.bachelor.voicepick.features.authentication.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /*
     * Returns a list of all users
     */
    @GetMapping()
    @Operation(summary = "Get all users")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users found", content = {
            @Content(mediaType = "application/json")
        }),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<List<User>> getAllUsers() {
        ResponseEntity<List<User>> response;

        try {
            List<User> users = userService.getAllUsers();
            response = new ResponseEntity<>(users, HttpStatus.OK);
        } catch (IllegalStateException e) {
            response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (UnauthorizedException e) {
            response = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
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
    @Operation(summary = "Get user by email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found", content = {
            @Content(mediaType = "application/json")
        }),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<User> getUserByEmail(@RequestBody EmailDto request) {
        ResponseEntity<User> response;

        if (!this.userService.getCurrentUser().getEmail().equals(request.getEmail())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        var optionalUser = userService.getUserByEmail(request.getEmail());
        response = optionalUser
            .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        return response;
    }

    @PatchMapping("/{uuid}/profile-picture")
    @Operation(summary = "Update profile picture")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile picture updated", content = {
            @Content(mediaType = "application/json")
        }),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<String> updateProfilePicture(@PathVariable String uuid, @RequestBody ProfilePictureDto request) {
        ResponseEntity<String> response;

        if (!this.userService.getCurrentUser().getUuid().equals(uuid)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            this.userService.updateProfilePicture(uuid, request.getPictureName());
            response = new ResponseEntity<>(HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return response;
    }
}
