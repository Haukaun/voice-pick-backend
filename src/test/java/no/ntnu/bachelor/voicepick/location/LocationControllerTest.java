package no.ntnu.bachelor.voicepick.location;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import no.ntnu.bachelor.voicepick.controllers.ProductLocationController;
import no.ntnu.bachelor.voicepick.dtos.AddLocationRequest;
import no.ntnu.bachelor.voicepick.features.pluck.controllers.PluckListLocationController;
import no.ntnu.bachelor.voicepick.features.pluck.services.PluckListLocationService;
import no.ntnu.bachelor.voicepick.services.ProductLocationService;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
class LocationControllerTest {
  
  @Autowired
  private ProductLocationController productLocationController;
  @Autowired
  private ProductLocationService productLocationService;

  @Autowired 
  private PluckListLocationService pluckListLocationService;

  @Autowired
  private PluckListLocationController pluckListLocationController;

  /**
   * Tries to add a location with negative control digits
   */
  @Test
  @DisplayName("Add productlocation with negative control digits")
  @Order(1)
  void addProductLocationWithoutControlDigits() {
    var response = productLocationController.addProductLocation(new AddLocationRequest("H201", -1));

    assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
  }

  /**
   * Adds a valid location
   */
  @Test
  @DisplayName("Add valid product location")
  @Order(2)
  void addProductLocation() {
    productLocationController.addProductLocation(new AddLocationRequest("H201", 321));

    assertEquals(1, productLocationService.getAll().size());
  }

  /**
   * Tries to add the location that was added in the test above a second time
   */
  @Test
  @DisplayName("Add location that already exists")
  @Order(3)
  void addSameLocationTwice() {
    var response = productLocationController.addProductLocation(new AddLocationRequest("H201", 321));

    assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
  }

  /**
   * Tries to add the location that was added in the test above a second time
   */
  @Test
  @DisplayName("Add pluckListLocation with negative control digits")
  @Order(4)
  void addPluckListLocationWithoutControlDigits() {
    var response = pluckListLocationController.addLocation(new AddLocationRequest("H233", -1));

    assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
  }

  /**
   * Adds a valid pluck list location.
   */
  @Test
  @DisplayName("Add valid pluckList location")
  @Order(5)
  void addPluckListLocation() {
    pluckListLocationController.addLocation(new AddLocationRequest("H201", 321));

    assertEquals(1, pluckListLocationService.getAll().size());
  }
}
