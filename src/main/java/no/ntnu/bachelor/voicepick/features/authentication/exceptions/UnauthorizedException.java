package no.ntnu.bachelor.voicepick.features.authentication.exceptions;

/**
 * Exception for when a user is not authorized to do an action.
 */
public class UnauthorizedException extends RuntimeException {

  public UnauthorizedException(String message) {
    super(message);
  }

}
