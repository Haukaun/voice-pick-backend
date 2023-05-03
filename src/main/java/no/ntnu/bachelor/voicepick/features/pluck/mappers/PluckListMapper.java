package no.ntnu.bachelor.voicepick.features.pluck.mappers;

import no.ntnu.bachelor.voicepick.dtos.LocationDto;
import no.ntnu.bachelor.voicepick.dtos.UserDto;
import no.ntnu.bachelor.voicepick.features.authentication.models.User;
import no.ntnu.bachelor.voicepick.features.pluck.dtos.PluckDto;
import no.ntnu.bachelor.voicepick.features.pluck.dtos.PluckListDto;
import no.ntnu.bachelor.voicepick.features.pluck.models.Pluck;
import no.ntnu.bachelor.voicepick.features.pluck.models.PluckList;
import no.ntnu.bachelor.voicepick.mappers.LocationMapper;
import no.ntnu.bachelor.voicepick.mappers.UserMapper;
import no.ntnu.bachelor.voicepick.models.Location;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class PluckListMapper {

  private PluckMapper pluckMapper = Mappers.getMapper(PluckMapper.class);
  private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
  private LocationMapper locationMapper = Mappers.getMapper(LocationMapper.class);

  @Mapping(target = "plucks", source = "plucks", qualifiedByName = "plucksToPlucksDto")
  @Mapping(target = "user", source = "user", qualifiedByName = "userToUserDto")
  @Mapping(target = "location", source = "location", qualifiedByName = "locationToLocationDto")
  public abstract PluckListDto toPluckListDto(PluckList pluckList);
  public abstract Set<PluckListDto> toPluckListDto(Set<PluckList> pluckList);

  @Named("plucksToPlucksDto")
  public Set<PluckDto> plucksToPlucksDto(Set<Pluck> plucks) {
    return pluckMapper.toPluckDto(plucks);
  }

  @Named("userToUserDto")
  public UserDto userToUserDto(User user) { return userMapper.toUserDto(user); }

  @Named("locationToLocationDto")
  public LocationDto locationToLocationDto(Location location) {
    return locationMapper.toLocationDto(location);
  }

}
