package no.ntnu.bachelor.voicepick.location;

import no.ntnu.bachelor.voicepick.features.authentication.controllers.AuthController;
import no.ntnu.bachelor.voicepick.features.authentication.dtos.LoginRequest;
import no.ntnu.bachelor.voicepick.features.authentication.dtos.SignupRequest;
import no.ntnu.bachelor.voicepick.features.authentication.dtos.TokenRequest;
import no.ntnu.bachelor.voicepick.features.authentication.models.Role;
import no.ntnu.bachelor.voicepick.features.authentication.services.AuthService;
import no.ntnu.bachelor.voicepick.services.LocationService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import no.ntnu.bachelor.voicepick.controllers.LocationController;
import no.ntnu.bachelor.voicepick.dtos.AddLocationRequest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
class LocationControllerTest {

  @Autowired
  private TestRestTemplate template;
  @Autowired
  private LocationController locationController;
  @Autowired
  private LocationService locationService;
  @Autowired
  private AuthController authController;
  @Autowired
  private AuthService authService;

  private static final String EMAIL = "sizoff0303@filevino.com";
  private static final String PASSWORD = "qSAvxpZIQ+MHmnCM";
  private static final String FIRST_NAME = "Lars";
  private static final String LAST_NAME = "Monsen";

  /**
   * Creates an authorized user for the tests
   *
   * @return headers with token of the authorized user
   */
  HttpHeaders setup() {
    this.authController.signup(new SignupRequest(
            EMAIL,
            PASSWORD,
            FIRST_NAME,
            LAST_NAME
    ));

    String userId = null;
    try {
      userId = this.authService.getUserId(EMAIL);
      this.authService.addRole(userId, Role.LEADER);
    } catch (Exception e) {
      fail("Failed at setup");
    }

    var response = this.authController.login(new LoginRequest(EMAIL, PASSWORD));
    var body = response.getBody();
    assert body != null;

    var headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "Bearer " + body.getAccess_token());

    return headers;
  }

  /**
   * Deletes the user created to run the tests
   */
  void cleanup() {
    var body = this.authController.login(new LoginRequest(EMAIL, PASSWORD)).getBody();
    assert body != null;
    var token = body.getAccess_token();
    this.authController.delete(new TokenRequest(token));
  }

  @Test
  @DisplayName("Add location without authority")
  void addLocationWithoutAuthority() {
    var headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    var body = new AddLocationRequest("H209", 345);
    var response = template.exchange("/locations", HttpMethod.POST, new HttpEntity<>(body, headers), String.class);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }

  /**
   * Tries to add a location with negative control digits
   */
  @Test
  @DisplayName("Add location with negative control digits")
  @Order(1)
  void addProductLocationWithoutControlDigits() {
    var headers = this.setup();

    var body = new AddLocationRequest("H209", -231);
    var response = template.exchange("/locations", HttpMethod.POST, new HttpEntity<>(body, headers), String.class);

    assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());

    this.cleanup();
  }

  /**
   * Adds a valid location
   */
  @Test
  @DisplayName("Add valid product location")
  @Order(2)
  void addProductLocation() {
    var headers = this.setup();

    var body = new AddLocationRequest("H209", 231);
    var response = template.exchange("/locations", HttpMethod.POST, new HttpEntity<>(body, headers), String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());

    this.cleanup();
  }

  /**
   * Tries to add the location that was added in the test above a second time
   */
  @Test
  @DisplayName("Add location that already exists")
  @Order(3)
  void addSameLocationTwice() {
    var headers = this.setup();

    var body = new AddLocationRequest("H209", 231);
    var response = template.exchange("/locations", HttpMethod.POST, new HttpEntity<>(body, headers), String.class);

    assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
    assertEquals(1, this.locationService.getAll().size());

    this.cleanup();
  }
}
