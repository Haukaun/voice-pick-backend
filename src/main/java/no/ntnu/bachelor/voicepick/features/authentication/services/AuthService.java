package no.ntnu.bachelor.voicepick.features.authentication.services;

import java.util.*;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.EntityNotFoundException;
import no.ntnu.bachelor.voicepick.dtos.EmailDto;
import no.ntnu.bachelor.voicepick.features.authentication.dtos.*;
import no.ntnu.bachelor.voicepick.features.authentication.models.Role;
import no.ntnu.bachelor.voicepick.features.authentication.models.User;
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

  private final UserService userService;

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

  /*
   * Different grant types allowed with keycloak
   */
  public enum GrantType {
    PASSWORD("password"),
    CREDENTIALS("credentials");

    private final String label;

    GrantType(String label) { this.label = label; }

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

    // Admin access
    HttpHeaders headers = this.getAdminHeaders();

    SignupKeycloakRequest body = new SignupKeycloakRequest(
        request.getFirstName(),
        request.getLastName(),
        request.getEmail(),
        true,
        false,
        List.of(new KeycloakCredentials(
            "password",
            request.getPassword(),
            false)));

    ObjectMapper mapper = new ObjectMapper();
    String jsonBody = mapper.writeValueAsString(body);

    HttpEntity<String> httpEntity = new HttpEntity<>(jsonBody, headers);

    var signupUrl = baseUrl + "/auth/admin/realms/" + realm + "/users";
    var response = restTemplate.postForEntity(signupUrl, httpEntity, String.class);

    if (response.getStatusCode().is2xxSuccessful()) {
      var uid = this.getUserId(request.getEmail());
      User user = new User(uid, request.getFirstName(), request.getLastName(), request.getEmail().toLowerCase());
      userService.createUser(user);
    }
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
   * Find the user by email, then changes its password with a random one.
   * This method is run when /reset-password endpoint is used.
   *
   * @param recipient email of the recipient that will have its password changed
   * @param randomPassword the password that will be set for the recipient
   * @return true/false depending on if the password change was successful
   * @throws JsonProcessingException if json-body is invalid
   */
  public boolean resetUserPassword(EmailDto recipient, String randomPassword) throws JsonProcessingException {
    Optional<User> user = userService.getUserByEmail(recipient.getEmail());

    if (user.isEmpty()) {
      throw new EntityNotFoundException("User with email (" + recipient.getEmail() + ") does not exist.");
    }
    var headers = this.getAdminHeaders();

    KeycloakCredentials body = new KeycloakCredentials(
            "password",
            randomPassword,
            false);

    ObjectMapper mapper = new ObjectMapper();
    String jsonBody = mapper.writeValueAsString(body);

    String uid = user.get().getId();
    var url = baseUrl + "/auth/admin/realms/" + realm + "/users/" + uid + "/reset-password";
    var response = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(jsonBody, headers), String.class);

    return response.getStatusCode().is2xxSuccessful();
  }

  /**
   * Deletes a user
   *
   * @throws EntityNotFoundException if a user could not be found with the given email
   */
  public void delete() {
    String uid = userService.getCurrentUser().getId();

    var headers = this.getAdminHeaders();

    var url = baseUrl + "/auth/admin/realms/" + realm + "/users/" + uid;
    var response = restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(headers), String.class);

    if (response.getStatusCode().is2xxSuccessful()) {
      this.userService.deleteUser(uid);
    }

  }

  /**
   * Returns the id for a given user
   *
   * @param username of the user to get the id for
   * @return the id for a given user
   */
  public String getUserId(String username) throws JsonProcessingException {
    // Admin access
    HttpHeaders headers = this.getAdminHeaders();

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
    // Add admin token to headers
    HttpHeaders headers = this.getAdminHeaders();

    // Get role
    var roleUrl = baseUrl + "/auth/admin/realms/" + realm + "/roles/" + role.label;
    var response = restTemplate.exchange(roleUrl, HttpMethod.GET, new HttpEntity<>(headers), RoleRequest.class);

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

  /**
   * Generates headers for http request with admin access
   *
   * @return http headers that can be used in an http request with admin access
   */
  private HttpHeaders getAdminHeaders() {
    var response = this.login(new LoginRequest(
            this.managerUsername,
            this.managerPassword
    ));

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set(AUTHORIZATION_KEY, this.getAuthorizationValue(response.getAccess_token()));

    return headers;
  }

  public void setEmailVerified(String userId, boolean emailVerified) throws JsonProcessingException {
    HttpHeaders headers = this.getAdminHeaders();

    Map<String, Object> updateRequest = new HashMap<>();
    updateRequest.put("emailVerified", emailVerified);

    ObjectMapper mapper = new ObjectMapper();
    String jsonBody;
    
    jsonBody = mapper.writeValueAsString(updateRequest);

    HttpEntity<String> httpEntity = new HttpEntity<>(jsonBody, headers);

    var url = baseUrl + "/auth/admin/realms/" + realm + "/users/" + userId;
    restTemplate.exchange(url, HttpMethod.PUT, httpEntity, String.class);
  }
}
