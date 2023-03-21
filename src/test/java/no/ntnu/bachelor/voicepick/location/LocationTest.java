package no.ntnu.bachelor.voicepick.location;

import no.ntnu.bachelor.voicepick.features.pluck.models.PluckListLocation;
import no.ntnu.bachelor.voicepick.models.ProductLocation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LocationTest {
  
  @Test
  @DisplayName("Create a valid plucklist location")
  void createValidPluckListLocation() {
    var location = new PluckListLocation("H201", 346);

    assertEquals("H201", location.getName());
    assertEquals(346, location.getControlDigit());
  }

  @Test
  @DisplayName("Try to create an invalid plucklist location")
  void createInvalidPluckListLocation() {
    // Invalid location
    try {
      new PluckListLocation("", 346);
      fail();
    } catch (IllegalArgumentException e ) {
      assertTrue(true);
    }

    // Invalid controll digits
    try {
      new PluckListLocation("H201", -1);
      fail();
    } catch (IllegalArgumentException e ) {
      assertTrue(true);
    }
  }


  @Test
  @DisplayName("Create a valid product location")
  void createValidLocation() {
    var location = new ProductLocation("H201", 346);

    assertEquals("H201", location.getName());
    assertEquals(346, location.getControlDigit());
  }

  @Test
  @DisplayName("Try to create an invalid product location")
  void createInvalidLocation() {
    // Invalid location
    try {
      new ProductLocation("", 346);
      fail();
    } catch (IllegalArgumentException e ) {
      assertTrue(true);
    }

    // Invalid controll digits
    try {
      new ProductLocation("H201", -1);
      fail();
    } catch (IllegalArgumentException e ) {
      assertTrue(true);
    }
  }

}
