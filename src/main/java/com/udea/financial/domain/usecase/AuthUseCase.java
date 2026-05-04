package com.udea.financial.domain.usecase;

import com.udea.financial.domain.exception.AccountLockedException;
import com.udea.financial.domain.exception.ExceptionMessages;
import com.udea.financial.domain.exception.InvalidCredentialsException;
import com.udea.financial.domain.gateway.IPasswordEncryptor;
import com.udea.financial.domain.gateway.IUserRepository;
import com.udea.financial.domain.model.User;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class AuthUseCase {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 2;

    private final IUserRepository userRepository;
    private final IPasswordEncryptor passwordEncryptor;

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException(ExceptionMessages.INVALID_CREDENTIALS));

        if (isAccountLocked(user)) {
            throw new AccountLockedException(ExceptionMessages.ACCOUNT_LOCKED);
        }

        if (!passwordEncryptor.matches(password, user.getPassword())) {
            handleFailedAttempt(user);
            throw new InvalidCredentialsException(ExceptionMessages.INVALID_CREDENTIALS);
        }

        resetFailedAttempts(user);
        return user;
    }

    private boolean isAccountLocked(User user) {
        if (user.getLockTime() == null) {
            return false;
        }
        if (user.getLockTime().plusMinutes(LOCK_DURATION_MINUTES).isAfter(LocalDateTime.now())) {
            return true;
        }
        resetFailedAttempts(user);
        return false;
    }

    private void handleFailedAttempt(User user) {
        int newAttempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(newAttempts);
        if (newAttempts >= MAX_FAILED_ATTEMPTS) {
            user.setLockTime(LocalDateTime.now());
        }
        userRepository.update(user);
    }

    private void resetFailedAttempts(User user) {
        user.setFailedLoginAttempts(0);
        user.setLockTime(null);
        userRepository.update(user);
    }
}
