package no.ntnu.bachelor.voicepick.models;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
@Table(name = ProductLocation.TABLE_NAME)
public class ProductLocation {

  public static final String TABLE_NAME = "product_location";
  public static final String PRIMARY_KEY = "product_location_id";
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = ProductLocation.PRIMARY_KEY)
  private Long id;

  @Column(name = "location")
  private String location;

  @Column(name = "control_digit")
  private String controlDigit;

  @JsonBackReference
  @OneToOne(mappedBy = "location")
  private Product product;

  public ProductLocation(String location, String controlDigits) {
    if (location.isBlank()) throw new IllegalArgumentException("Location cannot be empty");
    if (controlDigits.isBlank()) throw new IllegalArgumentException("Control digits cannot not be empty");

    this.location = location;
    this.controlDigit = controlDigits;
  }

}
