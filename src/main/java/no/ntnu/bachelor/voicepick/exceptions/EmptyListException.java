package no.ntnu.bachelor.voicepick.exceptions;

/**
 * An exception that can be thrown when an list is empty when it should be
 * 
 * @author Joakim
 */
public class EmptyListException extends Exception {

  public EmptyListException(String msg) {
    super(msg);
  }

}
