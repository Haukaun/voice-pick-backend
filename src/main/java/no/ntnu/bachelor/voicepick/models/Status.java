package no.ntnu.bachelor.voicepick.models;

/**
 * Represents the status of a product
 * 
 * @author Joakim
 */
public enum Status {

  READY("READY"),
  EMPTY("EMPTY");

  public final String label;

  private Status(String label) {
    this.label = label;
  }

}
