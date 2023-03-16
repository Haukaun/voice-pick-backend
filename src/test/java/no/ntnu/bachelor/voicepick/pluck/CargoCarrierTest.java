package no.ntnu.bachelor.voicepick.pluck;

import no.ntnu.bachelor.voicepick.features.pluck.models.CargoCarrier;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CargoCarrierTest {

  @Test
  @DisplayName("Test creating an invalid cargo carrier")
  public void createInvalidCargoCarrier() {
    try {
      var cargoCarrier = new CargoCarrier("   ", 23L);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    try {
      var cargoCarrier = new CargoCarrier("Halvpall", -2L);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }
  }

  @Test
  @DisplayName("Try to create a valid cargo carrier")
  public void createValidCargoCarrier() {
    var cargoCarrier = new CargoCarrier("Halvpall", 1L);

    assertEquals("Halvpall", cargoCarrier.getName());
    assertEquals(1, cargoCarrier.getIdentifier());
  }

}
