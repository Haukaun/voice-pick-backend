package no.ntnu.bachelor.voicepick.location;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import no.ntnu.bachelor.voicepick.dtos.AddProductRequest;
import no.ntnu.bachelor.voicepick.dtos.AddWarehouseDto;
import no.ntnu.bachelor.voicepick.exceptions.EmptyListException;
import no.ntnu.bachelor.voicepick.features.authentication.models.RoleType;
import no.ntnu.bachelor.voicepick.features.authentication.models.User;
import no.ntnu.bachelor.voicepick.features.authentication.services.UserService;
import no.ntnu.bachelor.voicepick.features.pluck.services.PluckListService;
import no.ntnu.bachelor.voicepick.models.*;
import no.ntnu.bachelor.voicepick.repositories.LocationRepository;
import no.ntnu.bachelor.voicepick.repositories.ProductRepository;
import no.ntnu.bachelor.voicepick.services.LocationService;
import no.ntnu.bachelor.voicepick.services.ProductService;
import no.ntnu.bachelor.voicepick.services.WarehouseService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class LocationTest {

  @Autowired
  private LocationService locationService;
  @Autowired
  private LocationRepository locationRepository;

  @Autowired
  private ProductService productService;
  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private PluckListService pluckListService;

  @Autowired
  private UserService userService;

  @Autowired
  private WarehouseService warehouseService;

  private static final String WAREHOUSE_NAME = "test";
  private static final String EMAIL = "test@test.test";
  private static final String UUID = "123123";
  private static final String FIRST_NAME = "test";
  private static final String LAST_NAME = "mann";
  private Warehouse warehouse;

  @BeforeEach
  void setup() {
    User user = new User(UUID, FIRST_NAME, LAST_NAME, EMAIL);
    userService.createUser(user);
    Optional<User> optionalUser = userService.getUserByEmail(EMAIL);
    if (optionalUser.isEmpty()) {
      fail();
    }
    warehouseService.createWarehouse(optionalUser.get(), new AddWarehouseDto(WAREHOUSE_NAME, "testgata"));
    warehouseService.findByName(WAREHOUSE_NAME).ifPresent(value -> warehouse = value);
  }

  @AfterEach
  void teardown() {
    this.locationService.deleteAll();
    this.pluckListService.deleteAll();
    this.productRepository.deleteAll();
    this.warehouseService.deleteAll();
  }

  @Test
  @DisplayName("Create a valid location")
  void createValidPluckListLocation() {
    var location = new Location("H201", 346, LocationType.PLUCK_LIST);

    assertEquals("H201", location.getCode());
    assertEquals(346, location.getControlDigits());
    assertEquals(LocationType.PLUCK_LIST, location.getLocationType());
  }

  @Test
  @DisplayName("Try to create an invalid location")
  void createInvalidPluckListLocation() {
    // Invalid location
    try {
      new Location("", 346, LocationType.PLUCK_LIST);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    // Invalid control digits
    try {
      new Location("H201", -1, LocationType.PLUCK_LIST);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    try {
      new Location("H201", 10, null);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }
  }

  @Test
  @DisplayName("Add location")
  void addLocation() {
    this.locationService.addLocation(new Location("H201", 10, LocationType.PLUCK_LIST), warehouse);

    assertEquals(1, this.locationRepository.findAll().size());
  }

  @Test
  @DisplayName("Add location that already exist")
  void addExistingLocation() {
    this.locationService.addLocation(new Location("H209", 123, LocationType.PLUCK_LIST), warehouse);
    Location location = null;
    try {
      location = new Location("H209", 123, LocationType.PLUCK_LIST);
    } catch (IllegalArgumentException e) {
      fail();
    }
    try {
      this.locationService.addLocation(location, warehouse);
      fail("Exception was not thrown");
    } catch (EntityExistsException e) {
      assertTrue(true);
    }
  }

  @Test
  @DisplayName("Successfully get zero available product locations")
  void successfullyGetZeroAvailableProductLocations() {
    var productLocation = new Location("H209", 123, LocationType.PRODUCT);
    var product = new Product("product1", 1.0, 1.0, 1, ProductType.D_PAK, Status.READY);
    productLocation.addEntity(product);

    var locations = locationService.getAvailableProductLocationsInWarehouse(warehouse);
    assertEquals(0, locations.size());
  }

  @Test
  @DisplayName("Get all location when there are none")
  void getAllWithoutAny() {
    assertEquals(0, this.locationService.getAll().size());
  }

  @Test
  @DisplayName("Get all locations")
  void getAll() {
    this.locationService.addLocation(new Location("H209", 123, LocationType.PLUCK_LIST), warehouse);
    this.locationService.addLocation(new Location("H215", 321, LocationType.PLUCK_LIST), warehouse);
    this.productService.addProduct(new AddProductRequest("product1", "H209", 1.0, 1.0, 1, ProductType.D_PAK), warehouse);

    assertEquals(2, this.locationService.getAll().size());
  }

  @Test
  @DisplayName("Get location by code")
  void getByCode() {
    this.locationService.addLocation(new Location("H209", 123, LocationType.PLUCK_LIST), warehouse);
    assertFalse(this.locationService.getLocationByCodeAndWarehouse("H215", warehouse).isPresent());
    assertTrue(this.locationService.getLocationByCodeAndWarehouse("H209", warehouse).isPresent());
  }

  @Test
  @DisplayName("Get all product locations")
  void getAllProductLocations() {
    this.locationService.addLocation(new Location("H209", 123, LocationType.PRODUCT), warehouse);
    this.locationService.addLocation(new Location("H215", 123, LocationType.PRODUCT), warehouse);
    this.locationService.addLocation(new Location("H219", 123, LocationType.PLUCK_LIST), warehouse);

    this.productService.addProduct(new AddProductRequest("product1", "H209", 1.0, 1.0, 1, ProductType.D_PAK), warehouse);

    try {
      this.pluckListService.generateRandomPluckList(UUID);
    } catch (EmptyListException e) {
      fail("Failed to generate random pluck");
    }

    assertEquals(1, this.locationService.getAvailableProductLocationsInWarehouse(warehouse).size());
  }


  @Test
  @DisplayName("Get all product locations in specific warehouse")
  void getAllProductLocationsInWarehouse() {
    this.locationService.addLocation(new Location("H209", 123, LocationType.PLUCK_LIST), warehouse);
    this.locationService.addLocation(new Location("H215", 123, LocationType.PLUCK_LIST), warehouse);
    this.locationService.addLocation(new Location("H219", 123, LocationType.PLUCK_LIST), warehouse);
    this.locationService.addLocation(new Location("G121", 123, LocationType.PRODUCT), warehouse);

    var locations = locationService.getAvailableProductLocationsInWarehouse(warehouse);

    assertEquals(1, locations.size());
  }

  @Test
  @DisplayName("Get all pluck list locations")
  void getAllPluckListLocations() {
    this.locationService.addLocation(new Location("H209", 123, LocationType.PRODUCT), warehouse);
    this.locationService.addLocation(new Location("H215", 123, LocationType.PLUCK_LIST), warehouse);
    this.locationService.addLocation(new Location("H219", 123, LocationType.PLUCK_LIST), warehouse);

    this.productService.addProduct(new AddProductRequest("product1", "H209", 1.0, 1.0, 1, ProductType.D_PAK), warehouse);

    try {
      this.pluckListService.generateRandomPluckList(UUID);
    } catch (EmptyListException e) {
      fail("Failed to generate random pluck");
    }

    assertEquals(2, this.locationService.getAvailablePluckListLocationsInWarehouse(warehouse).size());
  }

  @Test
  @DisplayName("Get location entities for a location that does not exists")
  void getEntitiesForInvalidLocation() {
    try {
      this.locationService.getLocationEntities(2L);
      fail("Exception was not thrown when it should have been");
    } catch (EntityNotFoundException e) {
      assertTrue(true);
    }
  }

  @Test
  @DisplayName("Get location entities")
  void getLocationEntities() {
    this.locationService.addLocation(new Location("H209", 123, LocationType.PLUCK_LIST), warehouse);
    this.locationService.addLocation(new Location("H200", 123, LocationType.PRODUCT), warehouse);
    this.productService.addProduct(new AddProductRequest("product1", "H200", 1.0, 1.0, 1, ProductType.D_PAK), warehouse);

    try {
      this.pluckListService.generateRandomPluckList(UUID);
      this.pluckListService.generateRandomPluckList(UUID);
    } catch (EmptyListException e) {
      fail("Failed to generate random pluck");
    }
    var location = this.locationService.getLocationByCodeAndWarehouse("H209", warehouse);
    if (location.isEmpty()) {
      fail("Failed to fetch location id");
    }

    assertEquals(2, this.locationService.getLocationEntities(location.get().getId()).size());
  }

  @Test
  @DisplayName("Delete location")
  void deleteLocation() {
    this.locationService.addLocation(new Location("H209", 123, LocationType.PLUCK_LIST), warehouse);
    this.locationService.getLocationByCodeAndWarehouse("H209", warehouse).ifPresent(location -> this.locationService.deleteLocation(location.getId()));
    assertEquals(0, this.locationService.getAll().size());
  }

  @Test
  @DisplayName("Delete none existing location")
  void deleteNoneExistingLocation() {
    this.locationService.addLocation(new Location("H200", 123, LocationType.PLUCK_LIST), warehouse);
    try {
      locationService.deleteLocation(123123123123L);
      fail("EntityNotFoundException should have been throw, but was not");
    } catch (EntityNotFoundException e) {
      assertTrue(true);
    }
  }

  @Test
  @DisplayName("Delete all location")
  void deleteAllLocations() {
    this.locationService.addLocation(new Location("H209", 123, LocationType.PLUCK_LIST), warehouse);
    this.locationService.addLocation(new Location("B123", 123, LocationType.PLUCK_LIST), warehouse);
    this.locationService.addLocation(new Location("H123", 123, LocationType.PLUCK_LIST), warehouse);
    this.locationService.addLocation(new Location("A345", 123, LocationType.PLUCK_LIST), warehouse);

    this.locationService.deleteAll();

    assertEquals(0, this.locationService.getAll().size());
  }
}
