package no.ntnu.bachelor.voicepick.features.authentication.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A request containing a token
 * 
 * @author Joakim
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenRequest {
  private String token;
}
