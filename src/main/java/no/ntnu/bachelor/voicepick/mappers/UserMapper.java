package no.ntnu.bachelor.voicepick.mappers;

import no.ntnu.bachelor.voicepick.features.authentication.dtos.UserDto;
import no.ntnu.bachelor.voicepick.features.authentication.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

  @Mapping(target="id", source="id")
  public abstract UserDto toUserDto(User user);

  public abstract Collection<UserDto> toUserDto(Collection<User> user);

}
