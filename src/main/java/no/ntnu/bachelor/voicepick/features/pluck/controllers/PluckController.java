package no.ntnu.bachelor.voicepick.features.pluck.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.exceptions.EmptyListException;
import no.ntnu.bachelor.voicepick.features.pluck.models.PluckList;
import no.ntnu.bachelor.voicepick.features.pluck.services.PluckService;

@RestController
@RequestMapping("/plucks")
@RequiredArgsConstructor
public class PluckController {

  private final PluckService pluckService;

  @GetMapping
  public ResponseEntity<?> getPluck() {
    try {
      return new ResponseEntity<PluckList>(this.pluckService.generateRandomPluckList(), HttpStatus.OK);
    } catch (EmptyListException e) {
      return new ResponseEntity<String>(e.getMessage(), HttpStatus.METHOD_NOT_ALLOWED);
    }
  }

}
