package com.udea.financial.infrastructure.entrypoint.rest.handler;

import com.udea.financial.domain.exception.*;
import com.udea.financial.infrastructure.entrypoint.rest.dto.ErrorResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link GlobalExceptionHandler}.
 */
@DisplayName("GlobalExceptionHandler — unit tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    // =========================================================================
    @Nested
    @DisplayName("handleUserNotFound()")
    class HandleUserNotFound {

        @Test
        @DisplayName("Retorna 404 con el mensaje de la excepción y detail fijo")
        void handleUserNotFound_returns404WithExpectedBody() {
            // Arrange
            UserNotFoundException ex = new UserNotFoundException("The requested user does not exist");

            // Act
            ResponseEntity<ErrorResponseDTO> response = handler.handleUserNotFound(ex);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            ErrorResponseDTO body = response.getBody();
            assertThat(body).isNotNull();
            assertThat(body.getErrorCode()).isEqualTo(404);
            // Validamos que el detalle sea el texto en español que lanza tu Handler
            assertThat(body.getDetails()).containsExactly("El recurso solicitado no existe en el sistema");
        }
    }

    // =========================================================================
    @Nested
    @DisplayName("handleDuplicateEmail()")
    class HandleDuplicateEmail {

        @Test
        @DisplayName("Retorna 409 con el mensaje de la excepción y detail fijo")
        void handleDuplicateEmail_returns409WithExpectedBody() {
            // Arrange
            DuplicateEmailException ex = new DuplicateEmailException("A user with this email already exists");

            // Act
            ResponseEntity<ErrorResponseDTO> response = handler.handleDuplicateEmail(ex);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            ErrorResponseDTO body = response.getBody();
            assertThat(body).isNotNull();
            assertThat(body.getErrorCode()).isEqualTo(409);
            // Validamos contra el texto en español
            assertThat(body.getDetails()).containsExactly("Ya existe un registro con este correo electrónico");
        }
    }

    // =========================================================================
    @Nested
    @DisplayName("handleInvalidCredentials()")
    class HandleInvalidCredentials {

        @Test
        @DisplayName("Retorna 401 con el mensaje de la excepción y sin campo details")
        void handleInvalidCredentials_returns401WithoutDetails() {
            // Arrange
            InvalidCredentialsException ex =
                    new InvalidCredentialsException(ExceptionMessages.INVALID_CREDENTIALS);

            // Act
            ResponseEntity<ErrorResponseDTO> response = handler.handleInvalidCredentials(ex);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            ErrorResponseDTO body = response.getBody();
            assertThat(body).isNotNull();
            assertThat(body.getErrorCode()).isEqualTo(401);
            assertThat(body.getDetails()).isNull();
        }
    }

    // =========================================================================
    @Nested
    @DisplayName("handleAccountLocked()")
    class HandleAccountLocked {

        @Test
        @DisplayName("Retorna 423 con el mensaje de la excepción y el detalle de tiempo")
        void handleAccountLocked_returns423WithoutDetails() {
            // Arrange
            AccountLockedException ex =
                    new AccountLockedException("Cuenta bloqueada temporalmente por seguridad");

            // Act
            ResponseEntity<ErrorResponseDTO> response = handler.handleAccountLocked(ex);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.LOCKED);
            ErrorResponseDTO body = response.getBody();
            assertThat(body).isNotNull();
            assertThat(body.getErrorCode()).isEqualTo(423);
            // CORRECCIÓN: Tu código SI devuelve detalles con el tiempo de desbloqueo
            assertThat(body.getDetails()).containsExactly("Demasiados intentos fallidos. La cuenta se desbloqueará automáticamente en 2 minutos.");
        }
    }

    // =========================================================================
    @Nested
    @DisplayName("handleValidation()")
    class HandleValidation {

        @Test
        @DisplayName("Retorna 400 con 'La validación de los datos ha fallado' y los detalles de cada campo")
        void handleValidation_returns400WithFieldDetails() {
            // Arrange
            BindingResult bindingResult = mock(BindingResult.class);
            when(bindingResult.getFieldErrors()).thenReturn(List.of(
                    new FieldError("userRequestDTO", "name",    "Name is required"),
                    new FieldError("userRequestDTO", "password","Password must be at least 8 characters")
            ));

            MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
            when(ex.getBindingResult()).thenReturn(bindingResult);

            // Act
            ResponseEntity<ErrorResponseDTO> response = handler.handleValidation(ex);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            ErrorResponseDTO body = response.getBody();
            assertThat(body).isNotNull();
            assertThat(body.getErrorCode()).isEqualTo(400);
            // Validamos contra el texto en español
            assertThat(body.getMessage()).isEqualTo("La validación de los datos ha fallado");
            assertThat(body.getDetails())
                    .hasSize(2)
                    .containsExactlyInAnyOrder(
                            "name: Name is required",
                            "password: Password must be at least 8 characters"
                    );
        }

        @Test
        @DisplayName("Sin errores de campo retorna lista de details vacía")
        void handleValidation_noFieldErrors_returnsEmptyDetails() {
            // Arrange
            BindingResult bindingResult = mock(BindingResult.class);
            when(bindingResult.getFieldErrors()).thenReturn(List.of());

            MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
            when(ex.getBindingResult()).thenReturn(bindingResult);

            // Act
            ResponseEntity<ErrorResponseDTO> response = handler.handleValidation(ex);

            // Assert
            assertThat(response.getBody().getDetails()).isEmpty();
        }
    }

    // =========================================================================
    @Nested
    @DisplayName("handleGeneral()")
    class HandleGeneral {

        @Test
        @DisplayName("Excepción genérica retorna 500 con mensaje genérico y detalle de la causa")
        void handleGeneral_returns500WithGenericMessageAndCauseDetail() {
            // Arrange
            Exception ex = new RuntimeException("Unexpected DB failure");

            // Act
            ResponseEntity<ErrorResponseDTO> response = handler.handleGeneral(ex);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            ErrorResponseDTO body = response.getBody();
            assertThat(body).isNotNull();
            assertThat(body.getErrorCode()).isEqualTo(500);
            // Validamos contra el texto en español
            assertThat(body.getMessage()).isEqualTo("Ha ocurrido un error inesperado en el servidor");
            assertThat(body.getDetails()).containsExactly("Unexpected DB failure");
        }
    }
}