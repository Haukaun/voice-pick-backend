package no.ntnu.bachelor.voicepick.location;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import no.ntnu.bachelor.voicepick.dtos.AddProductRequest;
import no.ntnu.bachelor.voicepick.exceptions.EmptyListException;
import no.ntnu.bachelor.voicepick.features.authentication.models.User;
import no.ntnu.bachelor.voicepick.features.authentication.repositories.UserRepository;
import no.ntnu.bachelor.voicepick.features.authentication.services.UserService;
import no.ntnu.bachelor.voicepick.features.pluck.services.PluckListService;
import no.ntnu.bachelor.voicepick.models.Location;
import no.ntnu.bachelor.voicepick.models.ProductType;
import no.ntnu.bachelor.voicepick.models.Status;
import no.ntnu.bachelor.voicepick.repositories.LocationRepository;
import no.ntnu.bachelor.voicepick.repositories.ProductRepository;
import no.ntnu.bachelor.voicepick.services.LocationService;
import no.ntnu.bachelor.voicepick.services.ProductService;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
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

  @AfterEach
  void teardown() {
    this.locationService.deleteAll();
    this.pluckListService.deleteAll();
    this.userService.deleteAll();
    this.productRepository.deleteAll();
  }
  
  @Test
  @DisplayName("Create a valid location")
  void createValidPluckListLocation() {
    var location = new Location("H201", 346);

    assertEquals("H201", location.getCode());
    assertEquals(346, location.getControlDigits());
  }

  @Test
  @DisplayName("Try to create an invalid location")
  void createInvalidPluckListLocation() {
    // Invalid location
    try {
      new Location("", 346);
      fail();
    } catch (IllegalArgumentException e ) {
      assertTrue(true);
    }

    // Invalid control digits
    try {
      new Location("H201", -1);
      fail();
    } catch (IllegalArgumentException e ) {
      assertTrue(true);
    }
  }

  @Test
  @DisplayName("Add location")
  void addLocation() {
    this.locationService.addLocation("H209", 123);

    assertEquals(1, this.locationRepository.findAll().size());
  }

  @Test
  @DisplayName("Get all location when there are none")
  void getAllWithoutAny() {
    assertEquals(0, this.locationService.getAll().size());
  }

  @Test
  @DisplayName("Get all locations")
  void getAll() {
    this.locationService.addLocation("H209", 123);
    this.locationService.addLocation("H215", 321);
    this.productService.addProduct(new AddProductRequest("product1", "H209", 1.0, 1.0, 1, ProductType.D_PAK, Status.READY));

    assertEquals(2, this.locationService.getAll().size());
  }

  @Test
  @DisplayName("Get location by code")
  void getByCode() {
    this.locationService.addLocation("H209", 123);

    assertFalse(this.locationService.getLocationByCode("H215").isPresent());
    assertTrue(this.locationService.getLocationByCode("H209").isPresent());
  }

  @Test
  @DisplayName("Get all product locations")
  @Transactional
  void getAllProductLocations() {
    this.locationService.addLocation("H209", 123);
    this.locationService.addLocation("H215", 123);
    this.locationService.addLocation("H219", 123);

    this.productService.addProduct(new AddProductRequest("product1", "H209", 1.0, 1.0, 1, ProductType.D_PAK, Status.READY));

    var uid = "pjpiaerpg";
    this.userService.createUser(new User(uid, "Hans", "Val", "hans@val.com"));
    try {
      this.pluckListService.generateRandomPluckList(uid);
    } catch (EmptyListException e) {
      fail("Failed to generate random pluck");
    }

    assertEquals(1, this.locationService.getAllProductLocations().size());
  }

  @Test
  @DisplayName("Get all pluck list locations")
  @Transactional
  void getAllPluckListLocations() {
    this.locationService.addLocation("H209", 123);
    this.locationService.addLocation("H215", 123);
    this.locationService.addLocation("H219", 123);

    this.productService.addProduct(new AddProductRequest("product1", "H209", 1.0, 1.0, 1, ProductType.D_PAK, Status.READY));

    var uid = "pjpiaerpg";
    this.userService.createUser(new User(uid, "Hans", "Val", "hans@val.com"));
    try {
      this.pluckListService.generateRandomPluckList(uid);
    } catch (EmptyListException e) {
      fail("Failed to generate random pluck");
    }

    assertEquals(2, this.locationService.getAvailablePluckListLocation().size());
  }

  @Test
  @DisplayName("Get location entities")
  @Transactional
  void getLocationEntities() {
    var uid = "poaejpah";
    this.userService.createUser(new User(uid, "Hand", "Val", "hans@val.com"));
    this.locationService.addLocation("H209", 123);
    this.locationService.addLocation("H215", 123);
    this.productService.addProduct(new AddProductRequest("product1", "H215", 1.0, 1.0, 1, ProductType.D_PAK, Status.READY));
    try {
      this.pluckListService.generateRandomPluckList(uid);
      this.pluckListService.generateRandomPluckList(uid);
    } catch (EmptyListException e) {
      fail("Failed to generate random pluck");
    }
    var location = this.locationService.getLocationByCode("H209");
    if (location.isEmpty()) {
      fail("Failed to fetch location id");
    }

    assertEquals(2, this.locationService.getLocationEntities(location.get().getId()).size());
  }

  @Test
  @DisplayName("Delete location")
  void deleteLocation() {
    this.locationService.addLocation("H209", 123);
    this.locationService.deleteLocation("H209");

    assertEquals(0, this.locationService.getAll().size());
  }

  @Test
  @DisplayName("Delete none existing location")
  void deleteNoneExistingLocation() {
    this.locationService.addLocation("H209", 123);

    try {
      this.locationService.deleteLocation("B123");
      fail("EntityNotFoundException should have been throw, but was not");
    } catch (EntityNotFoundException e) {
      assertTrue(true);
    }
  }

  @Test
  @DisplayName("Delete all location")
  void deleteAllLocations() {
    this.locationService.addLocation("H209", 123);
    this.locationService.addLocation("B123", 123);
    this.locationService.addLocation("H123", 123);
    this.locationService.addLocation("A345", 123);

    this.locationService.deleteAll();

    assertEquals(0, this.locationService.getAll().size());
  }
}
