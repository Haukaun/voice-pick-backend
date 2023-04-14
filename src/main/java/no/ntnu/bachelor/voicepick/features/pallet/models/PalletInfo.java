package no.ntnu.bachelor.voicepick.features.pallet.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import no.ntnu.bachelor.voicepick.models.ProductType;
import no.ntnu.bachelor.voicepick.models.Status;

@Data
@AllArgsConstructor
public class PalletInfo {
  private String productName;
  private double productWeight;
  private double productVolume;
  private int quantity;
  private ProductType type;
}
