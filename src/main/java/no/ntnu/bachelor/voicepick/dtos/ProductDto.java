package no.ntnu.bachelor.voicepick.dtos;
import lombok.AllArgsConstructor;
import lombok.Data;
import no.ntnu.bachelor.voicepick.models.ProductType;
import no.ntnu.bachelor.voicepick.models.Status;

@Data
public class ProductDto {
  private Long id;
  private String name;
  private double weight;
  private double volume;
  private int quantity;
  private ProductType type;
  private Status status;

  private LocationDto location;
}
