package no.ntnu.bachelor.voicepick.models;

/**
 * Represents the status of a product
 * 
 * @author Joakim
 */
public enum Status {

  READY("READY"),
  EMPTY("EMPTY"),
  WITHOUT_LOCATION("WITHOUT_LOCATION"),
  INACTIVE("INACTIVE");

  public final String label;

  private Status(String label) {
    this.label = label;
  }

}
