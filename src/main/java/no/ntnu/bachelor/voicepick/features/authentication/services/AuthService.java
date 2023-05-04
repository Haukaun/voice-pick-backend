package no.ntnu.bachelor.voicepick.features.authentication.services;

import java.security.SecureRandom;
import java.util.*;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.EntityNotFoundException;
import no.ntnu.bachelor.voicepick.features.authentication.dtos.*;
import no.ntnu.bachelor.voicepick.features.authentication.exceptions.InvalidPasswordException;
import no.ntnu.bachelor.voicepick.features.authentication.exceptions.ResetPasswordException;
import no.ntnu.bachelor.voicepick.features.authentication.models.Role;
import no.ntnu.bachelor.voicepick.features.authentication.models.RoleType;
import no.ntnu.bachelor.voicepick.features.authentication.models.User;
import no.ntnu.bachelor.voicepick.features.authentication.utils.JwtUtil;
import no.ntnu.bachelor.voicepick.mappers.RoleMapper;
import no.ntnu.bachelor.voicepick.mappers.WarehouseMapper;
import no.ntnu.bachelor.voicepick.pojos.TokenObject;
import no.ntnu.bachelor.voicepick.models.Warehouse;
import no.ntnu.bachelor.voicepick.services.TokenStore;
import org.mapstruct.factory.Mappers;
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

  private final JwtUtil jwtUtil;

  private final WarehouseMapper warehouseMapper = Mappers.getMapper(WarehouseMapper.class);
  private final RoleMapper roleMapper = Mappers.getMapper(RoleMapper.class);

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

  private final TokenStore<String, TokenObject> emailVerificationStore = new TokenStore<>(8, 10);

  /*
   * Different grant types allowed with keycloak
   */
  public enum GrantType {
    PASSWORD("password"),
    CREDENTIALS("credentials");

    private final String label;

    GrantType(String label) {
      this.label = label;
    }

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
  public LoginResponse login(LoginRequest request) throws JsonProcessingException {
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
    ResponseEntity<KeycloakLoginResponse> response = restTemplate.postForEntity(loginUrl, httpEntity,
        KeycloakLoginResponse.class);

    KeycloakLoginResponse keycloakResponseBody = response.getBody();
    LoginResponse loginResponse = null;
    if (keycloakResponseBody != null) {
      var email = jwtUtil.getEmail(keycloakResponseBody.getAccess_token());
      var userName = jwtUtil.getUserName(keycloakResponseBody.getAccess_token());
      var emailVerified = jwtUtil.getEmailVerified(keycloakResponseBody.getAccess_token());
      var currentUser = userService.getUserByEmail(email);

      Warehouse warehouse = null;
      var uuid = "";
      String profilePictureName = null;
      Set<Role> roles = new HashSet<>();
      if (currentUser.isPresent()) {
        uuid = currentUser.get().getUuid();
        warehouse = currentUser.get().getWarehouse();
        roles = currentUser.get().getRoles();
        var profilePicture = currentUser.get().getProfilePicture();
        if (profilePicture != null) {
          profilePictureName = profilePicture.getName();
        }
      }

      loginResponse = new LoginResponse(
          keycloakResponseBody.getAccess_token(),
          keycloakResponseBody.getRefresh_token(),
          keycloakResponseBody.getExpires_in(),
          keycloakResponseBody.getRefresh_expires_in(),
          keycloakResponseBody.getToken_type(),
          uuid,
          userName,
          profilePictureName,
          email,
          emailVerified,
          roleMapper.toRoleDto(roles),
          warehouseMapper.toWarehouseDto(warehouse));
    } else {
      throw new EntityNotFoundException("User not found");
    }
    return loginResponse;
  }

  /**
   * Signs up a user
   * 
   * @param request sign up request containing user information
   * @throws JsonProcessingException if sign up request cannot be parsed to JSON
   *                                 object
   */
  public void signup(SignupRequest request) throws JsonProcessingException {
    if (request.getEmail().isBlank())
      throw new IllegalArgumentException("Email cannot be empty");
    if (request.getPassword().isBlank())
      throw new IllegalArgumentException("Password cannot be empty");
    if (request.getFirstName().isBlank())
      throw new IllegalArgumentException("First name cannot be empty");
    if (request.getLastName().isBlank())
      throw new IllegalArgumentException("Last name cannot be empty");

    // Admin access
    HttpHeaders headers = this.getAdminHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

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
   * Updates the password of the user to a new random password
   *
   * @param uuid of the user to update password of
   * @return the random password set to the user
   */
  public String forgotPassword(String uuid) throws EntityNotFoundException, JsonProcessingException, ResetPasswordException {
    var CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    var LENGTH = 8;
    var random = new SecureRandom();

    var newPassword = new StringBuilder(LENGTH);
    for (int i = 0; i < LENGTH; i++) {
      newPassword.append(CHARS.charAt(random.nextInt(CHARS.length())));
    }
    this.resetUserPassword(uuid, newPassword.toString());
    return newPassword.toString();
  }

  /**
   * Changes the password of a user
   *
   * @param uuid of the user to change the password of
   * @param email of the user to update
   * @param currentPassword the password of the user before changing it
   * @param newPassword the value set to the new password of the user
   * @throws JsonProcessingException if something went wrong when parsing request details
   * @throws InvalidPasswordException if current password provided is invalid
   * @throws ResetPasswordException if something went wrong when resetting password
   */
  public LoginResponse changePassword(String uuid, String email, String currentPassword, String newPassword) throws JsonProcessingException, InvalidPasswordException, ResetPasswordException {
    LoginResponse response = null;

    try {
      // Login to check if password provided is correct
      response = this.login(new LoginRequest(email, currentPassword));

      // Update the password
      this.resetUserPassword(uuid, newPassword);

    } catch (Exception e) {
      throw new InvalidPasswordException("The current password provided does not match");
    }

    return response;
  }

  /**
   * Changes the password of a user
   *
   * @param uuid of the user to update password of
   * @param password the password that will be set for the recipient
   * @throws JsonProcessingException if json-body is invalid
   * @throws ResetPasswordException if something went wrong when trying to reset the password
   * @throws EntityNotFoundException if no user with the given uuid was found
   */
  private void resetUserPassword(String uuid, String password) throws JsonProcessingException, ResetPasswordException {
    Optional<User> user = userService.getUserByUuid(uuid);

    if (user.isEmpty()) {
      throw new EntityNotFoundException("User with uuid (" + uuid + ") does not exist.");
    }
    var headers = this.getAdminHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    KeycloakCredentials body = new KeycloakCredentials(
        "password",
        password,
        false);

    ObjectMapper mapper = new ObjectMapper();
    String jsonBody = mapper.writeValueAsString(body);

    var url = baseUrl + "/auth/admin/realms/" + realm + "/users/" + uuid + "/reset-password";
    var response = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(jsonBody, headers), String.class);

    if (!response.getStatusCode().is2xxSuccessful()) {
      throw new ResetPasswordException("Failed to reset password for user with uuid: " + uuid);
    }
  }

  /**
   * Generates an email verification code
   *
   * @param uuid of the user to generate a email verification code for
   * @return the generated code
   */
  public String generateEmailVerificationCode(String uuid) {
    var code =  this.emailVerificationStore.generateCode();
    this.emailVerificationStore.addToken(uuid, new TokenObject(code));
    return code;
  }

  /**
   * Validated a verification code for a user
   *
   * @param uuid of the user to be validated
   * @param code to validate
   * @return {@code true} if code is valid for the user, {@code false} otherwise
   */
  public boolean validateEmailVerificationCode(String uuid, String code) {
    return this.emailVerificationStore.isValidToken(uuid, code);
  }

  /**
   * Deletes a user
   *
   * @throws EntityNotFoundException if a user could not be found with the given
   *                                 email
   */
  public void delete() {
    String uid = userService.getCurrentUser().getUuid();

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
    var userDetailResponse = restTemplate.exchange(userDetailUrl, HttpMethod.GET, new HttpEntity<>(headers),
        String.class);
    var body = userDetailResponse.getBody();

    // Check if user was found or not
    if (body == null) {
      throw new EntityNotFoundException("Did not find any users with username: " + username);
    }

    // Map response to object
    ObjectMapper mapper = new ObjectMapper();
    var userDetails = mapper.readValue(body, new TypeReference<List<UserDetails>>() {
    });

    return userDetails.get(0).getId();
  }

  /**
   * Adds a role to a user
   *
   * @param userId of the user to add the role to
   * @param role   the role to be added
   */
  public void addRole(String userId, RoleType role) throws JsonProcessingException {
    // Add admin token to headers
    HttpHeaders headers = this.getAdminHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    // Get role
    var roleUrl = baseUrl + "/auth/admin/realms/" + realm + "/roles/" + role.label;
    var response = restTemplate.exchange(roleUrl, HttpMethod.GET, new HttpEntity<>(headers), KeycloakRoleResponse.class);

    var body = new ObjectMapper().writeValueAsString(Collections.singletonList(response.getBody()));

    HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

    var url = baseUrl + "/auth/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm";
    var updateResponse = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);

    if (updateResponse.getStatusCode().is2xxSuccessful()) {
      this.userService.addRole(userId, role);
    }
  }

  /**
   * Returns a string for the authorization field in an http request in the format
   * "Bearer token"
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
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add(CLIENT_ID_KEY, this.clientId);
    map.add(CLIENT_SECRET_KEY, this.clientSecret);
    map.add(GRANT_TYPE_KEY, GrantType.PASSWORD.value());
    map.add("username", this.managerUsername);
    map.add("password", this.managerPassword);

    HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, headers);

    var loginUrl = baseUrl + "/auth/realms/" + realm + "/protocol/openid-connect/token";
    ResponseEntity<KeycloakLoginResponse> response = restTemplate.postForEntity(loginUrl, httpEntity,
        KeycloakLoginResponse.class);
    var body = response.getBody();

    if (body == null) {
      throw new EntityNotFoundException("Did not find any users with username: " + this.managerUsername);
    }

    headers.set(AUTHORIZATION_KEY, this.getAuthorizationValue(body.getAccess_token()));

    return headers;
  }

  /**
   * Updates the verified field of a user
   *
   * @param uuid of the user to update
   * @param emailVerified a boolean describing if the mail is verified or not
   */
  public void setEmailVerified(String uuid, boolean emailVerified) throws JsonProcessingException {
    HttpHeaders headers = this.getAdminHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    Map<String, Object> updateRequest = new HashMap<>();
    updateRequest.put("emailVerified", emailVerified);

    ObjectMapper mapper = new ObjectMapper();
    String jsonBody;

    jsonBody = mapper.writeValueAsString(updateRequest);

    HttpEntity<String> httpEntity = new HttpEntity<>(jsonBody, headers);

    var url = baseUrl + "/auth/admin/realms/" + realm + "/users/" + uuid;
    restTemplate.exchange(url, HttpMethod.PUT, httpEntity, String.class);
  }
}
