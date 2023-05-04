package no.ntnu.bachelor.voicepick.warehouse;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import no.ntnu.bachelor.voicepick.dtos.AddWarehouseDto;
import no.ntnu.bachelor.voicepick.features.authentication.models.Role;
import no.ntnu.bachelor.voicepick.features.authentication.models.RoleType;
import no.ntnu.bachelor.voicepick.features.authentication.models.User;
import no.ntnu.bachelor.voicepick.features.authentication.services.UserService;
import no.ntnu.bachelor.voicepick.models.Warehouse;
import no.ntnu.bachelor.voicepick.repositories.WarehouseRepository;
import no.ntnu.bachelor.voicepick.services.WarehouseService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class WarehouseTest {

  @Autowired
  private WarehouseService warehouseService;

  @Autowired
  private WarehouseRepository warehouseRepository;

  @Autowired
  private UserService userService;

  private static final String WAREHOUSE_NAME = "test";
  private static final String EMAIL = "test@test.test";
  private static final String UUID = "123123";
  private static final String FIRST_NAME = "test";
  private static final String LAST_NAME = "mann";

  private Warehouse warehouse;
  private User leader;

  @BeforeEach
  void setup() {
    User user = new User(UUID, FIRST_NAME, LAST_NAME, EMAIL);
    userService.createUser(user);
    Optional<User> optionalUser = userService.getUserByEmail(EMAIL);
    if (optionalUser.isEmpty()) {
      fail();
    }
    this.leader = optionalUser.get();
    warehouseService.createWarehouse(leader, new AddWarehouseDto(WAREHOUSE_NAME, "testgata"));
    warehouseService.findByName(WAREHOUSE_NAME).ifPresent(value -> warehouse = value);
  }

  @AfterEach
  void tearDown() {
    userService.deleteAll();
    warehouseService.deleteAll();
  }

  @Test
  @DisplayName("Remove user from warehouse when user is deleted.")
  void removeUserFromWarehouseOnUserDelete() {
    assertEquals(1, warehouse.getUsers().size());
    try {
      userService.deleteUser(UUID);
      assertEquals(0, warehouse.getUsers().size());
    } catch (EntityNotFoundException e) {
      fail();
    }
  }

  @Test
  @DisplayName("Successfully remove user when warehouse is set to null")
  void removeUserWithNoWarehouseOnDelete() {
    try {
      User user = new User("qweqwe", "Ola", "Nordmann", "ola@nordmann.no");
      userService.createUser(user);
      userService.deleteUser("qweqwe");
      assertEquals(Optional.empty(), userService.getUserByUuid("qweqwe"));
    } catch (EntityNotFoundException e) {
      fail();
    }
  }
  
  @Test
  @DisplayName("Successfully get all users in warehouse")
  void successfullyGetAllUsersInWarehouse() {
    try {
      var users = warehouseService.findAllUsersInWarehouse(warehouse);
      assertEquals(1, users.size());
    } catch (IllegalArgumentException e) {
      fail();
    }
  }

  @Test
  @DisplayName("Successfully remove user from warehouse.")
  void removeUserFromWarehouse() {
    User userObject = new User("qweqwe", "Ola", "Nordmann", "ola@nordmann.no");
    userService.createUser(userObject);
    var warehouse = leader.getWarehouse();

    if (warehouse == null) {
      fail();
    }

    Optional<User> optionalUserToRemove = userService.getUserByUuid("qweqwe");
    if (optionalUserToRemove.isEmpty()) {
      fail();
    }
    var user = optionalUserToRemove.get();
    warehouse.addUser(user);

    assertEquals(2, warehouse.getUsers().size());

    warehouseService.removeUserFromWarehouse(warehouse, user.getUuid());

    assertEquals(1, warehouse.getUsers().size());
  }

  @Test
  @DisplayName("Successfully get zero users from warehouse after removing the last user.")
  void successfullyGetZeroUsersFromWarehouseAfterRemovingUser() {
    assertEquals(1, warehouseService.findAllUsersInWarehouse(warehouse).size());
    warehouse.removeUser(leader);
    assertEquals(0, warehouseService.findAllUsersInWarehouse(warehouse).size());
  }

  @Test
  @DisplayName("Successfully get error when getting users from null warehouse.")
  void successfullyGetErrorWhenGettingUsersFromNullWarehouse() {
    try {
      warehouseService.findAllUsersInWarehouse(null);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }
  }

  @Test
  @DisplayName("Successfully check if two users are in the same warehouse.")
  void successfullyCheckIfUsersInTheSameWarehouse() {
    User userObject = new User("qweqwe", "Ola", "Nordmann", "ola@nordmann.no");
    userService.createUser(userObject);
    var warehouse = leader.getWarehouse();

    Optional<User> optionalUser = userService.getUserByUuid("qweqwe");

    var user = optionalUser.get();
    warehouse.addUser(user);

    assertTrue(warehouseService.inSameWarehouse("qweqwe", UUID));
  }

  @Test
  @DisplayName("Successfully check two users are not in the same warehouse.")
  void successfullyCheckIfTwoUsersNotInTheSameWarehouse() {
    User userObject = new User("qweqwe", "Ola", "Nordmann", "ola@nordmann.no");
    userService.createUser(userObject);

    assertFalse(warehouseService.inSameWarehouse("qweqwe", UUID));
  }
}
