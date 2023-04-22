package no.ntnu.bachelor.voicepick.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import no.ntnu.bachelor.voicepick.models.LocationType;

@Data
public class LocationDto {
  private String code;
  private int controlDigits;
  private LocationType locationType;

}
