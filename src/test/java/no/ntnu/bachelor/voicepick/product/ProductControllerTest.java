package no.ntnu.bachelor.voicepick.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import no.ntnu.bachelor.voicepick.controllers.ProductController;
import no.ntnu.bachelor.voicepick.dtos.AddProductRequest;
import no.ntnu.bachelor.voicepick.models.ProductType;
import no.ntnu.bachelor.voicepick.models.Status;
import no.ntnu.bachelor.voicepick.services.ProductService;
import no.ntnu.bachelor.voicepick.services.LocationService;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class ProductControllerTest {

  @Autowired
  private ProductController productController;
  @Autowired
  private ProductService productService;
  @Autowired
  private LocationService locationService;

   @Test
   @DisplayName("Try to add an invalid product")
   @Order(1)
   void addInvalidProduct() {
    ResponseEntity<String> response = this.productController.addProduct(new AddProductRequest(
      "", 
      "", 
      -1, 
      -100, 
      -200, 
      ProductType.D_PAK, 
      Status.EMPTY
      ));

      assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
      assertEquals(0, productService.getAllProducts().size());
   }

   @Test
   @DisplayName("Add a product without any location")
   @Order(2)
   void addProductWithoutLocation() {
    this.productController.addProduct(new AddProductRequest(
      "Q-Melk",
      "",
      1.75,
      1.75,
      50,
      ProductType.D_PAK,
      Status.READY
    ));

    assertEquals(1, productService.getAllProducts().size());
    assertEquals(0, locationService.getAll().size());
  }

   @Test
   @DisplayName("Add a valid product")
   @Order(3)
   void addProduct() {
    this.productController.addProduct(new AddProductRequest(
      "Coca Cola",
      "H201",
      1.75,
      1.75,
      50,
      ProductType.D_PAK,
      Status.READY
    ));

    var productsFound = this.productService.getProductsByName("Coca Cola");
    assertEquals(1, productsFound.size());

    var product = productsFound.get(0);
    assertEquals("Coca Cola", product.getName());
    assertNull(product.getLocation());
    assertEquals(1.75, product.getWeight());
    assertEquals(1.75, product.getVolume());
    assertEquals(50, product.getQuantity());
    assertEquals(ProductType.D_PAK, product.getType());
    assertEquals(Status.READY, product.getStatus());
   }
}
