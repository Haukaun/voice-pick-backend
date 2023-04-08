package no.ntnu.bachelor.voicepick.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {
    private String uuid;
    private String firstName;
    private String lastName;
    private String email;

}
