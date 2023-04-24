package no.ntnu.bachelor.voicepick.features.authentication.models;

public enum RoleType {
    ADMIN("ADMIN"),
    LEADER("LEADER"),
    USER("USER");

    public final String label;

    private RoleType(String label) { this.label = label; }
}
