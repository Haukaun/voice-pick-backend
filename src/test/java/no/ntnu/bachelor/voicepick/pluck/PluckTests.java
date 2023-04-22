package no.ntnu.bachelor.voicepick.pluck;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import no.ntnu.bachelor.voicepick.features.pluck.dtos.UpdatePluckRequest;
import no.ntnu.bachelor.voicepick.features.pluck.models.Pluck;
import no.ntnu.bachelor.voicepick.features.pluck.repositories.PluckRepository;
import no.ntnu.bachelor.voicepick.features.pluck.services.PluckService;
import no.ntnu.bachelor.voicepick.models.Product;
import no.ntnu.bachelor.voicepick.models.ProductType;
import no.ntnu.bachelor.voicepick.models.Status;
import no.ntnu.bachelor.voicepick.repositories.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.aspectj.bridge.MessageUtil.fail;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Slf4j
class PluckTests {

  @Autowired
  private PluckService pluckService;
  @Autowired
  private PluckRepository pluckRepository;

  @Autowired
  private ProductRepository productRepository;

  @BeforeEach
  void setup() {
    this.productRepository.save(new Product("product1", 1.0, 1.0, 10, ProductType.D_PAK, Status.READY));
  }

  @AfterEach
  void teardown() {
    this.pluckRepository.deleteAll();
    this.productRepository.deleteAll();
  }

  @Test
  @DisplayName("Save pluck")
  void savePluck() {
    var product = this.productRepository.findAll().get(0);
    this.pluckService.savePluck(new Pluck(product, 10, LocalDateTime.now()));

    assertEquals(1, this.pluckRepository.findAll().size());
  }

  @Test
  @DisplayName("Update pluck that does not exits")
  void updateNonExistingPluck() {
    var now = LocalDateTime.now();
    try {
      this.pluckService.updatePluck(1L, new UpdatePluckRequest(10, now, now));
      fail("No exception was thrown");
    } catch (EntityNotFoundException e) {
      assertTrue(true);
    }
  }

  @Test
  @DisplayName("Update pluck")
  void updatePluck() {
    var product = this.productRepository.findAll().get(0);
    this.pluckService.savePluck(new Pluck(product, 10, LocalDateTime.now()));
    var pluck = this.pluckRepository.findAll().get(0);

    var now = LocalDateTime.now();
    this.pluckService.updatePluck(pluck.getId(), new UpdatePluckRequest(pluck.getAmount(), now, now));

    var optionalUpdatedPluck = this.pluckRepository.findById(pluck.getId());
    if (optionalUpdatedPluck.isEmpty()) {
      fail("Failed to fetch pluck after updating");
    } else {
      var updatedPluck = optionalUpdatedPluck.get();
      assertEquals(pluck.getAmount(), updatedPluck.getAmountPlucked());
      assertNotNull(updatedPluck.getConfirmedAt());
      assertNotNull(updatedPluck.getPluckedAt());
    }
  }

}
