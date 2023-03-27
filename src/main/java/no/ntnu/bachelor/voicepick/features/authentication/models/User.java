package no.ntnu.bachelor.voicepick.features.authentication.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "users")
public class User {

    @Id
    @Column(name = "uid")
    private String uid;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    public User() {
    }

    public User(String uid, String firstName, String lastName, String email) {
        if (firstName == null || firstName.isBlank()) throw new IllegalArgumentException("firstName cannot be empty");
        if (lastName == null || lastName.isBlank()) throw new IllegalArgumentException("lastName cannot be empty");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email cannot be empty");

        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}
