package no.ntnu.bachelor.voicepick.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import no.ntnu.bachelor.voicepick.features.authentication.dtos.LoginRequest;
import no.ntnu.bachelor.voicepick.features.authentication.dtos.SignupRequest;
import no.ntnu.bachelor.voicepick.features.authentication.models.Role;
import no.ntnu.bachelor.voicepick.features.authentication.services.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
class RoleTests {

    @Autowired
    private TestRestTemplate template;

    @Autowired
    private AuthService authService;

    @Test
    @DisplayName("Test that accessing public endpoint works")
    void accessPublicEndpoint() {
        ResponseEntity<String> response = template.getForEntity("/version", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/user", "/leader", "/admin"})
    void accessUserEndpointWithoutToken(String url) {
        ResponseEntity<String> response = template.getForEntity(url, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Access user endpoint with user token")
    void accessUserEndpointWithToken() {
        var tmpEmail = "lidav87442@orgria.com";
        var tmpPassword = "hF+U*)w,*H4A<Ujg";

        // Create user
        try {
            authService.signup(new SignupRequest(
                    tmpEmail,
                    tmpPassword,
                    "test",
                    "user"
            ));
        } catch (Exception e) {
            log.info("User already created. Skipping this step...");
        }
        // Login with user
        var loginResponse = authService.login(new LoginRequest(
                tmpEmail,
                tmpPassword
        ));

        // Send request with token
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + loginResponse.getAccess_token());

        ResponseEntity<String> response = template.exchange("/user", HttpMethod.GET, new HttpEntity<>(headers), String.class);

        // Validate response
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Tear down
        // TODO: Delete user
    }

    @Test
    @DisplayName("Access leader endpoint with token")
    void accessLeaderEndpointWithToken() {
        var tmpEmail = "lidav87442@orgria.com";
        var tmpPassword = "hF+U*)w,*H4A<Ujg";

        // Create user
        try {
            authService.signup(new SignupRequest(
                    tmpEmail,
                    tmpPassword,
                    "test",
                    "user"
            ));
        } catch (Exception e) {
            log.info("User already created... Skipping this step!");
        }
        // Get id of user created
        String userId = "";
        try {
            userId = authService.getUserId(tmpEmail);
        } catch (JsonProcessingException e) {
            fail("Failed to get user id");
        }

        // Add leader role to user
        try {
            authService.addRole(userId, Role.LEADER);
        } catch (Exception e) {
            fail("Failed to add role to user");
        }

        // Login as user
        var loginResponse = authService.login(new LoginRequest(
                tmpEmail,
                tmpPassword
        ));

        // Send request to leader endpoint
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + loginResponse.getAccess_token());

        ResponseEntity<String> response = template.exchange("/leader", HttpMethod.GET, new HttpEntity<>(headers), String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        // TODO: Delete user
    }

    @Test
    @DisplayName("Access admin endpoint with token")
    void accessAdminEndpointWithToken() {
        var tmpEmail = "lidav87442@orgria.com";
        var tmpPassword = "hF+U*)w,*H4A<Ujg";

        // Create user
        try {
            authService.signup(new SignupRequest(
                    tmpEmail,
                    tmpPassword,
                    "Admin",
                    "User"
            ));
        } catch (Exception e) {
            log.info("User already created... Skipping this step!");
        }

        // Get id of user created
        String userId = "";
        try {
            userId = authService.getUserId(tmpEmail);
        } catch (JsonProcessingException e) {
            fail("Failed to get user id");
        }

        // Add leader role to user
        try {
            authService.addRole(userId, Role.ADMIN);
        } catch (Exception e) {
            fail("Failed to add role to user");
        }

        // Login as user
        var loginResponse = authService.login(new LoginRequest(
                tmpEmail,
                tmpPassword
        ));

        // Send request to admin endpoint
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + loginResponse.getAccess_token());

        ResponseEntity<String> response = template.exchange("/admin", HttpMethod.GET, new HttpEntity<>(headers), String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        // TODO: Delete user
    }

}
