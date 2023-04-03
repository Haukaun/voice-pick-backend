package no.ntnu.bachelor.voicepick.features.authentication.dtos;

import lombok.Data;

/**
 * An object containing the message received when signed out
 * 
 * @author Joakim
 */
@Data
public class SignoutResponse {

  private String message;

}
