package no.ntnu.bachelor.voicepick.features.pluck.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.EntityNotFoundException;
import no.ntnu.bachelor.voicepick.features.authentication.utils.JwtUtil;
import no.ntnu.bachelor.voicepick.features.pluck.dtos.CargoCarrierDto;
import no.ntnu.bachelor.voicepick.features.pluck.dtos.PluckListDto;
import no.ntnu.bachelor.voicepick.features.pluck.models.PluckList;
import no.ntnu.bachelor.voicepick.features.pluck.mappers.PluckListMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.exceptions.EmptyListException;
import no.ntnu.bachelor.voicepick.features.pluck.services.PluckListService;

import java.util.Optional;

@RestController
@RequestMapping("/plucks")
@RequiredArgsConstructor
public class PluckListController {

  private final JwtUtil jwt;
  private final PluckListService pluckListService;
  private final PluckListMapper pluckListMapper = Mappers.getMapper(PluckListMapper.class);

  /**
   * Returns a randomly generated pluck list
   * 
   * @return {@code 200 OK} if ok,
   * {@code 500 INTERNAL_SERVER_ERROR} if something goes wrong,
   * {@code 204} if there are no products available to make a pluck list
   */
  @GetMapping
  public ResponseEntity<PluckListDto> getRandomPluckList(@RequestHeader(name = "Authorization") Optional<String> token) {
    if (token.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    try {
      String uid = jwt.getUid(token.get().substring(7)); // Remove 'Bearer '

      return new ResponseEntity<>(pluckListMapper.toPluckListDto(this.pluckListService.generateRandomPluckList(uid)), HttpStatus.OK);
    } catch (EmptyListException e) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (JsonProcessingException e) {
      return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
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
