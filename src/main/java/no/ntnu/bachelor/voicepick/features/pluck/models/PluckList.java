package no.ntnu.bachelor.voicepick.features.pluck.models;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

  public PluckList(String route, String destination) {
    this.route = route;
    this.destination = destination;
  }

  public void addPluck(Pluck pluck) {
    this.plucks.add(pluck);
    pluck.setPluckList(this);
  }

}
