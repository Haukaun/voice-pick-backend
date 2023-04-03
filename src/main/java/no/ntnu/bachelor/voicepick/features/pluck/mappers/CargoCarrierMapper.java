package no.ntnu.bachelor.voicepick.features.pluck.mappers;

import no.ntnu.bachelor.voicepick.features.pluck.dtos.CargoCarrierDto;
import no.ntnu.bachelor.voicepick.features.pluck.models.CargoCarrier;
import org.mapstruct.Mapper;

import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class CargoCarrierMapper {

  public abstract CargoCarrierDto toCargoCarrierDto(CargoCarrier cargoCarrier);

  public abstract Set<CargoCarrierDto> toCargoCarrierDto(Set<CargoCarrier> cargoCarriers);

}
