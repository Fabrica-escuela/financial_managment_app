package com.udea.financial.infrastructure.entrypoint.rest.mapper;

import com.udea.financial.domain.model.User;
import com.udea.financial.infrastructure.entrypoint.rest.dto.UserRequestDTO;
import com.udea.financial.infrastructure.entrypoint.rest.dto.UserResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserRestMapper {
    User toDomain(UserRequestDTO dto);
    UserResponseDTO toResponse(User user);
}
