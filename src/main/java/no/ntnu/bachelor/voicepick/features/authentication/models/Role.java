package no.ntnu.bachelor.voicepick.features.authentication.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Represents a role for a user
 *
 * @author Joakim
 */
@Entity
@Table(name = Role.TABLE_NAME)
@NoArgsConstructor
@Getter
@Setter
public class Role {
    public static final String TABLE_NAME = "roles";
    public static final String PRIMARY_KEY = "role_id";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Role.PRIMARY_KEY)
    private Long id;

    @Column(name = "type")
    private RoleType type;

    @JsonBackReference
    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new LinkedHashSet<>();

    public Role(RoleType type) {
        this.type = type;
    }

    public void addUser(User user) {
        this.users.add(user);
    }

    public void removeUser(User user) {
        this.users.remove(user);
    }
}
