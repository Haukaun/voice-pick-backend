package no.ntnu.bachelor.voicepick.authentication;

import no.ntnu.bachelor.voicepick.features.authentication.controllers.AuthController;
import no.ntnu.bachelor.voicepick.features.authentication.dtos.*;
import no.ntnu.bachelor.voicepick.features.authentication.services.UserService;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthTests {

    @Autowired
    private AuthController authController;

    @Autowired
    private UserService userService;

    private final static String EMAIL = "bamel39838@kaudat.com";
    private final static String PASSWORD = "spodjkfspdojkfef";
    private final static String FIRST_NAME = "Knut";
    private final static String LAST_NAME = "Hansen";

    /**
     * Setup environment for tests
     */
    private void setup() {
        this.authController.signup(new SignupRequest(
                EMAIL,
                PASSWORD,
                FIRST_NAME,
                LAST_NAME
        ));
    }

    /**
     * Tears down the environment
     */
    private void tearDown() {
        var loginResponse = this.authController.login(new LoginRequest(
                EMAIL,
                PASSWORD
        ));

        this.authController.delete(new TokenRequest(
                loginResponse.getBody().getAccess_token()
        ));
    }

    @Test
    @DisplayName("Register new user with missing email")
    void registerInvalidUser() {
        var response = this.authController.signup(new SignupRequest(
                "",
                PASSWORD,
                FIRST_NAME,
                LAST_NAME
        ));

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
    }

    @Test
    @DisplayName("Register new user")
    void registerNewUser() {
        var response = this.authController.signup(new SignupRequest(
                EMAIL,
                PASSWORD,
                FIRST_NAME,
                LAST_NAME
        ));
        
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Check if the user is registered in the database
        var optionalUser = userService.getUserByEmail(EMAIL);
        assertTrue(optionalUser.isPresent());
        assertEquals(FIRST_NAME, optionalUser.get().getFirstName());
        assertEquals(LAST_NAME, optionalUser.get().getLastName());

        this.tearDown();
    }

    @Test
    @DisplayName("Try to register a new user that already exists")
    void registerUserThatExists() {
        this.setup();

        var response = this.authController.signup(new SignupRequest(
                EMAIL,
                PASSWORD,
                FIRST_NAME,
                LAST_NAME
        ));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());

        this.tearDown();
    }

    @Test
    @DisplayName("Tries to login with invalid credentials")
    void loginWithInvalidCredentials() {
        this.setup();

        var response = this.authController.login(new LoginRequest(
                EMAIL,
                "ARandomPassword"
        ));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        this.tearDown();
    }

    @Test
    @DisplayName("Test login with valid credentials")
    void loginWithNewUser() {
        this.setup();

        var response = this.authController.login(new LoginRequest(
                EMAIL,
                PASSWORD
        ));

        var body = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assert body != null;
        assertFalse(body.getAccess_token().isEmpty());
        assertFalse(body.getRefresh_token().isEmpty());

        // Call introspect endpoint to validate token
        var tokenResponse = this.authController.introspect(new TokenRequest(
                body.getAccess_token()
        ));

        assertEquals(HttpStatus.OK, tokenResponse.getStatusCode());

        this.tearDown();
    }

    @Test
    @DisplayName("Logout endpoint makes the access token invalid")
    void logout() {
        this.setup();

        var loginResponse = this.authController.login(new LoginRequest(
                EMAIL,
                PASSWORD
        ));
        var loginBody = loginResponse.getBody();

        assert loginBody != null;
        assertFalse(loginBody.getAccess_token().isEmpty());

        // Validate access token
        var introReponse = this.authController.introspect(new TokenRequest(
                loginBody.getAccess_token()
        ));

        assertEquals(HttpStatus.OK, introReponse.getStatusCode());

        // Logout
        var logoutResponse = this.authController.signout(new TokenRequest(
                loginBody.getRefresh_token()
        ));

        assertEquals(HttpStatus.OK, logoutResponse.getStatusCode());

        // Check that access token no longer is valid
        introReponse = this.authController.introspect(new TokenRequest(
                loginBody.getAccess_token()
        ));

        assertEquals(HttpStatus.UNAUTHORIZED, introReponse.getStatusCode());

        this.tearDown();
    }
    
    @Test
    @DisplayName("Delete your user")
    void deleteYourUser() {
        this.setup();

        var loginResponse = this.authController.login(new LoginRequest(
                EMAIL,
                PASSWORD
        ));

        var deleteResponse = this.authController.delete(new TokenRequest(
                loginResponse.getBody().getAccess_token()
        ));

        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
    }

}
