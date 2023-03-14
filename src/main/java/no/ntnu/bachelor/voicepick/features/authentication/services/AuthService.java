package no.ntnu.bachelor.voicepick.features.authentication.services;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.EntityNotFoundException;
import no.ntnu.bachelor.voicepick.features.authentication.dtos.*;
import no.ntnu.bachelor.voicepick.features.authentication.models.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

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


  private static final String CLIENT_ID_KEY = "client_id";
  private static final String CLIENT_SECRET_KEY = "client_secret";
  private static final String TOKEN_KEY = "token";
  private static final String REFRESH_TOKEN_KEY = "refresh_token";
  private static final String GRANT_TYPE_KEY = "grant_type";
  private static final String AUTHORIZATION_KEY = "Authorization";

  public enum GrantType {
    PASSWORD("password"),
    CREDENTIALS("credentials");

    private final String label;

    private GrantType(String label) { this.label = label; }

    public String value() {
      return this.label;
    }
  }

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
    map.add(CLIENT_ID_KEY, this.clientId);
    map.add(CLIENT_SECRET_KEY, this.clientSecret);
    map.add(GRANT_TYPE_KEY, GrantType.PASSWORD.value());
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
   * @param request sign up request containing user information
   * @throws JsonProcessingException if sign up request cannot be parsed to JSON
   *                                 object
   */
  public void signup(SignupRequest request) throws JsonProcessingException {
    if (request.getEmail().isBlank()) throw new IllegalArgumentException("Email cannot be empty");
    if (request.getPassword().isBlank()) throw new IllegalArgumentException("Password cannot be empty");
    if (request.getFirstName().isBlank()) throw new IllegalArgumentException("First name cannot be empty");
    if (request.getLastName().isBlank()) throw new IllegalArgumentException("Last name cannot be empty");

    // Login as admin user
    LoginResponse adminResponse = this.login(new LoginRequest(this.managerUsername, this.managerPassword));

    // Use token to register new user
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set(AUTHORIZATION_KEY, this.getAuthorizationValue(adminResponse.getAccess_token()));

    SignupKeycloakRequest body = new SignupKeycloakRequest(
        request.getFirstName(),
        request.getLastName(),
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
  public boolean signOut(TokenRequest request) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add(CLIENT_ID_KEY, this.clientId);
    map.add(CLIENT_SECRET_KEY, this.clientSecret);
    map.add(REFRESH_TOKEN_KEY, request.getToken());

    HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, headers);

    var signOutUrl = baseUrl + "/auth/realms/" + realm + "/protocol/openid-connect/logout";
    var response = restTemplate.postForEntity(signOutUrl, httpEntity, SignoutResponse.class);

    return response.getStatusCode().is2xxSuccessful();
  }

  /**
   * Checks the status of a token. Whether it's active or not.
   *
   * @param request a request containing the token to check.
   */
  public boolean introspect(TokenRequest request) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add(CLIENT_ID_KEY, this.clientId);
    map.add(CLIENT_SECRET_KEY, this.clientSecret);
    map.add(TOKEN_KEY, request.getToken());

    HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, headers);

    var url = baseUrl + "/auth/realms/" + realm + "/protocol/openid-connect/token/introspect";
    var response = restTemplate.postForEntity(url, httpEntity, IntrospectResponse.class);
    var result = response.getBody();

    if (result != null) {
      return result.isActive();
    } else {
      return false;
    }
  }

  /**
   * Deletes a user
   *
   * @param email of the user to delete
   * @throws EntityNotFoundException if a user could not be found with the given email
   */
  public void delete(String email) throws EntityNotFoundException {
    // TODO: tmp solution
    String userId = "";
    try {
      userId = this.getUserId(email);
    } catch (Exception e) {
      throw new EntityNotFoundException("Could not get id for user with username: " + email);
    }

    var adminResponse = this.login(new LoginRequest(
            this.managerUsername,
            this.managerPassword
    ));

    var headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set(AUTHORIZATION_KEY, this.getAuthorizationValue(adminResponse.getAccess_token()));

    var url = baseUrl + "/auth/admin/realms/" + realm + "/users/" + userId;
    var response = restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(headers), String.class);


//    String userId = "";
//    try {
//      userId = this.getUserId(email);
//    } catch (Exception e) {
//      throw new EntityNotFoundException("Could not get id for user with username: " + email);
//    }
//
//    // Get token for deleting users
//    HttpHeaders deletionTokenHeaders = new HttpHeaders();
//    deletionTokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//    MultiValueMap<String, String> deletionMap = new LinkedMultiValueMap<>();
//    deletionMap.add(GRANT_TYPE_KEY, "credentials");
//    deletionMap.add(CLIENT_ID_KEY, this.clientId);
//    deletionMap.add(CLIENT_SECRET_KEY, this.clientSecret);
//    deletionMap.add("scope", "realm:delete-users");
//
//    HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(deletionMap, deletionTokenHeaders);
//
//    var url = baseUrl + "/auth/realms/" + realm + "/protocol/openid-connect/token";
//    ResponseEntity<LoginResponse> deletionTokenResponse = restTemplate.postForEntity(url, httpEntity, LoginResponse.class);
//    var deletionToken = deletionTokenResponse.getBody();
//
//    HttpHeaders deletionHeaders = new HttpHeaders();
//    deletionHeaders.setContentType(MediaType.APPLICATION_JSON);
//    deletionHeaders.set(AUTHORIZATION_KEY, this.getAuthorizationValue(deletionToken.getAccess_token()));
//
//    // Delete user
//    var deleteUrl = baseUrl + "/auth/admin/realms/" + realm + "/users/" + userId;
//    restTemplate.delete(deleteUrl, new HttpEntity<>(deletionHeaders), String.class);
  }

  /**
   * Returns the id for a given user
   *
   * @param username of the user to get the id for
   * @return the id for a given user
   */
  public String getUserId(String username) throws JsonProcessingException {
    // Login as admin user
    LoginResponse adminResponse = this.login(new LoginRequest(this.managerUsername, this.managerPassword));

    // Add admin token to headers
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set(AUTHORIZATION_KEY, this.getAuthorizationValue(adminResponse.getAccess_token()));

    // Search for user
    var userDetailUrl = baseUrl + "/auth/admin/realms/" + realm + "/users/?username=" + username;
    var userDetailResponse = restTemplate.exchange(userDetailUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);
    var body = userDetailResponse.getBody();

    // Check if user was found or not
    if (body == null) {
      throw new EntityNotFoundException("Did not find any users with username: " + username);
    }

    // Map response to object
    ObjectMapper mapper = new ObjectMapper();
    var userDetails = mapper.readValue(body, new TypeReference<List<UserDetails>>(){});

    return userDetails.get(0).getId();
  }

  /**
   * Adds a role to a user
   *
   * @param userId of the user to add the role to
   * @param role the role to be added
   */
  public void addRole(String userId, Role role) throws JsonProcessingException {
    // Login as admin user
    LoginResponse adminResponse = this.login(new LoginRequest(this.managerUsername, this.managerPassword));

    // Add admin token to headers
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set(AUTHORIZATION_KEY, this.getAuthorizationValue(adminResponse.getAccess_token()));

    // Get role
    var roleUrl = baseUrl + "/auth/admin/realms/" + realm + "/roles/" + role.label;
    var response = restTemplate.exchange(roleUrl, HttpMethod.GET, new HttpEntity<>(headers), RoleResponse.class);

    var body = new ObjectMapper().writeValueAsString(Collections.singletonList(response.getBody()));

    HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

    var url = baseUrl + "/auth/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm";
    restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
  }

  /**
   * Returns a string for the authorization field in an http request in the format "Bearer token"
   *
   * @param token the token to be used in the authorization field
   * @return a complete string for the value of the authorization field
   */
  private String getAuthorizationValue(String token) {
    return "Bearer " + token;
  }
}
