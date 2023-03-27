package no.ntnu.bachelor.voicepick.pluck;

import java.time.LocalDateTime;
import java.util.Objects;

import no.ntnu.bachelor.voicepick.features.pluck.models.CargoCarrier;
import no.ntnu.bachelor.voicepick.models.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import no.ntnu.bachelor.voicepick.models.Product;
import no.ntnu.bachelor.voicepick.models.ProductType;
import no.ntnu.bachelor.voicepick.models.Status;
import no.ntnu.bachelor.voicepick.features.pluck.models.Pluck;
import no.ntnu.bachelor.voicepick.features.pluck.models.PluckList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PluckTests {

  @Test
  @DisplayName("Create a valid pluck list")
  void testCreatingPluckList() {
    // Locations
    var h201 = new Location("H201", 321);
    var m119 = new Location("M119", 875);
    var m200 = new Location("M200", 321);

    // Products
    var melk = new Product("Q-melk", 1.75, 1.75, 50, ProductType.D_PAK, Status.READY);
    var cola = new Product("6-pack Coca Cola", 9, 9, 100, ProductType.D_PAK, Status.READY);

    // Add products to locations
    h201.addEntity(melk);
    m119.addEntity(cola);

    // Create pluck
    var melkPluck = new Pluck(melk, 10, LocalDateTime.now());
    var colaPluck = new Pluck(cola, 10, LocalDateTime.now());

    // Create pluck list
    var pluckList = new PluckList("1234", "Kiwi - Nedre Strandgate");

    // Add location to pluck list
    m200.addEntity(pluckList);

    // Add plucks to pluck list
    pluckList.addPluck(melkPluck);
    pluckList.addPluck(colaPluck);

    assertEquals("1234", pluckList.getRoute());
    assertEquals("Kiwi - Nedre Strandgate", pluckList.getDestination());
    assertEquals(2, pluckList.getPlucks().size());
    assertNull(pluckList.getCargoCarrier());

    pluckList.setCargoCarrier(new CargoCarrier("Halvpall", 2L, "two"));

    assertEquals("Halvpall", pluckList.getCargoCarrier().getName());
    assertEquals(2L, pluckList.getCargoCarrier().getIdentifier());

    var melkResult = pluckList.getPlucks().stream().filter(pluck -> Objects.equals(pluck.getProduct().getName(), "Q-melk"))
        .findFirst();
    assertTrue(melkResult.isPresent());
    assertEquals("H201", melkResult.get().getProduct().getLocation().getCode());
    assertEquals(321, melkResult.get().getProduct().getLocation().getControlDigits());

  }

}
