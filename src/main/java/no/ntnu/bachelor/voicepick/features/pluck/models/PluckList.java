package no.ntnu.bachelor.voicepick.features.pluck.models;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * An entity that represnets a pluck list
 * 
 * @author Joakim
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = PluckList.TABLE_NAME)
public class PluckList {

  public static final String TABLE_NAME = "pluck_list";
  public static final String PRIMARY_KEY = "pluck_list_id";

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = PluckList.PRIMARY_KEY)
  private Long id;

  @Column(name = "route")
  private String route;

  @Column(name = "destination")
  private String destination;

  // TODO: Add ref to plucker

  @JsonManagedReference
  @OneToMany(mappedBy = "pluckList")
  private Set<Pluck> plucks = new HashSet<>();

  @JsonManagedReference
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = CargoCarrier.PRIMARY_KEY)
  private CargoCarrier cargoCarrier;

  public PluckList(String route, String destination) {
    this.route = route;
    this.destination = destination;
  }

  /**
   * Adds a pluck to the pluck list. Also adds the pluck list that was called upon
   * and adds that pluck list to the pluck
   * 
   * @param pluck the pluck to add to the pluck list
   */
  public void addPluck(Pluck pluck) {
    this.plucks.add(pluck);
    pluck.setPluckList(this);
  }

  /**
   * Returns all plucks for the pluck list
   *
   * @return a set of all plucks
   */
  public Set<Pluck> getPlucks() {
    return this.plucks;
  }

}
