package com.udea.financial.domain.usecase;

import com.udea.financial.domain.exception.DuplicateEmailException;
import com.udea.financial.domain.exception.ExceptionMessages;
import com.udea.financial.domain.exception.UserNotFoundException;
import com.udea.financial.domain.gateway.IPasswordEncryptor;
import com.udea.financial.domain.gateway.IUserRepository;
import com.udea.financial.domain.model.User;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class UserUseCase {

    private final IUserRepository userRepository;
    private final IPasswordEncryptor passwordEncryptor;

    public void saveUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new DuplicateEmailException(ExceptionMessages.DUPLICATE_EMAIL);
        }
        user.setPassword(passwordEncryptor.encrypt(user.getPassword()));
        userRepository.save(user);
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(ExceptionMessages.USER_NOT_FOUND_BY_EMAIL + email));
    }

    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(ExceptionMessages.USER_NOT_FOUND_BY_ID + id));
    }

    public void deleteUserById(Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(ExceptionMessages.USER_NOT_FOUND_BY_ID + id));
        userRepository.deleteById(id);
    }

    public List<User> allUsers() {
        return userRepository.allUsers();
    }
}
