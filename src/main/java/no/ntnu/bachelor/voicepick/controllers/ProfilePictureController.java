package no.ntnu.bachelor.voicepick.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.features.authentication.dtos.ProfilePictureDto;
import no.ntnu.bachelor.voicepick.models.ProfilePicture;
import no.ntnu.bachelor.voicepick.services.ProfilePictureService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profile-pictures")
@RequiredArgsConstructor
public class ProfilePictureController {

    private final ProfilePictureService profilePictureService;

    @PostMapping
    @Operation(summary = "Add a new picture")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Picture added", content = @Content),
            @ApiResponse(responseCode = "409", description = "Picture conflict", content = @Content)
    })
    public ResponseEntity<String> addPicture(@RequestBody ProfilePictureDto request) {
        ResponseEntity<String> response;

        try {
            profilePictureService.addProfilePicture(new ProfilePicture(request.getPictureName()));
            response = new ResponseEntity<>(HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            response = new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (EntityExistsException e) {
            response = new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }

        return response;
    }

}
