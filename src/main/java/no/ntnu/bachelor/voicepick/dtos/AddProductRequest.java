package no.ntnu.bachelor.voicepick.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.ntnu.bachelor.voicepick.models.ProductType;

/**
 * A class containing information about a product
 * 
 * @author Joakim
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddProductRequest {
  private String name;
  private String location;
  private double weight;
  private double volume;
  private int quantity;
  private ProductType type;
}
