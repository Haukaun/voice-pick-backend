package no.ntnu.bachelor.voicepick.mappers;

import no.ntnu.bachelor.voicepick.dtos.LocationDto;
import no.ntnu.bachelor.voicepick.dtos.LocationEntityDto;
import no.ntnu.bachelor.voicepick.models.Location;
import no.ntnu.bachelor.voicepick.models.LocationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class LocationEntityMapper {

  private LocationMapper locationMapper = Mappers.getMapper(LocationMapper.class);

  @Mapping(target = "location", source = "location", qualifiedByName = "locationToLocationDto")
  public abstract LocationEntityDto toLocationEntityDto(LocationEntity locationEntity);

  public abstract Set<LocationEntityDto> toLocationEntityDto(Set<LocationEntity> locationEntities);

  @Named("locationToLocationDto")
  public LocationDto locationToLocationDto(Location location) {
    return locationMapper.toLocationDto(location);
  }
}
