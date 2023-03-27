package no.ntnu.bachelor.voicepick.pluck;

import no.ntnu.bachelor.voicepick.features.pluck.controllers.CargoCarrierController;
import no.ntnu.bachelor.voicepick.features.pluck.dtos.CargoCarrierDto;
import no.ntnu.bachelor.voicepick.features.pluck.models.CargoCarrier;
import no.ntnu.bachelor.voicepick.features.pluck.services.CargoCarrierService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class CargoCarrierControllerTest {

  @Autowired
  private CargoCarrierController cargoCarrierController;
  @Autowired
  private CargoCarrierService cargoCarrierService;

  @Test
  @DisplayName("Test getting cargo carriers")
  void getCargoCarriers() {
    // Setup
    this.cargoCarrierService.add(new CargoCarrier("Helpall", 1l, "one"));

    // Execution
    var response = this.cargoCarrierController.getCargoCarriers();
    var body = response.getBody();

    // Validation
    assert body != null;
    assertEquals(1, body.size());
    assertEquals("Helpall", body.get(0).getName());
    assertEquals(1L, body.get(0).getIdentifier());

    // Tear down
    this.cargoCarrierService.delete(body.get(0));
  }

  @Test
  @DisplayName("Add cargo carrier")
  void addCargoCarrier() {
    // Execution
    this.cargoCarrierController.addCargoCarrier(new CargoCarrierDto(
            "Helpall",
            1L,
            "one"
    ));

    // Validation
    var storedCargoCarriers = this.cargoCarrierService.findAll();
    assertEquals(1, storedCargoCarriers.size());

    // Tear down
    this.cargoCarrierService.delete(storedCargoCarriers.get(0));
  }

  /*
   * Should not be allowed to add two cargo carriers with same identifier
   */
  @Test
  @DisplayName("Add two cargo carriers with same identifier")
  void addTwoCargoeCarriersWithSameIdentifier() {
    // Execution
    var firstResponse = this.cargoCarrierController.addCargoCarrier(new CargoCarrierDto(
            "Helpall",
            1L,
            "one"
    ));
    var secondResponse = this.cargoCarrierController.addCargoCarrier(new CargoCarrierDto(
            "Halvpall",
            1L,
            "one"
    ));

    // Validation
    assertEquals(HttpStatus.OK, firstResponse.getStatusCode());
    assertEquals(HttpStatus.CONFLICT, secondResponse.getStatusCode());

    // Tear down
    var cargoCarriers = this.cargoCarrierService.findAll();
    this.cargoCarrierService.deleteAll(cargoCarriers);
  }

}
