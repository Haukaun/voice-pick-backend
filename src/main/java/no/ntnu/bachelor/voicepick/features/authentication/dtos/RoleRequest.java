package no.ntnu.bachelor.voicepick.features.authentication.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoleRequest {
    private String id;
    private String name;
}
