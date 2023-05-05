package no.ntnu.bachelor.voicepick.features.pluck.models;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.ntnu.bachelor.voicepick.features.authentication.models.User;
import no.ntnu.bachelor.voicepick.models.LocationEntity;
import no.ntnu.bachelor.voicepick.models.Warehouse;

/**
 * An entity that represents a pluck list
 * 
 * @author Joakim
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = PluckList.TABLE_NAME)
public class PluckList extends LocationEntity {

  public static final String TABLE_NAME = "pluck_lists";
  public static final String PRIMARY_KEY = "pluck_list_id";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = PluckList.PRIMARY_KEY)
  private Long id;

  @Column(name = "route")
  private String route;

  @Column(name = "destination")
  private String destination;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "confirmed_at")
  private LocalDateTime confirmedAt;

  @Column(name = "finished_at")
  private LocalDateTime finishedAt;

  @JsonManagedReference
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = User.PRIMARY_KEY)
  private User user;

  @JsonBackReference
  @ManyToOne
  @JoinColumn(name = Warehouse.PRIMARY_KEY)
  private Warehouse warehouse;

  @JsonManagedReference
  @OneToMany(mappedBy = "pluckList")
  private Set<Pluck> plucks = new HashSet<>();

  @JsonManagedReference
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = CargoCarrier.PRIMARY_KEY)
  private CargoCarrier cargoCarrier;

  public PluckList(String route, String destination, User user) {
    if (route.isBlank()) throw new IllegalArgumentException("Route cannot be blank");
    if (route.isBlank()) throw new IllegalArgumentException("Destination cannot be blank");

    this.route = route;
    this.destination = destination;
    this.user = user;
    this.createdAt = LocalDateTime.now();
  }

  public PluckList(String route, String destination) {
    if (route.isBlank()) throw new IllegalArgumentException("Route cannot be blank");
    if (route.isBlank()) throw new IllegalArgumentException("Destination cannot be blank");

    this.route = route;
    this.destination = destination;
    this.createdAt = LocalDateTime.now();
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

  /**
   * Clears the pluck ready for deletion
   */
  public void clear() {
    for (var pluck : this.plucks) {
      pluck.setPluckList(null);
    }
    this.plucks.clear();

    if (user != null) {
      this.user.removePluckList(this);
      this.user = null;
    }

    if (cargoCarrier != null) {
      this.cargoCarrier.removePluckList(this);
      this.cargoCarrier = null;
    }
  }
}
