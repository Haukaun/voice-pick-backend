package no.ntnu.bachelor.voicepick.features.pluck.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * An entity that represents a pallet type
 *
 * @author Joakim
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = CargoCarrier.TABLE_NAME)
public class CargoCarrier {

  public static final String TABLE_NAME = "cargo_carrier";
  public static final String PRIMARY_KEY = "cargo_carrier_id";

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = CargoCarrier.PRIMARY_KEY)
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "identifier")
  private int identifier;


  @OneToMany(mappedBy = "cargoCarrier", fetch = FetchType.EAGER)
  private Set<PluckList> pluckLists = new HashSet<>();

  @Column(name = "phonetic_identifier")
  private String phoneticIdentifier;

  @Column(name = "is_active")
  private boolean isActive;

  public CargoCarrier(String name, int identifier, String phoneticIdentifier) {
    if (name.isBlank()) throw new IllegalArgumentException("Cannot create cargo carrier without a name");
    if (identifier < 0) throw new IllegalArgumentException("Identifier cannot be negative");
    if (phoneticIdentifier.isBlank()) throw new IllegalArgumentException("Cannot create cargo carrier without a phonetic identifier");

    this.name = name;
    this.identifier = identifier;
    this.phoneticIdentifier = phoneticIdentifier;
    this.isActive = true;
  }

  /**
   * Adds a pluck list to a cargo carrier
   *
   * @param pluckList to be added
   */
  public void addToPluckList(PluckList pluckList) {
    this.pluckLists.add(pluckList);
    pluckList.setCargoCarrier(this);
  }

  public void removePluckList(PluckList pluckList) {
    this.pluckLists.remove(pluckList);
    pluckList.setCargoCarrier(null);
  }

}
