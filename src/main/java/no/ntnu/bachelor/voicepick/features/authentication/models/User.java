package no.ntnu.bachelor.voicepick.features.authentication.models;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.ntnu.bachelor.voicepick.features.pluck.models.PluckList;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity(name = User.TABLE_NAME)
@NoArgsConstructor
public class User {
    
    public static final String TABLE_NAME = "users";
    public static final String PRIMARY_KEY = "user_id";

    @Id
    @Column(name = User.PRIMARY_KEY)
    private String id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @OneToMany(mappedBy = "user")
    private List<PluckList> pluckLists = new ArrayList<>();

    public User(String id, String firstName, String lastName, String email) {
        if (firstName == null || firstName.isBlank()) throw new IllegalArgumentException("firstName cannot be empty");
        if (lastName == null || lastName.isBlank()) throw new IllegalArgumentException("lastName cannot be empty");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email cannot be empty");

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public void addPluckList(PluckList pluckList) {
        this.pluckLists.add(pluckList);
        pluckList.setUser(this);
    }

    public void removePluckList(PluckList pluckList) {
        this.pluckLists.remove(pluckList);
        pluckList.setUser(null);
    }
}
