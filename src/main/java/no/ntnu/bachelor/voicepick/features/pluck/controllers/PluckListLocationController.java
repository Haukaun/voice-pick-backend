package no.ntnu.bachelor.voicepick.features.pluck.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.dtos.AddLocationRequest;
import no.ntnu.bachelor.voicepick.features.pluck.services.PluckListLocationService;

@RestController
@RequestMapping("/plucklistlocations")
@RequiredArgsConstructor
public class PluckListLocationController {
    
    private final PluckListLocationService pluckListLocationService;


    @PostMapping
    public ResponseEntity<String> addLocation(@RequestBody AddLocationRequest location) {
        ResponseEntity<String> response;
        try {
            this.pluckListLocationService.addLocation(location);
            response = new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException | EntityExistsException e) {
            response = new ResponseEntity<>(e.getMessage(), HttpStatus.METHOD_NOT_ALLOWED);
        }
        return response;
    }
}
