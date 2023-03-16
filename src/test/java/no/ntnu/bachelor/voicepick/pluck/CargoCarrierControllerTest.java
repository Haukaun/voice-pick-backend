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
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    this.cargoCarrierService.add(new CargoCarrier("Helpall", 1l));

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
            1L
    ));

    // Validation
    var storedCargoCarriers = this.cargoCarrierService.findAll();
    assertEquals(1, storedCargoCarriers.size());

    // Tear down
    this.cargoCarrierService.delete(storedCargoCarriers.get(0));
  }

}
