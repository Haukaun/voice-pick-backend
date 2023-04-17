package no.ntnu.bachelor.voicepick.features.pallet.dtos;

import lombok.Data;
import no.ntnu.bachelor.voicepick.models.ProductType;

@Data
public class PalletInfoDto {

  private String productName;
  private double productWeight;
  private double productVolume;
  private int quantity;
  private ProductType type;

}
