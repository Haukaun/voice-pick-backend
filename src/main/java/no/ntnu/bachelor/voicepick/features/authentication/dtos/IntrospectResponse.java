package no.ntnu.bachelor.voicepick.features.authentication.dtos;

import lombok.Data;

/**
 * A class for the response when checking the state of a token
 * 
 * @author Joakim
 */
@Data
public class IntrospectResponse {

  private boolean active;

  /**
   * Returns the state of the a token
   * 
   * @return {@code true} if token is active, {@code false} otherwise
   */
  public boolean isActive() {
    return this.active;
  }

}
