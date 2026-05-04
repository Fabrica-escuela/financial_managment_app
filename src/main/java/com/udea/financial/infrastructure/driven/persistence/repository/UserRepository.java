package com.udea.financial.infrastructure.driven.persistence.repository;

import com.udea.financial.infrastructure.driven.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
}
