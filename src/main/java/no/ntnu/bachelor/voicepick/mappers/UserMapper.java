package no.ntnu.bachelor.voicepick.mappers;

import no.ntnu.bachelor.voicepick.dtos.UserDto;
import no.ntnu.bachelor.voicepick.features.authentication.dtos.RoleDto;
import no.ntnu.bachelor.voicepick.features.authentication.models.Role;
import no.ntnu.bachelor.voicepick.features.authentication.models.User;
import no.ntnu.bachelor.voicepick.models.ProfilePicture;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Collection;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

  private RoleMapper roleMapper = Mappers.getMapper(RoleMapper.class);

  @Mapping(target="roles", source="roles", qualifiedByName = "roleToRoleDto")
  @Mapping(target="profilePictureName", source="profilePicture", qualifiedByName = "profilePictureToProfilePictureName")
  public abstract UserDto toUserDto(User user);

  public abstract Collection<UserDto> toUserDto(Collection<User> user);

  @Named("roleToRoleDto")
  public RoleDto roleToRoleDto(Role role) { return roleMapper.toRoleDto(role); }

  @Named("profilePictureToProfilePictureName")
  public String profilePictureToProfilePictureName(ProfilePicture profilePicture) {
    if (profilePicture == null) {
      return null;
    }
    return profilePicture.getName();
  }
}
