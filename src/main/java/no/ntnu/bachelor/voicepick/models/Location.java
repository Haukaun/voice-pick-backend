package no.ntnu.bachelor.voicepick.models;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

/**
 * An entity that represents a location in a warehouse
 * 
 * @author Joakim
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = Location.TABLE_NAME)
public class Location {

  public static final String TABLE_NAME = "location";
  public static final String PRIMARY_KEY = "location_id";
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = Location.PRIMARY_KEY)
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "control_digit")
  private int controlDigit;

  @JsonBackReference
  @OneToOne(mappedBy = "location", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private Product product;

  public Location(String name, int controlDigits) {
    if (name.isBlank()) throw new IllegalArgumentException("Location cannot be empty");
    if (controlDigits < 0) throw new IllegalArgumentException("Control digits cannot not be negative");

    this.name = name;
    this.controlDigit = controlDigits;
  }

}
