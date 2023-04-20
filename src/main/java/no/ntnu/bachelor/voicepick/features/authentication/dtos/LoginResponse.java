package no.ntnu.bachelor.voicepick.features.authentication.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import no.ntnu.bachelor.voicepick.dtos.WarehouseDto;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String expiresIn;
    private String refreshExpiresIn;
    private String tokenType;
    private String username;
    private String email;
    private Boolean emailVerified;

    private WarehouseDto warehouse;
}
