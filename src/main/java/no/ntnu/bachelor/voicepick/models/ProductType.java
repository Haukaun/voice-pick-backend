package no.ntnu.bachelor.voicepick.models;

/**
 * Represents the type of a product
 * 
 * @author Joakim
 */
public enum ProductType {
  F_PAK("F_PAK"),
  D_PAK("D_PAK");

  public final String label;

  private ProductType(String label) {
    this.label = label;
  }
}
