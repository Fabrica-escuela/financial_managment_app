package com.udea.financial.infrastructure.driven.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link JwtProvider} and {@link JwtAuthenticationFilter}.
 *
 * ── JwtProvider ──────────────────────────────────────────────────────────────
 * generateToken()
 *   ✔ genera un token no nulo con prefijo JWT estándar
 *
 * getEmailFromToken()
 *   ✔ extrae el email correcto del subject
 *
 * getUserIdFromToken()
 *   ✔ extrae el userId correcto del claim
 *
 * validateToken()
 *   ✔ token válido             → true
 *   ✔ token manipulado         → false
 *   ✔ string vacío             → false
 *
 * ── JwtAuthenticationFilter ──────────────────────────────────────────────────
 * doFilterInternal()
 *   ✔ sin header Authorization → no autentica, continúa la cadena
 *   ✔ header sin "Bearer "     → no autentica, continúa la cadena
 *   ✔ token inválido           → no autentica, continúa la cadena
 *   ✔ token válido             → autentica en SecurityContext, continúa la cadena
 */

// ─────────────────────────────────────────────────────────────────────────────
// JwtProvider
// ─────────────────────────────────────────────────────────────────────────────
@DisplayName("JwtProvider — unit tests")
class JwtProviderTest {

    // Clave de 64 caracteres (512 bits) para HS512 / cumple requisito JJWT
    private static final String SECRET =
            "dGVzdHNlY3JldGtleXRoYXRpc2xvbmdlbm91Z2hmb3JoczI1Ng==dGVzdA==";
    private static final long   EXP_MS = 3_600_000L; // 1 hora

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider(SECRET, EXP_MS);
    }

    // =========================================================================
    @Nested
    @DisplayName("generateToken()")
    class GenerateToken {

        @Test
        @DisplayName("Genera un token JWT no vacío con el formato estándar (3 partes)")
        void generateToken_returnsNonEmptyJwtWithThreeParts() {
            // Arrange / Act
            String token = jwtProvider.generateToken("user@example.com", 1L);

            // Assert — formato header.payload.signature
            assertThat(token).isNotBlank();
            assertThat(token.split("\\.")).hasSize(3);
        }
    }

    // =========================================================================
    @Nested
    @DisplayName("getEmailFromToken()")
    class GetEmailFromToken {

        @Test
        @DisplayName("Extrae el email exacto que se usó al generar el token")
        void getEmailFromToken_returnsCorrectEmail() {
            // Arrange
            String token = jwtProvider.generateToken("john@example.com", 10L);

            // Act
            String email = jwtProvider.getEmailFromToken(token);

            // Assert
            assertThat(email).isEqualTo("john@example.com");
        }
    }

    // =========================================================================
    @Nested
    @DisplayName("getUserIdFromToken()")
    class GetUserIdFromToken {

        @Test
        @DisplayName("Extrae el userId exacto que se usó al generar el token")
        void getUserIdFromToken_returnsCorrectUserId() {
            // Arrange
            String token = jwtProvider.generateToken("jane@example.com", 42L);

            // Act
            Long userId = jwtProvider.getUserIdFromToken(token);

            // Assert
            assertThat(userId).isEqualTo(42L);
        }
    }

    // =========================================================================
    @Nested
    @DisplayName("validateToken()")
    class ValidateToken {

        @Test
        @DisplayName("Token generado correctamente es válido")
        void validateToken_validToken_returnsTrue() {
            // Arrange
            String token = jwtProvider.generateToken("valid@example.com", 1L);

            // Act / Assert
            assertThat(jwtProvider.validateToken(token)).isTrue();
        }

        @Test
        @DisplayName("Token manipulado (firma incorrecta) retorna false")
        void validateToken_tamperedToken_returnsFalse() {
            // Arrange — cambiar el último carácter de la firma
            String token = jwtProvider.generateToken("tamper@example.com", 1L);
            String tampered = token.substring(0, token.length() - 1) + "X";

            // Act / Assert
            assertThat(jwtProvider.validateToken(tampered)).isFalse();
        }

        @Test
        @DisplayName("String vacío retorna false sin lanzar excepción")
        void validateToken_emptyString_returnsFalse() {
            assertThat(jwtProvider.validateToken("")).isFalse();
        }

        @Test
        @DisplayName("Token con formato incorrecto (basura) retorna false")
        void validateToken_garbageString_returnsFalse() {
            assertThat(jwtProvider.validateToken("not.a.token")).isFalse();
        }
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// JwtAuthenticationFilter
// ─────────────────────────────────────────────────────────────────────────────
@DisplayName("JwtAuthenticationFilter — unit tests")
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @Mock
    private HttpServletRequest  request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain         filterChain;

    @BeforeEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    // =========================================================================
    @Nested
    @DisplayName("doFilterInternal() — sin token")
    class NoToken {

        @Test
        @DisplayName("Sin header Authorization no autentica y pasa al siguiente filtro")
        void filter_noAuthorizationHeader_doesNotAuthenticate() throws Exception {
            // Arrange
            when(request.getHeader("Authorization")).thenReturn(null);

            // Act
            filter.doFilterInternal(request, response, filterChain);

            // Assert
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(filterChain).doFilter(request, response);
            verify(jwtProvider, never()).validateToken(any());
        }

        @Test
        @DisplayName("Header sin prefijo 'Bearer ' no autentica y pasa al siguiente filtro")
        void filter_headerWithoutBearerPrefix_doesNotAuthenticate() throws Exception {
            // Arrange
            when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz");

            // Act
            filter.doFilterInternal(request, response, filterChain);

            // Assert
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(filterChain).doFilter(request, response);
        }
    }

    // =========================================================================
    @Nested
    @DisplayName("doFilterInternal() — con token")
    class WithToken {

        @Test
        @DisplayName("Token inválido: no autentica y continúa la cadena")
        void filter_invalidToken_doesNotAuthenticate() throws Exception {
            // Arrange
            when(request.getHeader("Authorization")).thenReturn("Bearer invalid.token.here");
            when(jwtProvider.validateToken("invalid.token.here")).thenReturn(false);

            // Act
            filter.doFilterInternal(request, response, filterChain);

            // Assert
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(filterChain).doFilter(request, response);
            verify(jwtProvider, never()).getEmailFromToken(any());
        }

        @Test
        @DisplayName("Token válido: autentica en SecurityContext y continúa la cadena")
        void filter_validToken_setsAuthenticationInContext() throws Exception {
            // Arrange
            String token = "valid.jwt.token";
            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(jwtProvider.validateToken(token)).thenReturn(true);
            when(jwtProvider.getEmailFromToken(token)).thenReturn("user@example.com");

            // Act
            filter.doFilterInternal(request, response, filterChain);

            // Assert
            var auth = SecurityContextHolder.getContext().getAuthentication();
            assertThat(auth).isNotNull();
            assertThat(auth.getPrincipal()).isEqualTo("user@example.com");
            verify(filterChain).doFilter(request, response);
        }
    }
}