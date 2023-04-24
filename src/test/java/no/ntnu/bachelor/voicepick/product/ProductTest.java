package no.ntnu.bachelor.voicepick.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import no.ntnu.bachelor.voicepick.dtos.AddProductRequest;
import no.ntnu.bachelor.voicepick.dtos.AddWarehouseDto;
import no.ntnu.bachelor.voicepick.features.authentication.models.RoleType;
import no.ntnu.bachelor.voicepick.features.authentication.models.User;
import no.ntnu.bachelor.voicepick.features.authentication.services.UserService;
import no.ntnu.bachelor.voicepick.models.*;
import no.ntnu.bachelor.voicepick.services.LocationService;
import no.ntnu.bachelor.voicepick.services.ProductService;
import no.ntnu.bachelor.voicepick.services.WarehouseService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class ProductTest {

  @Autowired
  private ProductService productService;
  @Autowired
  private LocationService locationService;

  @Autowired
  private UserService userService;

  @Autowired
  private WarehouseService warehouseService;

  private static final String WAREHOUSE_NAME = "test";

  private Warehouse warehouse;

  @BeforeEach
  void setup() {
    var user = new User("123123123", "Test", "Testern", "test@test.test");
    userService.createUser(user);
    warehouseService.createWarehouse(user, new AddWarehouseDto(WAREHOUSE_NAME, "testgata"));
    warehouseService.findByName(WAREHOUSE_NAME).ifPresent(value -> warehouse = value);
    this.locationService.addLocation(new Location("H201", 123, LocationType.PRODUCT), warehouse);
  }

  @AfterEach
  void teardown() {
    for (Product product : this.productService.getAllProducts()) {
      this.productService.deleteAll(product.getName());
    }
    for (Location location : this.locationService.getAll()) {
      this.locationService.deleteLocation(location.getId());
    }
    userService.deleteAll();
    warehouseService.deleteAll();
  }

  @Test
  @DisplayName("Create a valid product")
  void createValidProduct() {
    var milk = new Product("Q-milk", 1.75, 1.75, 50, ProductType.D_PAK, Status.READY);
    var h201 = new Location("H201", 321, LocationType.PRODUCT);
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
      this.productService.addProduct(new AddProductRequest("", "", -1, -1, -1, null), warehouse);
      fail("Product should not be created");
    } catch (Exception e) {
      assertEquals(0, this.productService.getAvailableProducts().size());
    }

  }

  @Test
  @DisplayName("Add a product without any location")
  void addProductWithoutLocation() {
    var product = new AddProductRequest("Coca Cola", "", 1, 1, 10, ProductType.F_PAK);
    this.productService.addProduct(product, warehouse);

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
    this.productService.addProduct(new AddProductRequest("Pepsi", locations.get(0).getCode(), 1, 1, 10, ProductType.F_PAK), warehouse);

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
    this.productService.addProduct(new AddProductRequest("Fanta", locations.get(0).getCode(), 1, 1, 10, ProductType.F_PAK), warehouse);
    this.productService.deleteAll("Fanta");

    assertEquals(0, this.productService.getAvailableProducts().size());
  }
}