package no.ntnu.bachelor.voicepick.dtos;

import lombok.Data;
import no.ntnu.bachelor.voicepick.models.ProductType;
import no.ntnu.bachelor.voicepick.models.Status;

@Data
public class AddProductRequest {
  private String name;
  private String location;
  private double weight;
  private double volume;
  private int quantity;
  private ProductType type;
  private Status status;
}
