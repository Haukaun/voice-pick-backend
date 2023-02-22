package no.ntnu.bachelor.voicepick.features.pluck.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.exceptions.EmptyListException;
import no.ntnu.bachelor.voicepick.features.pluck.models.PluckList;
import no.ntnu.bachelor.voicepick.features.pluck.services.PluckListService;

@RestController
@RequestMapping("/plucks")
@RequiredArgsConstructor
public class PluckListController {

  private final PluckListService pluckListService;

  /**
   * Returns a randomly generated pluck list
   * 
   * @return {@code 200 OK} if ok, {@code 500 INTERNAL_SERVER_ERROR} if somethings
   *         goes wrong
   */
  @GetMapping
  public ResponseEntity<?> getPluckList() {
    try {
      return new ResponseEntity<PluckList>(this.pluckListService.generateRandomPluckList(), HttpStatus.OK);
    } catch (EmptyListException e) {
      return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}
