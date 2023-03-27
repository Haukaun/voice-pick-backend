package no.ntnu.bachelor.voicepick.models;


import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

/**
 * An entity that represents a product
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = Product.TABLE_NAME)
public class Product extends LocationEntity {

  public static final String TABLE_NAME = "product";
  public static final String PRIMARY_KEY = "product_id";

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = Product.PRIMARY_KEY)
  private Long id;

  @Column(name = "product_name")
  private String name;

  @Column(name = "weight")
  private double weight;

  @Column(name = "volume")
  private double volume;

  @Column(name = "quantity")
  private int quantity;

  @Column(name = "type")
  private ProductType type;

  @Column(name = "status")
  private Status status;

  public Product(String name, double weight, double volume, int quantity, ProductType type,
                 Status status) {

    if (name.isBlank()) throw new IllegalArgumentException("Name cannot be empty");
    if (weight <= 0) throw new IllegalArgumentException("Cannot create product with negative weight");
    if (volume <= 0) throw new IllegalArgumentException("Cannot create product with negative volume");
    if (quantity < 0) throw new IllegalArgumentException("Quantity cannot be negative");

    this.name = name;
    this.weight = weight;
    this.volume = volume;
    this.quantity = quantity;
    this.type = type;
    this.status = status;
  }

  public void removeLocation() {
    this.getLocation().removeEntity(this);
  }

}
