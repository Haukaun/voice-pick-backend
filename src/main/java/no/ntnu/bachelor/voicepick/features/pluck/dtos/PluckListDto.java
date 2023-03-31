package no.ntnu.bachelor.voicepick.features.pluck.dtos;

import lombok.Data;
import no.ntnu.bachelor.voicepick.features.authentication.dtos.UserDto;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class PluckListDto {

  private Long id;
  private String route;
  private String destination;
  private LocalDateTime confirmedAt;
  private LocalDateTime finishedAt;
  private UserDto userDto;
  private Set<PluckDto> plucks;
  private CargoCarrierDto cargoCarrier;


}
