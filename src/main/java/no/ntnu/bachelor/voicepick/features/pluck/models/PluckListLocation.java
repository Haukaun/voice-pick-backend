package no.ntnu.bachelor.voicepick.features.pluck.models;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = PluckListLocation.TABLE_NAME)
public class PluckListLocation {
    public static final String TABLE_NAME = "plucklist_location";
    public static final String PRIMARY_KEY = "plucklist_location_id";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = PluckListLocation.PRIMARY_KEY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "control_digit")
    private int controlDigit;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = PluckList.PRIMARY_KEY)
    private PluckList pluckList;


    public PluckListLocation(String name, int controlDigit){
        if (name.isBlank()) throw new IllegalArgumentException("Location cannot be empty");
        if (controlDigit < 0) throw new IllegalArgumentException("Control digits cannot not be negative");

        this.name = name;
        this.controlDigit = controlDigit;
    }
}
