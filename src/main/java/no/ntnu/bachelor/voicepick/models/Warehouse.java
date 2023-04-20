package no.ntnu.bachelor.voicepick.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import no.ntnu.bachelor.voicepick.features.authentication.models.User;
import no.ntnu.bachelor.voicepick.features.pluck.models.PluckList;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = Warehouse.TABLE_NAME)
public class Warehouse {

  public static final String TABLE_NAME = "warehouses";
  public static final String PRIMARY_KEY = "warehouse_id";

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = Warehouse.PRIMARY_KEY)
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "address")
  private String address;

  @JsonManagedReference
  @OneToMany(mappedBy = "warehouse")
  private Set<User> users = new LinkedHashSet<>();

  @JsonManagedReference
  @OneToMany(mappedBy = "warehouse")
  private Set<PluckList> pluckLists = new LinkedHashSet<>();

  @JsonManagedReference
  @OneToMany(mappedBy = "warehouse")
  private Set<Product> products = new LinkedHashSet<>();

  @JsonManagedReference
  @OneToMany(mappedBy = "warehouse")
  private Set<Location> locations = new LinkedHashSet<>();

  public Warehouse(String name, String address) {
    this.name = name;
    this.address = address;
  }

  public void addUser(User user) {
    this.users.add(user);
    user.setWarehouse(this);
  }

  public void removeUser(User user) {
    this.users.remove(user);
    user.setWarehouse(null);
  }

  public void addLocation(Location location) {
    this.locations.add(location);
    location.setWarehouse(this);
  }

  public void removeLocation(Location location) {
    this.locations.remove(location);
    location.setWarehouse(null);
  }

  public void addPluckList(PluckList pluckList) {
    this.pluckLists.add(pluckList);
    pluckList.setWarehouse(this);
  }

  public void removePluckList(PluckList pluckList) {
    this.pluckLists.remove(pluckList);
    pluckList.setWarehouse(null);
  }

  public void addProduct(Product product) {
    this.products.add(product);
    product.setWarehouse(this);
  }

  public void removeProduct(Product product) {
    this.products.remove(product);
    product.setWarehouse(null);
  }

  public void clear() {
    this.users.forEach(user -> user.setWarehouse(null));
    this.users.clear();

    this.locations.forEach(location -> location.setWarehouse(null));
    this.locations.clear();

    this.products.forEach(product -> product.setWarehouse(null));
    this.products.clear();

    this.pluckLists.forEach(pluckList -> pluckList.setWarehouse(null));
    this.pluckLists.clear();
  }

}
