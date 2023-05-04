package no.ntnu.bachelor.voicepick.features.authentication.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import no.ntnu.bachelor.voicepick.dtos.WarehouseDto;

import java.util.Collection;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String expiresIn;
    private String refreshExpiresIn;
    private String tokenType;

    private String uuid;
    private String username;
    private String pictureUrl;
    private String email;
    private Boolean emailVerified;
    private Collection<RoleDto> roles;
    private WarehouseDto warehouse;
}
