package no.ntnu.bachelor.voicepick.features.authentication.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.core.subst.Token;
import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.features.authentication.dtos.IntrospectResponse;
import no.ntnu.bachelor.voicepick.features.authentication.dtos.KeycloakCredentials;
import no.ntnu.bachelor.voicepick.features.authentication.dtos.LoginRequest;
import no.ntnu.bachelor.voicepick.features.authentication.dtos.LoginResponse;
import no.ntnu.bachelor.voicepick.features.authentication.dtos.SignoutResponse;
import no.ntnu.bachelor.voicepick.features.authentication.dtos.TokenRequest;
import no.ntnu.bachelor.voicepick.features.authentication.dtos.SignupRequest;
import no.ntnu.bachelor.voicepick.features.authentication.dtos.SignupKeycloakRequest;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final RestTemplate restTemplate;

  @Value("${keycloak.base-url}")
  private String baseUrl;
  @Value("${keycloak.realm}")
  private String realm;
  @Value("${keycloak.client-id}")
  private String clientId;
  @Value("${keycloak.client-secret}")
  private String clientSecret;

  @Value("${keycloak.manager.username}")
  private String managerUsername;
  @Value("${keycloak.manager.password}")
  private String managerPassword;

  /**
   * Logins in a user by returning a jwt token
   * 
   * @param request login request containing user credentials
   * @return a login response containing token information
   */
  public LoginResponse login(LoginRequest request) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("client_id", this.clientId);
    map.add("client_secret", this.clientSecret);
    map.add("grant_type", "password");
    map.add("username", request.getEmail());
    map.add("password", request.getPassword());

    HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, headers);

    var loginUrl = baseUrl + "/auth/realms/" + realm + "/protocol/openid-connect/token";
    ResponseEntity<LoginResponse> response = restTemplate.postForEntity(loginUrl, httpEntity, LoginResponse.class);

    return response.getBody();
  }

  /**
   * Signs up a user
   * 
   * @param request sign up request containins user information
   * @throws JsonProcessingException if sign up request cannot be parsed to JSON
   *                                 object
   */
  public void signup(SignupRequest request) throws JsonProcessingException {
    // Login as admin user
    LoginResponse adminResponse = this.login(new LoginRequest(this.managerUsername, this.managerPassword));

    // Use token to register new user
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "Bearer " + adminResponse.getAccess_token());

    SignupKeycloakRequest body = new SignupKeycloakRequest(
        request.getEmail(),
        true,
        true,
        List.of(new KeycloakCredentials(
            "password",
            request.getPassword(),
            false)));

    ObjectMapper mapper = new ObjectMapper();
    String jsonBody = mapper.writeValueAsString(body);

    HttpEntity<String> httpEntity = new HttpEntity<>(jsonBody, headers);

    var signupUrl = baseUrl + "/auth/admin/realms/" + realm + "/users";
    restTemplate.postForEntity(signupUrl, httpEntity, String.class);
  }

  /**
   * Signs out a user
   * 
   * @param request sign out request containing user tokens needed to sign out
   */
  public boolean signout(TokenRequest request) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("client_id", this.clientId);
    map.add("client_secret", this.clientSecret);
    map.add("refresh_token", request.getToken());

    HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, headers);

    var signOutUrl = baseUrl + "/auth/realms/" + realm + "/protocol/openid-connect/logout";
    var response = restTemplate.postForEntity(signOutUrl, httpEntity, SignoutResponse.class);

    return response.getStatusCode().is2xxSuccessful();
  }

  /**
   * Checks the status of a token. Wether it's active or not.
   *
   * @param request a request containing the token to check.
   */
  public boolean introspect(TokenRequest request) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("client_id", this.clientId);
    map.add("client_secret", this.clientSecret);
    map.add("token", request.getToken());

    HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, headers);

    var signOutUrl = baseUrl + "/auth/realms/" + realm + "/protocol/openid-connect/token/introspect";
    var response = restTemplate.postForEntity(signOutUrl, httpEntity, IntrospectResponse.class);
    var result = response.getBody();

    if (result != null) {
      return result.isActive();
    } else {
      return false;
    }
  }

}
