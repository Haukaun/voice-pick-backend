package no.ntnu.bachelor.voicepick.features.authentication.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoleBody {
    private String id;
    private String name;
}
