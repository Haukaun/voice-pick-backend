package no.ntnu.bachelor.voicepick.mappers;

import no.ntnu.bachelor.voicepick.features.pluck.dtos.PluckDto;
import no.ntnu.bachelor.voicepick.features.pluck.dtos.PluckListDto;
import no.ntnu.bachelor.voicepick.features.pluck.models.Pluck;
import no.ntnu.bachelor.voicepick.features.pluck.models.PluckList;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class PluckListMapper {

  private PluckMapper pluckMapper = Mappers.getMapper(PluckMapper.class);

  @Mapping(target = "plucks", source = "plucks", qualifiedByName = "plucksToPlucksDto")
  public abstract PluckListDto toPluckListDto(PluckList pluckList);

  @Named("plucksToPlucksDto")
  public Set<PluckDto> plucksToPlucksDto(Set<Pluck> plucks) {
    return pluckMapper.toPluckDto(plucks);
  }

}
