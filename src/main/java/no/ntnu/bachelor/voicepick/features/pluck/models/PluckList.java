package no.ntnu.bachelor.voicepick.features.pluck.models;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.OnDelete;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.ntnu.bachelor.voicepick.features.authentication.models.User;
import no.ntnu.bachelor.voicepick.models.LocationEntity;

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
public class PluckList extends LocationEntity {

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

  @Column(name = "confirmed_at")
  private LocalDateTime confirmedAt;

  @Column(name = "finished_at")
  private LocalDateTime finishedAt;

  @JsonManagedReference
  @OneToMany(mappedBy = "pluckList")
  private Set<Pluck> plucks = new HashSet<>();

  @JsonManagedReference
  @ManyToOne(optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
  @JoinColumn(name = User.PRIMARY_KEY, nullable = true)
  private User user;

  @JsonManagedReference
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = CargoCarrier.PRIMARY_KEY)
  private CargoCarrier cargoCarrier;

  public PluckList(String route, String destination, User user) {
    this.route = route;
    this.destination = destination;
    this.user = user;
  }

  /**
   * Adds a pluck to the pluck list
   *
   * @param pluck to be added
   */
  public void addPluck(Pluck pluck) {
    this.plucks.add(pluck);
    pluck.setPluckList(this);
  }

  /**
   * Removes a pluck from the pluck list
   *
   * @param pluck to be removed
   */
  public void removePluck(Pluck pluck) {
    this.plucks.remove(pluck);
    pluck.setPluckList(null);
  }

}
