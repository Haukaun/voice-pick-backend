package no.ntnu.bachelor.voicepick.location;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import no.ntnu.bachelor.voicepick.controllers.LocationController;
import no.ntnu.bachelor.voicepick.dtos.AddLocationRequest;
import no.ntnu.bachelor.voicepick.services.LocationService;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
class LocationControllerTest {
  
  @Autowired
  private LocationController locationController;
  @Autowired
  private LocationService locationService;

  @Test
  @DisplayName("Add location without control digits")
  @Order(1)
  void addLocationWithoutControlDigits() {
    var response = locationController.addLocation(new AddLocationRequest("H201", ""));

    assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
  }

  @Test
  @DisplayName("Add valid location")
  @Order(2)
  void addLocation() {
    locationController.addLocation(new AddLocationRequest("H201", "321"));

    assertEquals(1, locationService.getAll().size());
  }

  /**
   * Tries to add the location that was added in the test above a second time
   */
  @Test
  @DisplayName("Add location that already exists")
  @Order(3)
  void addSameLocationTwice() {
    var response = locationController.addLocation(new AddLocationRequest("H201", "321"));

    assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
  }

}
