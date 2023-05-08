package no.ntnu.bachelor.voicepick.features.pluck.controllers;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.features.pluck.dtos.CargoCarrierDto;
import no.ntnu.bachelor.voicepick.features.pluck.models.CargoCarrier;
import no.ntnu.bachelor.voicepick.features.pluck.services.CargoCarrierService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;

@RestController
@RequestMapping("/cargo-carriers")
@RequiredArgsConstructor
public class CargoCarrierController {

  private final CargoCarrierService service;

  @GetMapping
  @Operation(summary = "Get all cargo carriers")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Cargo carriers found", content = @Content),
    @ApiResponse(responseCode = "404", description = "Cargo carriers not found", content = @Content)
  })
  public ResponseEntity<List<CargoCarrier>> getCargoCarriers() {
    return new ResponseEntity<>(this.service.findAllActive(), HttpStatus.OK);
  }

  @PostMapping
  @Operation(summary = "Add a cargo carrier")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Cargo carrier added", content = @Content),
    @ApiResponse(responseCode = "409", description = "Cargo carrier already exists", content = @Content)
  })
  public ResponseEntity<String> addCargoCarrier(@RequestBody CargoCarrierDto body) {

    ResponseEntity<String> response;

    try {
      this.service.add(new CargoCarrier(body.getName(), body.getIdentifier(), body.getPhoneticIdentifier()));
      response = new ResponseEntity<>(HttpStatus.OK);
    } catch (EntityExistsException e) {
      response = new ResponseEntity<>("Cargo carrier with identifier (" + body.getIdentifier() + ") already exists", HttpStatus.CONFLICT);
    }

    return response;
  }
}
