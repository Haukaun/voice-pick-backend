package no.ntnu.bachelor.voicepick.models;

public enum LocationType {

  PLUCK_LIST("PLUCK_LIST"),
  PRODUCT("PRODUCT");

  public final String label;

  private LocationType(String label) { this.label = label; }

}
