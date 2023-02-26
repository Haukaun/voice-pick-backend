package no.ntnu.bachelor.voicepick.features.authentication.dtos;

import lombok.Data;

/**
 * A request containing a token
 * 
 * @author Joakim
 */
@Data
public class TokenRequest {
  private String token;
}
