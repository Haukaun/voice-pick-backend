package no.ntnu.bachelor.voicepick.features.authentication.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * A class for requesting the deletion of a user
 *
 * @author Joakim
 */
@Data
@AllArgsConstructor
public class DeleteUserRequest {
    private String email;
}
