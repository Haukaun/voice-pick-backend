package no.ntnu.bachelor.voicepick.features.pluck.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import no.ntnu.bachelor.voicepick.dtos.ProductDto;

import java.time.LocalDateTime;

@Data
public class PluckDto {
  private Long id;
  private ProductDto product;
  private int amount;
  private int amountPlucked;
  private LocalDateTime createdAt;
  private LocalDateTime confirmedAt;
  private LocalDateTime pluckedAt;

}
