package no.ntnu.bachelor.voicepick.features.pluck.controllers;

import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.features.pluck.dtos.CargoCarrierDto;
import no.ntnu.bachelor.voicepick.features.pluck.models.CargoCarrier;
import no.ntnu.bachelor.voicepick.features.pluck.services.CargoCarrierService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cargo-carriers")
@RequiredArgsConstructor
public class CargoCarrierController {

  private final CargoCarrierService service;

  @GetMapping
  public ResponseEntity<List<CargoCarrier>> getCargoCarriers() {
    return new ResponseEntity<>(this.service.findAll(), HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<String> addCargoCarrier(@RequestBody CargoCarrierDto body) {
    this.service.add(new CargoCarrier(body.getName(), body.getIdentifier()));
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
