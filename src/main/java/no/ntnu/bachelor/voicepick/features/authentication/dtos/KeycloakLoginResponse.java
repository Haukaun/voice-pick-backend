package no.ntnu.bachelor.voicepick.features.authentication.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeycloakLoginResponse {
  private String access_token;
  private String refresh_token;
  private String expires_in;
  private String refresh_expires_in;
  private String token_type;
}
