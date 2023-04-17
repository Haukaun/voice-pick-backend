package no.ntnu.bachelor.voicepick.features.authentication.dtos;

import lombok.Data;

@Data
public class KeycloakLoginResponse {
  private String access_token;
  private String refresh_token;
  private String expires_in;
  private String refresh_expires_in;
  private String token_type;
}
