package no.ntnu.bachelor.voicepick.features.authentication.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * A request containing a token
 * 
 * @author Joakim
 */
@Data
@AllArgsConstructor
public class TokenRequest {
  private String token;
}
