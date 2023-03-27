package no.ntnu.bachelor.voicepick.product;

import no.ntnu.bachelor.voicepick.models.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import no.ntnu.bachelor.voicepick.models.Product;
import no.ntnu.bachelor.voicepick.models.ProductType;
import no.ntnu.bachelor.voicepick.models.Status;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductTest {

  @Test
  @DisplayName("Create a valid product")
  void createValidProduct() {
    var milk = new Product("Q-milk", 1.75, 1.75, 50, ProductType.D_PAK, Status.READY);
    var h201 = new Location("H201", 321);
    h201.addEntity(milk);

    assertEquals("Q-milk", milk.getName());
    assertEquals("H201", milk.getLocation().getCode());
    assertEquals(321, milk.getLocation().getControlDigits());
    assertEquals(ProductType.D_PAK, milk.getType());
    assertEquals(Status.READY, milk.getStatus());
  }

  @Test
  @DisplayName("Try to add an invalid product")
  void createInvalidProduct() {
    // Invalid name
    try {
      new Product("", 0, 0, -1, ProductType.D_PAK, Status.EMPTY);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    // Invalid weight
    try {
      new Product("Melk", 0, 0, -1, ProductType.D_PAK, Status.EMPTY);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    // Invalid volume
    try {
      new Product("Melk", 1.75, 0, -1, ProductType.D_PAK, Status.EMPTY);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    // Invalid quantity
    try {
      new Product("Melk", 1.75, 1.75, -1, ProductType.D_PAK, Status.EMPTY);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }
  }

}
