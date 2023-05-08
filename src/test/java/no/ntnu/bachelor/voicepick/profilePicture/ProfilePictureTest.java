package no.ntnu.bachelor.voicepick.profilePicture;

import jakarta.persistence.EntityExistsException;
import no.ntnu.bachelor.voicepick.models.ProfilePicture;
import no.ntnu.bachelor.voicepick.repositories.ProfilePictureRepository;
import no.ntnu.bachelor.voicepick.services.ProfilePictureService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProfilePictureTest {

    @Autowired
    private ProfilePictureService service;
    @Autowired
    private ProfilePictureRepository repository;

    @AfterEach
    void tearDown() {
        this.repository.deleteAll();
    }

    @Test
    @DisplayName("Try to add an image with null")
    void tryToAddNullImage() {
        try {
            this.service.addProfilePicture(null);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    @DisplayName("Add image")
    void addImage() {
        try {
            this.service.addProfilePicture(new ProfilePicture("my-profile-picture.png"));
            assertEquals(1, this.repository.findAll().size());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    @DisplayName("Add two images with same name")
    void addImagesWithSameName() {
        var imgName = "img";
        this.service.addProfilePicture(new ProfilePicture(imgName));
        try {
            this.service.addProfilePicture(new ProfilePicture(imgName));
            fail();
        } catch (EntityExistsException e) {
            assertTrue(true);
        }
    }

}
