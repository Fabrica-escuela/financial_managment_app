package com.udea.financial.infrastructure.entrypoint.rest.handler;

import com.udea.financial.domain.exception.AccountLockedException;
import com.udea.financial.domain.exception.DuplicateEmailException;
import com.udea.financial.domain.exception.InvalidCredentialsException;
import com.udea.financial.domain.exception.UserNotFoundException;
import com.udea.financial.infrastructure.entrypoint.rest.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotFound(UserNotFoundException ex) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .errorCode(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .details(List.of("El recurso solicitado no existe en el sistema"))
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponseDTO> handleDuplicateEmail(DuplicateEmailException ex) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .errorCode(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .details(List.of("Ya existe un registro con este correo electrónico"))
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidCredentials(InvalidCredentialsException ex) {
        // Log info para no saturar los logs de error con intentos fallidos normales
        log.info("Intento de login fallido: {}", ex.getMessage());
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .errorCode(HttpStatus.UNAUTHORIZED.value())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccountLocked(AccountLockedException ex) {
        log.warn("Cuenta bloqueada detectada: {}", ex.getMessage());
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .errorCode(HttpStatus.LOCKED.value()) // Código 423
                .message(ex.getMessage())
                .details(List.of("Demasiados intentos fallidos. La cuenta se desbloqueará automáticamente en 2 minutos."))
                .build();
        return ResponseEntity.status(HttpStatus.LOCKED).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .toList();

        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .message("La validación de los datos ha fallado")
                .details(details)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneral(Exception ex) {
        log.error("Error no controlado capturado: ", ex);
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Ha ocurrido un error inesperado en el servidor")
                .details(List.of(ex.getMessage() != null ? ex.getMessage() : "No detail available"))
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}