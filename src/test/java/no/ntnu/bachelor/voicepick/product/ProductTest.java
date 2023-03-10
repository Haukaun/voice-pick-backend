package no.ntnu.bachelor.voicepick.product;

import no.ntnu.bachelor.voicepick.models.ProductLocation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import no.ntnu.bachelor.voicepick.models.Product;
import no.ntnu.bachelor.voicepick.models.ProductType;
import no.ntnu.bachelor.voicepick.models.Status;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProductTest {

  @Test
  @DisplayName("Create a valid product")
  void createValidProduct() {
    var h201 = new ProductLocation("H201", "321");
    var melk = new Product("Q-melk", h201, 1.75, 1.75, 50, ProductType.D_PAK, Status.READY);

    assertEquals(melk.getName(), "Q-melk");
    assertEquals(melk.getLocation().getLocation(), "H201");
    assertEquals(melk.getLocation().getControlDigit(), "321");
    assertEquals(melk.getType(), ProductType.D_PAK);
    assertEquals(melk.getStatus(), Status.READY);
  }

  @Test
  @DisplayName("Try to add an invalid product")
  void createInvalidProduct() {
    var location = new ProductLocation("H201", "321");
    // Invalid name
    try {
      new Product("", location, 0, 0, -1, ProductType.D_PAK, Status.EMPTY);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    // Invalid weight
    try {
      new Product("Melk", location, 0, 0, -1, ProductType.D_PAK, Status.EMPTY);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    // Invalid volume
    try {
      new Product("Melk", location, 1.75, 0, -1, ProductType.D_PAK, Status.EMPTY);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    // Invalid quantity
    try {
      new Product("Melk", location, 1.75, 1.75, -1, ProductType.D_PAK, Status.EMPTY);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }
  }

}
