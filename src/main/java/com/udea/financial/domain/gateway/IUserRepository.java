package com.udea.financial.domain.gateway;

import com.udea.financial.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserRepository {
    void save(User user);
    void update(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    void deleteById(Long id);
    List<User> allUsers();
}
