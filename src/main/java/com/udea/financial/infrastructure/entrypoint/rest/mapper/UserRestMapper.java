package com.udea.financial.infrastructure.entrypoint.rest.mapper;

import com.udea.financial.domain.model.User;
import com.udea.financial.infrastructure.entrypoint.rest.dto.UserRequestDTO;
import com.udea.financial.infrastructure.entrypoint.rest.dto.UserResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserRestMapper {

    // Ignoramos los campos que el DTO no provee para evitar el warning de "Unmapped target properties"
    @Mapping(target = "idUser", ignore = true)
    @Mapping(target = "registrationDate", ignore = true)
    @Mapping(target = "failedLoginAttempts", ignore = true)
    @Mapping(target = "lockTime", ignore = true)
    User toDomain(UserRequestDTO dto);

    UserResponseDTO toResponse(User user);
}