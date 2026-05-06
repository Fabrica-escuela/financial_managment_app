package com.udea.financial.infrastructure.driven.persistence.mapper;

import com.udea.financial.domain.model.User;
import com.udea.financial.infrastructure.driven.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Convierte de la base de datos al dominio
    User toDomain(UserEntity entity);

    // Convierte del dominio a la base de datos
    // Aseguramos explícitamente que el idUser se mantenga para que JPA haga UPDATE
    @Mapping(target = "idUser", source = "idUser")
    UserEntity toEntity(User user);
}