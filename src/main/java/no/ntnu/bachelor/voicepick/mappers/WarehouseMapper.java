package no.ntnu.bachelor.voicepick.mappers;

import no.ntnu.bachelor.voicepick.dtos.WarehouseDto;
import no.ntnu.bachelor.voicepick.models.Warehouse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class WarehouseMapper {
  public abstract WarehouseDto toWarehouseDto(Warehouse warehouse);

}
