package no.ntnu.bachelor.voicepick.pluck;

import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;
import no.ntnu.bachelor.voicepick.features.pluck.models.CargoCarrier;
import no.ntnu.bachelor.voicepick.features.pluck.models.PluckList;
import no.ntnu.bachelor.voicepick.features.pluck.repositories.CargoCarrierRepository;
import no.ntnu.bachelor.voicepick.features.pluck.repositories.PluckListRepository;
import no.ntnu.bachelor.voicepick.features.pluck.services.CargoCarrierService;
import no.ntnu.bachelor.voicepick.features.pluck.services.PluckListService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CargoCarrierTest {

  @Autowired
  private CargoCarrierService cargoCarrierService;
  @Autowired
  private CargoCarrierRepository cargoCarrierRepository;

  @Autowired
  private PluckListService pluckListService;
  @Autowired
  private PluckListRepository pluckListRepository;

  @AfterEach
  void teardown() {
    this.pluckListService.deleteAll();
    this.cargoCarrierRepository.deleteAll();
  }

  @Test
  @DisplayName("Test creating an invalid cargo carrier")
  void createInvalidCargoCarrier() {
    try {
      new CargoCarrier("   ", 23, "twentythree");
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    try {
      new CargoCarrier("Halvpall", -2, "minus two");
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    try {
      new CargoCarrier("Halvpall", 2, "");
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }
  }

  @Test
  @DisplayName("Try to create a valid cargo carrier")
  void createValidCargoCarrier() {
    var cargoCarrier = new CargoCarrier("Halvpall", 1, "one");

    assertEquals("Halvpall", cargoCarrier.getName());
    assertEquals(1, cargoCarrier.getIdentifier());
  }

  @Test
  @DisplayName("Add cargo")
  void addCargo() {
    this.cargoCarrierService.add(new CargoCarrier("carrier1", 1, "one"));

    assertEquals(1, this.cargoCarrierRepository.findAll().size());
  }

  @Test
  @DisplayName("Add cargo that already exists")
  void addCargoTwice() {
    try {
      this.cargoCarrierService.add(new CargoCarrier("carrier1", 1, "one"));
      this.cargoCarrierService.add(new CargoCarrier("carrier1", 1, "one"));
      fail("EntityExistsException should have been thrown, but wasn't");
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test
  @DisplayName("Find all cargoes when there are none")
  void findCargo() {
    assertEquals(0, this.cargoCarrierService.findAll().size());
  }

  @Test
  @DisplayName("Find all cargoes")
  void findAllCargoes() {
    this.cargoCarrierService.add(new CargoCarrier("carrier1", 1, "one"));

    assertEquals(1, this.cargoCarrierService.findAll().size());
  }

  @Test
  @DisplayName("Find all active cargo carrier when there are none")
  void findAllActiveWithoutAny() {
    assertEquals(0, this.cargoCarrierService.findAllActive().size());
  }

  @Test
  @DisplayName("Find all active cargo carriers when there are only inactive")
  void findAllActiveWithOnlyInactive() {
    var cargoCarrier = new CargoCarrier("carrier1", 1, "one");
    this.cargoCarrierService.add(cargoCarrier);
    this.cargoCarrierService.delete(cargoCarrier);

    assertEquals(0, this.cargoCarrierService.findAllActive().size());
  }

  @Test
  @DisplayName("Find all active cargo carrier when there are mixed types")
  void findAllActiveWithMixed() {
    var cargoCarrier = new CargoCarrier("carrier1", 1, "one");
    this.cargoCarrierService.add(cargoCarrier);
    this.cargoCarrierService.add(new CargoCarrier("carrier2", 2, "two"));
    this.cargoCarrierService.delete(cargoCarrier);

    assertEquals(1, this.cargoCarrierService.findAllActive().size());
  }


  @Test
  @DisplayName("Delete cargo")
  void deleteCargo() {
    var cargoCarrier = new CargoCarrier("carrier1", 1, "one");
    this.cargoCarrierService.add(cargoCarrier);

    this.cargoCarrierService.delete(cargoCarrier);

    assertEquals(0, this.cargoCarrierService.findAllActive().size());
  }

  @Test
  @DisplayName("Delete cargo with relation to pluck")
  @Transactional
  void deleteCargoWithRelation() {
    var pluck = new PluckList("route", "destination");
    var cargoCarrier = new CargoCarrier("carrier1", 1, "one");
    cargoCarrier.addToPluckList(pluck);
    this.cargoCarrierService.add(cargoCarrier);
    this.pluckListRepository.save(pluck);

    this.cargoCarrierService.delete(cargoCarrier);

    assertEquals(0, this.cargoCarrierService.findAllActive().size());
  }

}
