package no.ntnu.bachelor.voicepick.features.pluck.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import no.ntnu.bachelor.voicepick.dtos.LocationDto;
import no.ntnu.bachelor.voicepick.dtos.UserDto;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class PluckListDto {
  private Long id;
  private String route;
  private String destination;
  private LocalDateTime confirmedAt;
  private LocalDateTime finishedAt;
  private UserDto user;
  private Set<PluckDto> plucks;
  private CargoCarrierDto cargoCarrier;
  private LocationDto location;
}
