package no.ntnu.bachelor.voicepick.authentication;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import no.ntnu.bachelor.voicepick.features.authentication.models.User;
import no.ntnu.bachelor.voicepick.features.authentication.services.UserService;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthTests {
  @Autowired
  private UserService userService;

  private final static String UID = "123123";
  private final static String EMAIL = "bamel39838@kaudat.com";
  private final static String FIRST_NAME = "Knut";
  private final static String LAST_NAME = "Hansen";

  /**
   * Tears down the environment
   */
  @AfterEach
  void tearDown() {
    this.userService.deleteAll();
  }

  @Test
  @DisplayName("Register new user with missing email")
  void registerInvalidUser() {
    try {
      User user = new User(UID, FIRST_NAME, LAST_NAME, "");
      this.userService.createUser(user);
    } catch (Exception e) {
      assertEquals("Email cannot be empty", e.getMessage());
    }
  }

  @Test
  @DisplayName("Register new user")
  void registerNewUser() {
    User user = null;
    try {
      user = new User(UID, FIRST_NAME, LAST_NAME, EMAIL);
      userService.createUser(user);
    } catch (Exception e) {
      fail();
    }
    assertEquals(UID, userService.getUserByUid(UID).get().getId());
    assertEquals(1, userService.getAllUsers().size());
    assertTrue(userService.getUserByUid(user.getId()).isPresent());
  }

  @Test
  @DisplayName("Try to register a new user that already exists")
  void registerUserThatExists() {
    User user = new User(UID, FIRST_NAME, LAST_NAME, EMAIL);
    try {
      userService.createUser(user);
      userService.createUser(user);
    } catch (EntityExistsException e) {
      assertEquals("User with uid (" + user.getId() + ") already exists.", e.getMessage());
    }
  }
  @Test
  @DisplayName("Delete your user")
  void successfullyDeleteYourUser() {
    try {
      userService.createUser(new User(UID, FIRST_NAME, LAST_NAME, EMAIL));
      userService.deleteUser(UID);
    } catch (Exception e) {
      fail();
    }
    assertTrue(userService.getUserByUid(UID).isEmpty());
    assertEquals(0, userService.getAllUsers().size());
  }

  @Test
  @DisplayName("Unsuccessfully delete non-existing user")
  void unsuccessfullyDeleteNonExistingUser() {
    try {
      userService.deleteUser(UID);
      fail();
    } catch (EntityNotFoundException e) {
      assertEquals("User with id (" + UID + ") can't be deleted because it does not exist.", e.getMessage());
      assertEquals(0, userService.getAllUsers().size());
    }
  }
}
