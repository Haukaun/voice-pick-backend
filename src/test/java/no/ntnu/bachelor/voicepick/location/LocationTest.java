package no.ntnu.bachelor.voicepick.location;

import no.ntnu.bachelor.voicepick.models.ProductLocation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class LocationTest {
  
  @Test
  @DisplayName("Create a valid location")
  void createValidLocation() {
    var location = new ProductLocation("H201", "346");

    assertEquals(location.getLocation(), "H201");
    assertEquals(location.getControlDigit(), "346");
  }

  @Test
  @DisplayName("Try to create an invalid location")
  void createInvalidLocation() {
    // Invalid location
    try {
      new ProductLocation("", "346");
      fail();
    } catch (IllegalArgumentException e ) {
      assertTrue(true);
    }

    // Invalid controll digits
    try {
      new ProductLocation("H201", "");
      fail();
    } catch (IllegalArgumentException e ) {
      assertTrue(true);
    }
  }

}
