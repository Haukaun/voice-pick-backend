package no.ntnu.bachelor.voicepick.features.authentication.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    public User() {
    }

    public User(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;

        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("firstName cannot be empty");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("lastName cannot be empty");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
    }
}
