package no.ntnu.bachelor.voicepick.product;

import no.ntnu.bachelor.voicepick.dtos.AddProductRequest;
import no.ntnu.bachelor.voicepick.models.Location;
import no.ntnu.bachelor.voicepick.services.LocationService;
import no.ntnu.bachelor.voicepick.services.ProductService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import no.ntnu.bachelor.voicepick.models.Product;
import no.ntnu.bachelor.voicepick.models.ProductType;
import no.ntnu.bachelor.voicepick.models.Status;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductTest {

  @Autowired
  private ProductService productService;
  @Autowired
  private LocationService locationService;

  @BeforeEach
  void setup() {
    this.locationService.addLocation("H209", 123);
  }

  @AfterEach
  void teardown() {
    for (Product product : this.productService.getAllProducts()) {
      this.productService.deleteAll(product.getName());
    }
    for (Location location : this.locationService.getAll()) {
      this.locationService.deleteLocation(location.getCode());
    }
  }

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
  @DisplayName("Try to create invalid products")
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

  @Test
  @DisplayName("Try to add an invalid product")
  void addInvalidProduct() {
    try {
      this.productService.addProduct(new AddProductRequest("", "", -1, -1, -1, null));
      fail("Product should not be created");
    } catch (Exception e) {
      assertEquals(0, this.productService.getAvailableProducts().size());
    }

  }

  @Test
  @DisplayName("Add a product without any location")
  void addProductWithoutLocation() {
    var product = new AddProductRequest("Coca Cola", "", 1, 1, 10, ProductType.F_PAK);
    this.productService.addProduct(product);

    var result = this.productService.getProductsWithoutLocation("Coca Cola");
    if (result.isEmpty()) {
      fail("Could not find correct product");
    }
    var productFound = result.get(0);
    assertEquals("Coca Cola", productFound.getName());
    assertNull(productFound.getLocation());
    assertEquals(1, productFound.getWeight());
    assertEquals(1, productFound.getVolume());
    assertEquals(10, productFound.getQuantity());
    assertEquals(ProductType.F_PAK, productFound.getType());
    assertEquals(Status.WITHOUT_LOCATION, productFound.getStatus());
  }


  @Test
  @DisplayName("Add a valid product")
  void addProduct() {
    var locations = this.locationService.getAll();
    this.productService.addProduct(new AddProductRequest("Pepsi", locations.get(0).getCode(), 1, 1, 10, ProductType.F_PAK));

    var result = this.productService.getAvailableProductsByName("Pepsi");
    if (result.isEmpty()) {
      fail("Could not find correct product");
    }
    var productFound = result.get(0);
    assertEquals(1, this.productService.getAvailableProducts().size());
    assertEquals("Pepsi", productFound.getName());
    assertNotNull(productFound.getLocation());
    assertEquals(locations.get(0).getId(), productFound.getLocation().getId());
    assertEquals(locations.get(0).getCode(), productFound.getLocation().getCode());
    assertEquals(locations.get(0).getControlDigits(), productFound.getLocation().getControlDigits());
    assertEquals(1, productFound.getWeight());
    assertEquals(1, productFound.getVolume());
    assertEquals(10, productFound.getQuantity());
    assertEquals(ProductType.F_PAK, productFound.getType());
    assertEquals(Status.READY, productFound.getStatus());
  }

  @Test
  @DisplayName("Delete product")
  void deleteProduct() {
    var locations = this.locationService.getAll();
    this.productService.addProduct(new AddProductRequest("Fanta", locations.get(0).getCode(), 1, 1, 10, ProductType.F_PAK));
    this.productService.deleteAll("Fanta");

    assertEquals(0, this.productService.getAvailableProducts().size());
  }
}