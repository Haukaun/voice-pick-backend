package no.ntnu.bachelor.voicepick.pluck;

import lombok.extern.slf4j.Slf4j;
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

import static org.junit.jupiter.api.Assertions.assertEquals;


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

}
