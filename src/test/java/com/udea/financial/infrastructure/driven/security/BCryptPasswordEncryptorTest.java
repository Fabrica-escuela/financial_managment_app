package com.udea.financial.infrastructure.driven.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link BCryptPasswordEncryptor}.
 *
 * Branches covered
 * ─────────────────────────────────────────────────────────────────────────────
 * encrypt()
 *   ✔ produce hash distinto al texto plano
 *   ✔ dos cifrados del mismo texto producen hashes diferentes (salt aleatorio)
 *
 * matches()
 *   ✔ contraseña correcta      → true
 *   ✔ contraseña incorrecta    → false
 *   ✔ hash de otro texto       → false
 */
@DisplayName("BCryptPasswordEncryptor — unit tests")
class BCryptPasswordEncryptorTest {

    private BCryptPasswordEncryptor encryptor;

    @BeforeEach
    void setUp() {
        encryptor = new BCryptPasswordEncryptor();
    }

    // =========================================================================
    @Nested
    @DisplayName("encrypt()")
    class Encrypt {

        @Test
        @DisplayName("El hash producido es distinto al texto plano")
        void encrypt_producesHashDifferentFromPlainText() {
            // Arrange
            String plain = "mySecurePass123";

            // Act
            String hashed = encryptor.encrypt(plain);

            // Assert
            assertThat(hashed).isNotEqualTo(plain);
            assertThat(hashed).startsWith("$2"); // prefijo BCrypt
        }

        @Test
        @DisplayName("Dos cifrados del mismo texto generan hashes distintos (salt aleatorio)")
        void encrypt_sameInputProducesDifferentHashes() {
            // Arrange
            String plain = "samePassword";

            // Act
            String hash1 = encryptor.encrypt(plain);
            String hash2 = encryptor.encrypt(plain);

            // Assert
            assertThat(hash1).isNotEqualTo(hash2);
        }
    }

    // =========================================================================
    @Nested
    @DisplayName("matches()")
    class Matches {

        @Test
        @DisplayName("Contraseña correcta retorna true")
        void matches_correctPassword_returnsTrue() {
            // Arrange
            String plain = "correctPass456";
            String hashed = encryptor.encrypt(plain);

            // Act
            boolean result = encryptor.matches(plain, hashed);

            // Assert
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Contraseña incorrecta retorna false")
        void matches_wrongPassword_returnsFalse() {
            // Arrange
            String hashed = encryptor.encrypt("realPassword");

            // Act
            boolean result = encryptor.matches("wrongPassword", hashed);

            // Assert
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Hash de otro texto retorna false")
        void matches_hashOfDifferentText_returnsFalse() {
            // Arrange
            String hashOfA = encryptor.encrypt("passwordA");

            // Act — intento verificar "passwordB" contra el hash de "passwordA"
            boolean result = encryptor.matches("passwordB", hashOfA);

            // Assert
            assertThat(result).isFalse();
        }
    }
}