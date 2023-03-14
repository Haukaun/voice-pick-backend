package no.ntnu.bachelor.voicepick.features.authentication.models;

/**
 * Represents a role for a user
 *
 * @author Joakim
 */
public enum Role {
    USER("USER"),
    LEADER("LEADER"),
    ADMIN("ADMIN");

    public final String label;

    private Role(String label) { this.label = label; }
}
