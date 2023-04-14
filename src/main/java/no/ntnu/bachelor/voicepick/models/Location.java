package no.ntnu.bachelor.voicepick.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * An entity that represents a location in a warehouse
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = Location.TABLE_NAME)
public class Location {

    public static final String TABLE_NAME = "locations";
    public static final String PRIMARY_KEY = "location_id";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Location.PRIMARY_KEY)
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "control_digits")
    private int controlDigits;

    @Column(name = "location_type")
    private LocationType locationType;

    @JsonBackReference
    @OneToMany(mappedBy = "location", fetch = FetchType.EAGER)
    private Set<LocationEntity> entities = new HashSet<>();

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = Warehouse.PRIMARY_KEY)
    private Warehouse warehouse;

    public Location(String code, int controlDigits, LocationType locationType) {
        if (code == null || code.isBlank()) throw new IllegalArgumentException("Location code cannot be blank");
        if (controlDigits < 0) throw new IllegalArgumentException("Control digits cannot be negative");
        if (locationType == null) throw new IllegalArgumentException("LocationType cannot be null");

        this.locationType = locationType;
        this.code = code;
        this.controlDigits = controlDigits;
    }

    /**
     * Adds a location entity to the location
     *
     * @param entity to be added
     */
    public void addEntity(LocationEntity entity) {
        this.entities.add(entity);
        entity.setLocation(this);
    }

    public void removeWarehouse() {
        this.warehouse.removeLocation(this);
        this.setWarehouse(null);
    }
}
