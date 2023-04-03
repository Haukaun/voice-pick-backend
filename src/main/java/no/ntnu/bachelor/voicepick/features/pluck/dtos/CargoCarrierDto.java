package no.ntnu.bachelor.voicepick.features.pluck.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CargoCarrierDto {

  private Long id;
  private String name;
  private int identifier;
  private String phoneticIdentifier;
}
