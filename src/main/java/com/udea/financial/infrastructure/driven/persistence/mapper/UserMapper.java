package com.udea.financial.infrastructure.driven.persistence.mapper;

import com.udea.financial.domain.model.User;
import com.udea.financial.infrastructure.driven.persistence.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toDomain(UserEntity entity);
    UserEntity toEntity(User user);
}
