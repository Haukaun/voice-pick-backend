package no.ntnu.bachelor.voicepick.warehouse;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import no.ntnu.bachelor.voicepick.dtos.AddWarehouseDto;
import no.ntnu.bachelor.voicepick.features.authentication.models.User;
import no.ntnu.bachelor.voicepick.features.authentication.services.UserService;
import no.ntnu.bachelor.voicepick.models.Warehouse;
import no.ntnu.bachelor.voicepick.services.WarehouseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@Transactional
class WarehouseTest {

  @Autowired
  private WarehouseService warehouseService;

  @Autowired
  private UserService userService;

  private static final String WAREHOUSE_NAME = "test";
  private static final String EMAIL = "test@test.test";
  private static final String UUID = "123123";
  private static final String FIRST_NAME = "test";
  private static final String LAST_NAME = "mann";

  private Warehouse warehouse;

  private User user;

  @BeforeEach
  void setup() {
    User user = new User(UUID, FIRST_NAME, LAST_NAME, EMAIL);
    userService.createUser(user);
    Optional<User> optionalUser = userService.getUserByEmail(EMAIL);
    if (optionalUser.isEmpty()) {
      fail();
    }
    this.user = optionalUser.get();
  }

  @Test
  @DisplayName("Remove user from warehouse when user is deleted.")
  void removeUserFromWarehouseOnUserDelete() {
    warehouseService.createWarehouse(user, new AddWarehouseDto(WAREHOUSE_NAME, "testgata"));
    warehouseService.findByName(WAREHOUSE_NAME).ifPresent(value -> warehouse = value);
    assertEquals(1, warehouse.getUsers().size());
    try {
      userService.deleteUser(UUID);
      assertEquals(0, warehouse.getUsers().size());
    } catch (EntityNotFoundException e) {
      fail();
    }
  }

  @Test
  @DisplayName("Successfully delete user when warehouse is set to null")
  void removeUserWithNoWarehouseOnDelete() {
    try {
      userService.deleteUser(UUID);
      assertEquals(Optional.empty(), userService.getUserByUuid(UUID));
    } catch (EntityNotFoundException e) {
      fail();
    }
  }

}
