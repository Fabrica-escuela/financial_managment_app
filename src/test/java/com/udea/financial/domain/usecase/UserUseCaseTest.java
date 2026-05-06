package com.udea.financial.domain.usecase;

import com.udea.financial.domain.exception.DuplicateEmailException;
import com.udea.financial.domain.exception.ExceptionMessages;
import com.udea.financial.domain.exception.UserNotFoundException;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link UserUseCase}.
 *
 * Branches covered
 * ─────────────────────────────────────────────────────────────────────────────
 * saveUser()
 *   ✔ email duplicado          → DuplicateEmailException, save() no se llama
 *   ✔ email nuevo              → password cifrado y save() invocado
 *
 * findUserByEmail()
 *   ✔ email existe             → retorna usuario
 *   ✔ email no existe          → UserNotFoundException
 *
 * findUserById()
 *   ✔ id existe                → retorna usuario
 *   ✔ id no existe             → UserNotFoundException
 *
 * deleteUserById()
 *   ✔ id no existe             → UserNotFoundException, deleteById() no se llama
 *   ✔ id existe                → deleteById() invocado
 *
 * allUsers()
 *   ✔ lista vacía              → retorna lista vacía
 *   ✔ lista con usuarios       → retorna todos
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserUseCase — unit tests")
class UserUseCaseTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IPasswordEncryptor passwordEncryptor;

    @InjectMocks
    private UserUseCase userUseCase;

    // ── helpers ───────────────────────────────────────────────────────────────

    private User buildUser(Long id, String email) {
        return User.builder()
                .idUser(id)
                .name("Test User")
                .email(email)
                .password("rawPassword")
                .build();
    }

    // =========================================================================
    @Nested
    @DisplayName("saveUser()")
    class SaveUser {

        @Test
        @DisplayName("Email duplicado lanza DuplicateEmailException sin invocar save()")
        void saveUser_duplicateEmail_throwsDuplicateEmailException() {
            // Arrange
            User user = buildUser(null, "existing@example.com");
            when(userRepository.findByEmail("existing@example.com"))
                    .thenReturn(Optional.of(user));

            // Act
            Throwable thrown = catchThrowable(() -> userUseCase.saveUser(user));

            // Assert
            assertThat(thrown)
                    .isInstanceOf(DuplicateEmailException.class)
                    .hasMessage(ExceptionMessages.DUPLICATE_EMAIL);

            verify(passwordEncryptor, never()).encrypt(any());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Email nuevo: cifra la contraseña e invoca save()")
        void saveUser_newEmail_encryptsPasswordAndCallsSave() {
            // Arrange
            User user = buildUser(null, "new@example.com");
            when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
            when(passwordEncryptor.encrypt("rawPassword")).thenReturn("$2a$encodedHash");

            // Act
            userUseCase.saveUser(user);

            // Assert — contraseña reemplazada en el objeto antes del save
            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(captor.capture());
            assertThat(captor.getValue().getPassword()).isEqualTo("$2a$encodedHash");
        }

        @Test
        @DisplayName("save() nunca recibe la contraseña en texto plano")
        void saveUser_passwordStoredEncrypted_neverPlainText() {
            // Arrange
            User user = buildUser(null, "secure@example.com");
            user.setPassword("plainText123");
            when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
            when(passwordEncryptor.encrypt("plainText123")).thenReturn("hashed");

            // Act
            userUseCase.saveUser(user);

            // Assert
            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(captor.capture());
            assertThat(captor.getValue().getPassword())
                    .isNotEqualTo("plainText123")
                    .isEqualTo("hashed");
        }
    }

    // =========================================================================
    @Nested
    @DisplayName("findUserByEmail()")
    class FindUserByEmail {

        @Test
        @DisplayName("Email existente retorna el usuario correcto")
        void findUserByEmail_existingEmail_returnsUser() {
            // Arrange
            User expected = buildUser(1L, "found@example.com");
            when(userRepository.findByEmail("found@example.com"))
                    .thenReturn(Optional.of(expected));

            // Act
            User result = userUseCase.findUserByEmail("found@example.com");

            // Assert
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("Email inexistente lanza UserNotFoundException con el email en el mensaje")
        void findUserByEmail_nonExistingEmail_throwsUserNotFoundException() {
            // Arrange
            when(userRepository.findByEmail("ghost@example.com"))
                    .thenReturn(Optional.empty());

            // Act
            Throwable thrown = catchThrowable(
                    () -> userUseCase.findUserByEmail("ghost@example.com"));

            // Assert
            assertThat(thrown)
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("ghost@example.com");
        }
    }

    // =========================================================================
    @Nested
    @DisplayName("findUserById()")
    class FindUserById {

        @Test
        @DisplayName("ID existente retorna el usuario correcto")
        void findUserById_existingId_returnsUser() {
            // Arrange
            User expected = buildUser(42L, "id@example.com");
            when(userRepository.findById(42L)).thenReturn(Optional.of(expected));

            // Act
            User result = userUseCase.findUserById(42L);

            // Assert
            assertThat(result).isEqualTo(expected);
            assertThat(result.getIdUser()).isEqualTo(42L);
        }

        @Test
        @DisplayName("ID inexistente lanza UserNotFoundException con el id en el mensaje")
        void findUserById_nonExistingId_throwsUserNotFoundException() {
            // Arrange
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            // Act
            Throwable thrown = catchThrowable(() -> userUseCase.findUserById(99L));

            // Assert
            assertThat(thrown)
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    // =========================================================================
    @Nested
    @DisplayName("deleteUserById()")
    class DeleteUserById {

        @Test
        @DisplayName("ID inexistente lanza UserNotFoundException sin llamar deleteById()")
        void deleteUserById_nonExistingId_throwsUserNotFoundAndNeverDeletes() {
            // Arrange
            when(userRepository.findById(7L)).thenReturn(Optional.empty());

            // Act
            Throwable thrown = catchThrowable(() -> userUseCase.deleteUserById(7L));

            // Assert
            assertThat(thrown)
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("7");

            verify(userRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("ID existente invoca deleteById() una sola vez")
        void deleteUserById_existingId_callsDeleteByIdOnce() {
            // Arrange
            User user = buildUser(5L, "delete@example.com");
            when(userRepository.findById(5L)).thenReturn(Optional.of(user));

            // Act
            userUseCase.deleteUserById(5L);

            // Assert
            verify(userRepository, times(1)).deleteById(5L);
        }
    }

    // =========================================================================
    @Nested
    @DisplayName("allUsers()")
    class AllUsers {

        @Test
        @DisplayName("Sin usuarios registrados retorna lista vacía")
        void allUsers_noUsers_returnsEmptyList() {
            // Arrange
            when(userRepository.allUsers()).thenReturn(List.of());

            // Act
            List<User> result = userUseCase.allUsers();

            // Assert
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Con usuarios registrados retorna la lista completa")
        void allUsers_withUsers_returnsAllUsers() {
            // Arrange
            List<User> users = List.of(
                    buildUser(1L, "a@example.com"),
                    buildUser(2L, "b@example.com"),
                    buildUser(3L, "c@example.com")
            );
            when(userRepository.allUsers()).thenReturn(users);

            // Act
            List<User> result = userUseCase.allUsers();

            // Assert
            assertThat(result).hasSize(3);
            assertThat(result).extracting(User::getEmail)
                    .containsExactlyInAnyOrder(
                            "a@example.com", "b@example.com", "c@example.com");
        }
    }
}