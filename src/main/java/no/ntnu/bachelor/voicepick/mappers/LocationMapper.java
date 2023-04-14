package no.ntnu.bachelor.voicepick.mappers;

import no.ntnu.bachelor.voicepick.dtos.LocationDto;
import no.ntnu.bachelor.voicepick.models.Location;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class LocationMapper {

  public abstract LocationDto toLocationDto(Location location);

  @Mapping(target = "id", ignore = true)
  public abstract Location toLocation(LocationDto locationDto);

  public abstract Set<LocationDto> toLocationDto(Set<Location> locations);

}
