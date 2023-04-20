package no.ntnu.bachelor.voicepick.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseDto {

  private Long id;
  private String name;
  private String address;

}
