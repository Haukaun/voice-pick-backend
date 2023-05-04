package no.ntnu.bachelor.voicepick.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.ntnu.bachelor.voicepick.features.authentication.dtos.RoleDto;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {
    private String uuid;
    private String firstName;
    private String lastName;
    private String email;
    private Set<RoleDto> roles;
    private String profilePictureName;
}
