package no.ntnu.bachelor.voicepick.services;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.features.authentication.repositories.UserRepository;
import no.ntnu.bachelor.voicepick.features.authentication.services.UserService;
import no.ntnu.bachelor.voicepick.models.ProfilePicture;
import no.ntnu.bachelor.voicepick.repositories.ProfilePictureRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfilePictureService {

    private final ProfilePictureRepository profilePictureRepository;
    private final UserRepository userRepository;

    /**
     * Adds a profile picture
     *
     * @param picture to add
     */
    public void addProfilePicture(ProfilePicture picture) {
        if (picture == null) {
            throw new IllegalArgumentException("Picture cannot be null");
        }

        var optionalPicture = this.profilePictureRepository.findByName(picture.getName());
        if (optionalPicture.isPresent()) {
            throw new EntityExistsException("Profile picture is already added");
        }

        this.profilePictureRepository.save(picture);
    }

}
