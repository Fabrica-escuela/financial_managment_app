package com.udea.financial.domain.usecase;

import com.udea.financial.domain.exception.AccountLockedException;
import com.udea.financial.domain.exception.ExceptionMessages;
import com.udea.financial.domain.exception.InvalidCredentialsException;
import com.udea.financial.domain.gateway.IPasswordEncryptor;
import com.udea.financial.domain.gateway.IUserRepository;
import com.udea.financial.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthUseCase — unit tests")
class AuthUseCaseTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IPasswordEncryptor passwordEncryptor;

    @InjectMocks
    private AuthUseCase authUseCase;

    private static final String EMAIL    = "user@example.com";
    private static final String PASSWORD = "securePass123";
    private static final String ENCODED  = "$2a$10$encodedHash";

    private User activeUser() {
        return User.builder()
                .idUser(1L)
                .name("John Doe")
                .email(EMAIL)
                .password(ENCODED)
                .failedLoginAttempts(0)
                .lockTime(null)
                .build();
    }

    private User userWithAttempts(int attempts) {
        return User.builder()
                .idUser(1L)
                .name("John Doe")
                .email(EMAIL)
                .password(ENCODED)
                .failedLoginAttempts(attempts)
                .lockTime(null)
                .build();
    }

    private User lockedUser() {
        return User.builder()
                .idUser(1L)
                .name("John Doe")
                .email(EMAIL)
                .password(ENCODED)
                .failedLoginAttempts(5)
                .lockTime(LocalDateTime.now())
                .build();
    }

    private User userWithExpiredLock() {
        return User.builder()
                .idUser(1L)
                .name("John Doe")
                .email(EMAIL)
                .password(ENCODED)
                .failedLoginAttempts(5)
                .lockTime(LocalDateTime.now().minusMinutes(3))
                .build();
    }

    @Nested
    @DisplayName("login() — user lookup branch")
    class UserLookup {
        @Test
        @DisplayName("Cuando el correo no existe lanza InvalidCredentialsException")
        void login_userNotFound_throwsInvalidCredentials() {
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
            Throwable thrown = catchThrowable(() -> authUseCase.login(EMAIL, PASSWORD));
            assertThat(thrown)
                    .isInstanceOf(InvalidCredentialsException.class)
                    .hasMessage(ExceptionMessages.INVALID_CREDENTIALS);
            verify(userRepository, never()).update(any());
        }
    }

    @Nested
    @DisplayName("login() — account lock branch")
    class AccountLockBranch {
        @Test
        @DisplayName("Cuenta bloqueada dentro de la ventana lanza AccountLockedException")
        void login_accountLocked_throwsAccountLocked() {
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(lockedUser()));
            Throwable thrown = catchThrowable(() -> authUseCase.login(EMAIL, PASSWORD));
            assertThat(thrown)
                    .isInstanceOf(AccountLockedException.class)
                    .hasMessage(ExceptionMessages.ACCOUNT_LOCKED);
            verify(passwordEncryptor, never()).matches(any(), any());
            verify(userRepository, never()).update(any());
        }

        @Test
        @DisplayName("Bloqueo expirado: reinicia intentos y continúa con la validación de contraseña")
        void login_lockExpired_resetsAttemptsAndContinues() {
            User user = userWithExpiredLock();
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
            when(passwordEncryptor.matches(PASSWORD, ENCODED)).thenReturn(true);

            User result = authUseCase.login(EMAIL, PASSWORD);

            assertThat(result).isNotNull();
            verify(userRepository, atLeastOnce()).update(user);
        }
    }

    @Nested
    @DisplayName("login() — password validation branch")
    class PasswordValidation {
        @Test
        @DisplayName("Contraseña incorrecta lanza InvalidCredentialsException e incrementa contador")
        void login_wrongPassword_throwsInvalidCredentialsAndIncrementsCounter() {
            User user = userWithAttempts(0);
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
            when(passwordEncryptor.matches(PASSWORD, ENCODED)).thenReturn(false);

            Throwable thrown = catchThrowable(() -> authUseCase.login(EMAIL, PASSWORD));

            assertThat(thrown).isInstanceOf(InvalidCredentialsException.class);
            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).update(captor.capture());
            assertThat(captor.getValue().getFailedLoginAttempts()).isEqualTo(1);
        }

        @Test
        @DisplayName("Quinto intento fallido establece lockTime y lanza AccountLockedException")
        void login_fifthWrongPassword_setsLockTimeAndThrows() {
            User user = userWithAttempts(4);
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
            when(passwordEncryptor.matches(PASSWORD, ENCODED)).thenReturn(false);

            Throwable thrown = catchThrowable(() -> authUseCase.login(EMAIL, PASSWORD));

            // Corregido: Tu implementación lanza AccountLockedException al llegar a 5 fallos
            assertThat(thrown).isInstanceOf(AccountLockedException.class);

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).update(captor.capture());
            User updated = captor.getValue();
            assertThat(updated.getFailedLoginAttempts()).isEqualTo(5);
            assertThat(updated.getLockTime()).isNotNull();
        }
    }

    @Nested
    @DisplayName("login() — successful login branch")
    class SuccessfulLogin {
        @Test
        @DisplayName("Login exitoso devuelve el usuario y reinicia los intentos fallidos")
        void login_correctPassword_returnsUserAndResetsAttempts() {
            User user = userWithAttempts(2);
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
            when(passwordEncryptor.matches(PASSWORD, ENCODED)).thenReturn(true);

            User result = authUseCase.login(EMAIL, PASSWORD);

            assertThat(result).isEqualTo(user);
            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).update(captor.capture());
            assertThat(captor.getValue().getFailedLoginAttempts()).isZero();
        }
    }
}