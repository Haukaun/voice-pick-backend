package no.ntnu.bachelor.voicepick.pluck;

import no.ntnu.bachelor.voicepick.dtos.AddLocationRequest;
import no.ntnu.bachelor.voicepick.dtos.AddProductRequest;
import no.ntnu.bachelor.voicepick.features.pluck.controllers.PluckListController;
import no.ntnu.bachelor.voicepick.features.pluck.models.PluckList;
import no.ntnu.bachelor.voicepick.models.ProductType;
import no.ntnu.bachelor.voicepick.models.Status;
import no.ntnu.bachelor.voicepick.services.LocationService;
import no.ntnu.bachelor.voicepick.services.ProductService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
class PluckListControllerTest {

  @Autowired
  private PluckListController pluckListController;
  @Autowired
  private LocationService locationService;
  @Autowired
  private ProductService productService;

  /**
   * Tries to fetch a pluck list when there are no products store in the database
   */
  @Test
  @DisplayName("Try to get a pluck list when there are no products available")
  @Order(1)
  void getPluckListWithoutProducts() {
    var plucklist = this.pluckListController.getPluckList();

    assertEquals(HttpStatus.NO_CONTENT, plucklist.getStatusCode());
  }

  @Test
  @DisplayName("Get a pluck list")
  @Order(2)
  void getPluckList() {
    // Add data to database
    this.locationService.addLocation(new AddLocationRequest("H201", 321));
    this.productService.addProduct(new AddProductRequest(
            "Q-Melk",
            "H201",
            1.75,
            1.75,
            50,
            ProductType.D_PAK,
            Status.READY
    ));

    var response = this.pluckListController.getPluckList();
    var responseBody = response.getBody();

    if (responseBody instanceof PluckList) {
      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals(1, ((PluckList) responseBody).getPlucks().size());
    } else {
      fail();
    }
  }

}
