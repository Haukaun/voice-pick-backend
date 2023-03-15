package no.ntnu.bachelor.voicepick.location;

import no.ntnu.bachelor.voicepick.models.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LocationTest {
  
  @Test
  @DisplayName("Create a valid location")
  void createValidLocation() {
    var location = new Location("H201", 346);

    assertEquals("H201", location.getName());
    assertEquals(346, location.getControlDigit());
  }

  @Test
  @DisplayName("Try to create an invalid location")
  void createInvalidLocation() {
    // Invalid location
    try {
      new Location("", 346);
      fail();
    } catch (IllegalArgumentException e ) {
      assertTrue(true);
    }

    // Invalid controll digits
    try {
      new Location("H201", -1);
      fail();
    } catch (IllegalArgumentException e ) {
      assertTrue(true);
    }
  }

}
