package no.ntnu.bachelor.voicepick.features.pluck.controllers;

import jakarta.persistence.EntityNotFoundException;
import no.ntnu.bachelor.voicepick.features.pluck.dtos.CargoCarrierDto;
import no.ntnu.bachelor.voicepick.features.pluck.models.PluckList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.exceptions.EmptyListException;
import no.ntnu.bachelor.voicepick.features.pluck.services.PluckListService;

@RestController
@RequestMapping("/plucks")
@RequiredArgsConstructor
public class PluckListController {

  private final PluckListService pluckListService;

  /**
   * Returns a randomly generated pluck list
   * 
   * @return {@code 200 OK} if ok, {@code 500 INTERNAL_SERVER_ERROR} if something goes wrong
   */
  @GetMapping
  public ResponseEntity<PluckList> getRandomPluckList(@RequestHeader("Authorization") String token) {
    try {
      return new ResponseEntity<>(this.pluckListService.generateRandomPluckList(token), HttpStatus.OK);
    } catch (EmptyListException e) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (EntityNotFoundException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } catch (JsonProcessingException e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<PluckList> getPluckListById(@PathVariable Long id) {
    ResponseEntity<PluckList> response;

    var pluckListOpt = this.pluckListService.findById(id);
    response = pluckListOpt.map(pluckList -> new ResponseEntity<>(pluckList, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));

    return response;
  }

  @PutMapping("/{id}")
  public ResponseEntity<String> updateCargoCarrier(@PathVariable Long id, @RequestBody CargoCarrierDto requestBody) {
    ResponseEntity<String> response;

    try {
      this.pluckListService.updateCargoCarrier(id, requestBody.getIdentifier());
      response = new ResponseEntity<>(HttpStatus.OK);
    } catch (EntityNotFoundException e) {
      response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    return response;
  }

}
