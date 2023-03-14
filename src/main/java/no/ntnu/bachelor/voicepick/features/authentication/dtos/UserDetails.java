package no.ntnu.bachelor.voicepick.features.authentication.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDetails {
    private String id;
    private String createdTimestamp;
    private String username;
    private boolean enabled;
    private boolean emailVerified;
    private String email;
}
