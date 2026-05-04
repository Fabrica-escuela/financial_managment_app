package com.udea.financial.infrastructure.driven.persistence.adapter;

import com.udea.financial.domain.gateway.IUserRepository;
import com.udea.financial.domain.model.User;
import com.udea.financial.infrastructure.driven.persistence.mapper.UserMapper;
import com.udea.financial.infrastructure.driven.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements IUserRepository {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public void save(User user) {
        userRepository.save(userMapper.toEntity(user));
    }

    @Override
    public void update(User user) {
        userRepository.save(userMapper.toEntity(user));
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id).map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email).map(userMapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<User> allUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDomain)
                .toList();
    }
}
