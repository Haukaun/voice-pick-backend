package no.ntnu.bachelor.voicepick.authentication;

import no.ntnu.bachelor.voicepick.features.authentication.controllers.AuthController;
import no.ntnu.bachelor.voicepick.features.authentication.dtos.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthTests {

    @Autowired
    private AuthController authController;

    private final static String EMAIL = "bamel39838@kaudat.com";
    private final static String PASSWORD = "spodjkfspdojkfef";
    private final static String FIRST_NAME = "Knut";
    private final static String LAST_NAME = "Hansen";

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

        // Tear down
        this.authController.delete(new DeleteUserRequest(
                EMAIL
        ));
    }

    @Test
    @DisplayName("Try to register a new user that already exists")
    void registerUserThatExists() {
        // Setup
        this.authController.signup(new SignupRequest(
                EMAIL,
                PASSWORD,
                FIRST_NAME,
                LAST_NAME
        ));

        var response = this.authController.signup(new SignupRequest(
                EMAIL,
                PASSWORD,
                FIRST_NAME,
                LAST_NAME
        ));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());

        // Tear down
        this.authController.delete(new DeleteUserRequest(
                EMAIL
        ));
    }

    @Test
    @DisplayName("Tries to login with invalid credentials")
    void loginWithInvalidCredentials() {
        // Setup
        this.authController.signup(new SignupRequest(
                EMAIL,
                PASSWORD,
                FIRST_NAME,
                LAST_NAME
        ));

        var response = this.authController.login(new LoginRequest(
                EMAIL,
                "ARandomPassword"
        ));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        // Tear down
        this.authController.delete(new DeleteUserRequest(
                EMAIL
        ));
    }

    @Test
    @DisplayName("Test login with valid credentials")
    void loginWithNewUser() {
        // Setup
        this.authController.signup(new SignupRequest(
                EMAIL,
                PASSWORD,
                FIRST_NAME,
                LAST_NAME
        ));

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

        // Tear down
        this.authController.delete(new DeleteUserRequest(
                EMAIL
        ));
    }

    @Test
    @DisplayName("Logout endpoint makes the access token invalid")
    void logout() {
        // Setup
        this.authController.signup(new SignupRequest(
                EMAIL,
                PASSWORD,
                FIRST_NAME,
                LAST_NAME
        ));

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

        // Tear down
        this.authController.delete(new DeleteUserRequest(
                EMAIL
        ));
    }

//    /**
//     * Tries to delete a user with out the authorization for the user to be deleted
//     */
//    @Test
//    @DisplayName("Delete random user")
//    @Order(7)
//    void deleteRandomUser() {
//        var response = this.authController.delete(new DeleteUserRequest(
//                "Knut@solwr.com"
//        ));
//
//        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
//    }
    
    @Test
    @DisplayName("Delete your user")
    void deleteYourUser() {
        // Setup
        this.authController.signup(new SignupRequest(
                EMAIL,
                PASSWORD,
                FIRST_NAME,
                LAST_NAME
        ));

        var deleteResponse = this.authController.delete(new DeleteUserRequest(
                EMAIL
        ));

        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());

        // Tear down
        this.authController.delete(new DeleteUserRequest(
                EMAIL
        ));
    }

}
