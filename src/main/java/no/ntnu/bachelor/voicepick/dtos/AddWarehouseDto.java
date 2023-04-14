package no.ntnu.bachelor.voicepick.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddWarehouseDto {

  private String name;
  private String address;

}
