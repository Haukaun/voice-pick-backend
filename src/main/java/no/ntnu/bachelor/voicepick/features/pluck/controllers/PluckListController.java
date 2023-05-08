package no.ntnu.bachelor.voicepick.features.pluck.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.EntityNotFoundException;
import no.ntnu.bachelor.voicepick.features.authentication.utils.JwtUtil;
import no.ntnu.bachelor.voicepick.features.pluck.dtos.PluckListDto;
import no.ntnu.bachelor.voicepick.features.pluck.dtos.UpdatePluckListRequest;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/pluck-lists")
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
  @Operation(summary = "Get a random pluck list")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Pluck list found", content = @Content),
    @ApiResponse(responseCode = "204", description = "No products available to make a pluck list", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(responseCode = "422", description = "Unprocessable entity", content = @Content)
  })
  public ResponseEntity<PluckListDto> getRandomPluckList(@RequestHeader(name = "Authorization") Optional<String> token) {
    if (token.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    try {
      String uid = jwt.getUid(token.get().substring(7)); // Remove 'Bearer '

      return new ResponseEntity<>(pluckListMapper.toPluckListDto(this.pluckListService.generateRandomPluckList(uid)), HttpStatus.OK);
    } catch (EmptyListException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } catch (JsonProcessingException e) {
      return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
    }
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get a pluck list by id")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Pluck list found", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = PluckListDto.class))
    }),
    @ApiResponse(responseCode = "400", description = "Pluck list not found", content = @Content)
  })
  public ResponseEntity<PluckList> getPluckListById(@PathVariable Long id) {
    ResponseEntity<PluckList> response;

    var pluckListOpt = this.pluckListService.findById(id);
    response = pluckListOpt.map(pluckList -> new ResponseEntity<>(pluckList, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));

    return response;
  }

  /**
   * Update a pluck list
   *
   * @param id of the pluck list to update
   * @param request containing information of field of the pluck list to update with the updated values
   * @return {@code 200 OK} if successful, {@code 400 BAD REQUEST} if not
   */
  @PatchMapping("/{id}")
  @Operation(summary = "Update a pluck list")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Pluck list updated", content = @Content),
    @ApiResponse(responseCode = "400", description = "Pluck list not found", content = @Content)
  })
  public ResponseEntity<String> updatePluckList(@PathVariable Long id, @RequestBody UpdatePluckListRequest request) {
    ResponseEntity<String> response;

    try {
      this.pluckListService.updatePluckList(id, request);
      response = new ResponseEntity<>(HttpStatus.OK);
    } catch (EntityNotFoundException e) {
      response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    return response;
  }

  @PatchMapping("/{pluckId}/cargo-carriers/{cargoIdentifier}")
  @Operation(summary = "Change cargo carrier of a pluck")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Cargo carrier changed", content = @Content),
    @ApiResponse(responseCode = "404", description = "Pluck not found", content = @Content)
  })
  public ResponseEntity<String> changeCargoCarrier(@PathVariable Long pluckId, @PathVariable int cargoIdentifier) {
    ResponseEntity<String> response;

    try {
      this.pluckListService.updateCargoCarrier(pluckId, cargoIdentifier);
      response = new ResponseEntity<>(HttpStatus.OK);
    } catch (EntityNotFoundException e) {
      response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    return response;
  }

  @GetMapping("/users/{uuid}")
  @Operation(summary = "Get number of completed pluck lists for a user")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Number of completed pluck lists found", content = @Content),
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
  })
  public ResponseEntity<Integer> getNumberOfCompletedPlucks(@PathVariable String uuid) {
     return new ResponseEntity<>(this.pluckListService.getNumberOfCompletedPluckLists(uuid), HttpStatus.OK);
  }
}
