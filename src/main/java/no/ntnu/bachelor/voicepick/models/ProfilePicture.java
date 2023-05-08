package no.ntnu.bachelor.voicepick.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.ntnu.bachelor.voicepick.features.authentication.models.User;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = ProfilePicture.TABLE_NAME)
@NoArgsConstructor
@Getter
@Setter
public class ProfilePicture {

    public static final String TABLE_NAME = "profile_picture";
    public static final String PRIMARY_KEY = "profile_picture_id";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ProfilePicture.PRIMARY_KEY)
    private Long id;

    @Column(name = "name")
    private String name;

    @JsonBackReference
    @OneToMany(mappedBy = "profilePicture")
    private Set<User> users = new LinkedHashSet<>();

    public ProfilePicture(String name) {
        this.name = name;
    }

    public void addUser(User user) {
        this.users.add(user);
        user.setProfilePicture(this);
    }

    public void removeUser(User user) {
        this.users.remove(user);
        user.setProfilePicture(null);
    }
}
