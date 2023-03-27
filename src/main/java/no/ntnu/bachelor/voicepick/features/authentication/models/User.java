package no.ntnu.bachelor.voicepick.features.authentication.models;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PreRemove;
import lombok.Getter;
import lombok.Setter;
import no.ntnu.bachelor.voicepick.features.pluck.models.PluckList;

@Getter
@Setter
@Entity(name = User.TABLE_NAME)
public class User {

    public static final String TABLE_NAME = "users";
    public static final String PRIMARY_KEY = "user_id";

    @Id
    private String id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @JsonBackReference
    @OneToMany(fetch = FetchType.EAGER)
    private Set<PluckList> plucklists = new HashSet<>();


    public User() {
    }

    public User(String uid, String firstName, String lastName, String email) {
        if (firstName == null || firstName.isBlank()) throw new IllegalArgumentException("firstName cannot be empty");
        if (lastName == null || lastName.isBlank()) throw new IllegalArgumentException("lastName cannot be empty");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email cannot be empty");

        this.id = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}
