package no.ntnu.bachelor.voicepick.authentication;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import no.ntnu.bachelor.voicepick.features.authentication.models.RoleType;
import no.ntnu.bachelor.voicepick.features.authentication.models.User;
import no.ntnu.bachelor.voicepick.features.authentication.services.UserService;
import no.ntnu.bachelor.voicepick.models.ProfilePicture;
import no.ntnu.bachelor.voicepick.services.ProfilePictureService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserTests {

    @Autowired
    private UserService userService;
    @Autowired
    private ProfilePictureService profilePictureService;

    @AfterEach
    void teardown() {
        this.userService.deleteAll();
    }

    @Test
    @DisplayName("Create user with invalid fields")
    void createInvalidUser() {
        try {
            new User("", "Hans", "Val", "hans@val.com");
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        try {
            new User("asdasdasd", "", "Val", "hans@val.com");
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        try {
            new User("asdasdasd", "Hans", "", "hans@val.com");
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        try {
            new User("asdasasd", "Hans", "Val", "");
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    @DisplayName("Create valid user")
    void createUser() {
        try {
            new User("123pojkdfg", "Hans", "Val", "hans@val.com");
            assertTrue(true);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    @DisplayName("Create user that already exists")
    void createAlreadyExistingUser() {
        var user = new User("oijshdg", "Hans", "Val", "hans@val.com");
        this.userService.createUser(user);

        try {
            this.userService.createUser(user);
            fail();
        } catch (EntityExistsException e) {
            assertTrue(true);
        }
    }

    @Test
    @DisplayName("Save valid user to repository")
    void saveUser() {
        var user = new User("pjgasds", "Hans", "Val", "hans@val.com");
        this.userService.createUser(user);

        assertEquals(1, this.userService.getAllUsers().size());
    }

    @Test
    @DisplayName("Add role to user that does not exist")
    void addRoleToNonExistingUser() {
        try {
            this.userService.addRole("iopjgoiajerg", RoleType.LEADER);
            fail();
        } catch (EntityNotFoundException e) {
            assertTrue(true);
        }
    }

    @Test
    @DisplayName("Add role to user")
    void addRoleToUser() {
        var uuid = "pajpgajg";
        var user = new User(uuid, "Hans", "Val", "hans@val.com");
        this.userService.createUser(user);

        this.userService.addRole(uuid, RoleType.LEADER);

        assertEquals(2, this.userService.getUserByUuid(uuid).get().getRoles().size());
    }

    @Test
    @DisplayName("Get user by uuid that does not exisit")
    void getNonExistingUserByUuid() {
        var optionalUser = this.userService.getUserByUuid("oiejoiaerhg");

        assertTrue(optionalUser.isEmpty());
    }

    @Test
    @DisplayName("Get user by uuid")
    void getUserByUuid() {
        var uuid = "pajpgajg";
        var user = new User(uuid, "Hans", "Val", "hans@val.com");
        this.userService.createUser(user);

        var optionalUser = this.userService.getUserByUuid(uuid);

        assertTrue(optionalUser.isPresent());
    }

    @Test
    @DisplayName("Get all users")
    void getAllUsers() {
        var uuid = "pajpgajg";
        var user = new User(uuid, "Hans", "Val", "hans@val.com");
        this.userService.createUser(user);

        assertEquals(1, this.userService.getAllUsers().size());
    }

    @Test
    @DisplayName("Delete a user that does not exist")
    void deleteNonExistingUser() {
        try {
            this.userService.deleteUser("poaksdopasd");
            fail();
        } catch (EntityNotFoundException e) {
            assertTrue(true);
        }
    }

    @Test
    @DisplayName("Delete a user")
    void deleteUser() {
        var uuid = "pajpgajg";
        var user = new User(uuid, "Hans", "Val", "hans@val.com");
        this.userService.createUser(user);

        this.userService.deleteUser(uuid);

        assertEquals(0, this.userService.getAllUsers().size());
    }

    @Test
    @DisplayName("Delete all users")
    void deleteAllUsers() {
        this.userService.createUser(new User("123", "Hans", "Val", "hans@val.com"));
        this.userService.createUser(new User("321", "Arne", "Hansen", "arne@hansen.com"));

        this.userService.deleteAll();

        assertEquals(0, this.userService.getAllUsers().size());
    }

    @Test
    @DisplayName("Find user that does not exist by email")
    void getNonExistingUserByEmail() {
        var optionalUser = this.userService.getUserByEmail("email@test.com");

        assertTrue(optionalUser.isEmpty());
    }

    @Test
    @DisplayName("Find user by email")
    void getUserByEmail() {
        var email = "hans@val.com";
        this.userService.createUser(new User("123", "Hans", "Val", email));

        var optionalUser = this.userService.getUserByEmail(email);

        assertTrue(optionalUser.isPresent());
    }

    @Test
    @DisplayName("Update profile picture with null image")
    void updateProfilePictureWithNullImage() {
        var email = "hans@val.com";
        this.userService.createUser(new User("123", "Hans", "Val", email));

        try {
            this.userService.updateProfilePicture("123", "my-profile-image");
            fail();
        } catch (EntityNotFoundException e) {
            assertTrue(true);
        }
    }

    @Test
    @DisplayName("Update profile picture with valid image")
    void updateProfilePicture() {
        var uuid = "123";
        var email = "hans@val.com";
        this.userService.createUser(new User(uuid, "Hans", "Val", email));

        var imgName = "my-profile-picture";
        this.profilePictureService.addProfilePicture(new ProfilePicture(imgName));

        this.userService.updateProfilePicture(uuid, imgName);

        var optionalUser = this.userService.getUserByEmail(email);
        if (optionalUser.isEmpty()) {
            fail();
        }
        var user = optionalUser.get();

        assertEquals(imgName, user.getProfilePicture().getName());
    }
}
