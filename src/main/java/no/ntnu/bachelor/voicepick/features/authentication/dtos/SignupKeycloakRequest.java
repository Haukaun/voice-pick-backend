package no.ntnu.bachelor.voicepick.features.authentication.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignupKeycloakRequest {
  private String email;
  private boolean enabled;
  private boolean emailVerified;
  private List<KeycloakCredentials> credentials;
}