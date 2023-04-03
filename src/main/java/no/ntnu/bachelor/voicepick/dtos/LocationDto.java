package no.ntnu.bachelor.voicepick.dtos;

import lombok.Data;

@Data
public class LocationDto {

  private Long id;
  private String code;
  private int controlDigits;

}
