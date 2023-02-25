package no.ntnu.bachelor.voicepick.features.authentication.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KeycloakCredentials {
  private String type;
  private String value;
  private boolean temporary;
}
