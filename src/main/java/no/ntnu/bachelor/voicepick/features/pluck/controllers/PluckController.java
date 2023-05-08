package no.ntnu.bachelor.voicepick.features.pluck.controllers;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.features.pluck.dtos.UpdatePluckRequest;
import no.ntnu.bachelor.voicepick.features.pluck.services.PluckService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/plucks")
@RequiredArgsConstructor
public class PluckController {

    private final PluckService pluckService;

    @Operation(summary = "Update a pluck")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pluck updated", content = @Content),
            @ApiResponse(responseCode = "404", description = "Pluck not found", content = @Content)
    })
    @PatchMapping("/{id}")
    public ResponseEntity<String> updatePluck(@PathVariable Long id, @RequestBody UpdatePluckRequest request) {
        ResponseEntity<String> response;
        try {
            this.pluckService.updatePluck(id, request);
            response = new ResponseEntity<>(HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        return response;
    }

}
