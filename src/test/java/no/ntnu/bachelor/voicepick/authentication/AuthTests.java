package no.ntnu.bachelor.voicepick.authentication;

import no.ntnu.bachelor.voicepick.features.authentication.controllers.AuthController;
import no.ntnu.bachelor.voicepick.features.authentication.dtos.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthTests {

    @Autowired
    private AuthController authController;

    @Test
    @DisplayName("Register new user with missing email")
    @Order(1)
    void registerInvalidUser() {
        var response = this.authController.signup(new SignupRequest(
                "",
                "Knut123",
                "Knut",
                "Hansen"
        ));

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
    }

    @Test
    @DisplayName("Register new user")
    @Order(2)
    void registerNewUser() {
        var response = this.authController.signup(new SignupRequest(
                "Knut@solwr.com",
                "Knut123",
                "Knut",
                "Hansen"
        ));

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Try to register a new user that already exists")
    @Order(3)
    void registerUserThatExists() {
        var response = this.authController.signup(new SignupRequest(
                "Knut@solwr.com",
                "Knut123",
                "Knut",
                "Hansen"
        ));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    @DisplayName("Tries to login with invalid credentials")
    @Order(4)
    void loginWithInvalidCredentials() {
        var response = this.authController.login(new LoginRequest(
                "Knut@solwr.com",
                "ARandomPassword"
        ));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Login with newly created user")
    @Order(5)
    void loginWithNewUser() {
        var response = this.authController.login(new LoginRequest(
                "Knut@solwr.com",
                "Knut123"
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
    }

    @Test
    @DisplayName("Logout endpoint makes the access token invalid")
    @Order(6)
    void logout() {
        // Login
        var loginResponse = this.authController.login(new LoginRequest(
                "Knut@solwr.com",
                "Knut123"
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

    /**
     * Tries to delete a user that you have authorization to
     */
    @Test
    @DisplayName("Delete your user")
    @Order(8)
    void deleteYourUser() {
        var deleteResponse = this.authController.delete(new DeleteUserRequest(
                "Knut@solwr.com"
        ));

        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
    }

}
