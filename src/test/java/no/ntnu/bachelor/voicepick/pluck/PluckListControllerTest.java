package no.ntnu.bachelor.voicepick.pluck;

import jakarta.transaction.Transactional;
import no.ntnu.bachelor.voicepick.dtos.AddProductRequest;
import no.ntnu.bachelor.voicepick.features.authentication.dtos.LoginRequest;
import no.ntnu.bachelor.voicepick.features.authentication.dtos.SignupRequest;
import no.ntnu.bachelor.voicepick.features.authentication.services.AuthService;
import no.ntnu.bachelor.voicepick.features.pluck.dtos.CargoCarrierDto;
import no.ntnu.bachelor.voicepick.features.pluck.models.CargoCarrier;
import no.ntnu.bachelor.voicepick.features.pluck.models.PluckList;
import no.ntnu.bachelor.voicepick.features.pluck.services.CargoCarrierService;
import no.ntnu.bachelor.voicepick.features.pluck.services.PluckListService;
import no.ntnu.bachelor.voicepick.models.ProductType;
import no.ntnu.bachelor.voicepick.models.Status;
import no.ntnu.bachelor.voicepick.services.LocationService;
import no.ntnu.bachelor.voicepick.services.ProductService;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

import javax.tools.DocumentationTool.Location;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
class PluckListControllerTest {


  @Autowired
  private ProductService productService;
  @Autowired
  private CargoCarrierService cargoCarrierService;
  @Autowired
  private LocationService locationService;

  @Autowired
  private AuthService authService;

  @Autowired
  private TestRestTemplate template;

  /**
   * Tries to fetch a pluck list when there are no products store in the database
   */
  @Test
  @DisplayName("Try to get a pluck list when there are no products available")
  void getPluckListWithoutProducts() {

    var tmpEmail = "lidav87442@orgria.com";
    var tmpPassword = "hF+U*)w,*H4A<Ujg";

    // Create user
    try {
      authService.signup(new SignupRequest(
          tmpEmail,
          tmpPassword,
          "test",
          "user"));
    } catch (Exception e) {
      System.out.println("User already created. Skipping this step...");
    }
    // Login with user
    var loginResponse = authService.login(new LoginRequest(
        tmpEmail,
        tmpPassword));

    // Send request with token
    var headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "Bearer " + loginResponse.getAccess_token());

    // lag plukk liste lokasjon
    if (!this.locationService.getLocationByCode("H201").isPresent()) {
      this.locationService.addLocation("H201", 321);
    } 

    ResponseEntity<String> response = template.exchange("/plucks", HttpMethod.GET, new HttpEntity<>(headers),
        String.class);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

    try {

      authService.delete(loginResponse.getAccess_token());
      this.locationService.deleteLocation("H201");

    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

  }

  @Test
  @DisplayName("Get a pluck list")
  void getPluckList() {

    // Setup
    // Create test user
    var tmpEmail = "lidav87442@orgria.no";
    var tmpPassword = "hF+U*)w,*H4A<Ujg";
    try {
      authService.signup(new SignupRequest(
          tmpEmail,
          tmpPassword,
          "test",
          "user"));
    } catch (Exception e) {
      System.out.println("User already created. Skipping this step...");
    }

    // Login with user
    var loginResponse = authService.login(new LoginRequest(
        tmpEmail,
        tmpPassword));

    // Create dummy data
    this.locationService.addLocation("H201", 321);
    this.locationService.addLocation("H344", 555);
    this.productService.addProduct(new AddProductRequest(
        "Q-Melk",
        "H201",
        1.75,
        1.75,
        50,
        ProductType.D_PAK,
        Status.READY));

    // Get pluck list
    var headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "Bearer " + loginResponse.getAccess_token());

    ResponseEntity<String> response = template.exchange("/plucks", HttpMethod.GET, new HttpEntity<>(headers),
        String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());

    try {
      authService.delete(loginResponse.getAccess_token());
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  @Test
  @DisplayName("Update cargo carrier for pluck list")
  @Transactional
  void updateCargoCarrier() {
    // Create test user
    var tmpEmail = "lidav87442@orgria.com";
    var tmpPassword = "hF+U*)w,*H4A<Ujg";

    // Create user
    try {
      authService.signup(new SignupRequest(
          tmpEmail,
          tmpPassword,
          "test",
          "user"));
    } catch (Exception e) {
      System.out.println("User already created. Skipping this step...");
    }

    // Login with user
    var loginResponse = authService.login(new LoginRequest(
        tmpEmail,
        tmpPassword));

    // Send request with token
    var headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "Bearer " + loginResponse.getAccess_token());

    // Create dummy data
    this.locationService.addLocation("H201", 321);

    // Setup
    ResponseEntity<String> response = template.exchange("/plucks", HttpMethod.GET, new HttpEntity<>(headers),
        String.class);

    try {
      PluckList pluckList = new ObjectMapper().readValue(response.getBody(), PluckList.class);
      this.cargoCarrierService.add(new CargoCarrier("Helpall", 1L, "one"));
      var cargoCarrierTypes = this.cargoCarrierService.findAll();

      assertEquals(1, cargoCarrierTypes.size());

      // Execution
      HttpEntity<CargoCarrierDto> request = new HttpEntity<>(
          new CargoCarrierDto(
              cargoCarrierTypes.get(0).getName(),
              cargoCarrierTypes.get(0).getIdentifier(),
              cargoCarrierTypes.get(0).getPhoneticIdentifier()),
          headers);

      template.exchange("/plucks/" + pluckList.getId() + "/cargo-carrier", HttpMethod.PUT, request, Void.class);

      // Validation
      ResponseEntity<PluckList> updatedPluckListResponse = template.exchange("/plucks/" + pluckList.getId(),
          HttpMethod.GET, new HttpEntity<>(headers), PluckList.class);
      PluckList updatedPluckList = updatedPluckListResponse.getBody();

      assert updatedPluckList != null;
      assertEquals("Helpall", updatedPluckList.getCargoCarrier().getName());
      assertEquals(1L, updatedPluckList.getCargoCarrier().getIdentifier());

    } catch (JsonMappingException e) {
      e.printStackTrace();
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    // Clean up test user
    try {
      authService.delete(loginResponse.getAccess_token());
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }
}
