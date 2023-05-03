package no.ntnu.bachelor.voicepick.mappers;

import no.ntnu.bachelor.voicepick.features.authentication.dtos.RoleDto;
import no.ntnu.bachelor.voicepick.features.authentication.models.Role;
import org.mapstruct.Mapper;

import java.util.Collection;
@Mapper(componentModel = "spring")
public abstract class RoleMapper {

  public abstract RoleDto toRoleDto(Role role);

  public abstract Collection<RoleDto> toRoleDto(Collection<Role> roles);

}
