package no.ntnu.bachelor.voicepick.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import no.ntnu.bachelor.voicepick.features.authentication.dtos.LoginRequest;
import no.ntnu.bachelor.voicepick.features.authentication.dtos.SignupRequest;
import no.ntnu.bachelor.voicepick.features.authentication.models.Role;
import no.ntnu.bachelor.voicepick.features.authentication.services.AuthService;
import no.ntnu.bachelor.voicepick.features.authentication.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Slf4j
class RoleTests {

    @Autowired
    private TestRestTemplate template;

    @Autowired
    private AuthService authService;

    @Autowired
    private MockMvc mockMvc;


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
    @WithMockUser(username = "user", password = "pwd", roles = "USER")
    void accessUserEndpointWithToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/user")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("Access leader endpoint with token")
    @WithMockUser(username = "leader", password = "pwd", roles = "LEADER")
    void accessLeaderEndpointWithToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/leader")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("Access admin endpoint with token")
    @WithMockUser(username = "admin", password = "pwd", roles = "ADMIN")
    void accessAdminEndpointWithToken() throws Exception {
       mockMvc.perform(MockMvcRequestBuilders
               .get("/admin")
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().is2xxSuccessful());
    }

}
