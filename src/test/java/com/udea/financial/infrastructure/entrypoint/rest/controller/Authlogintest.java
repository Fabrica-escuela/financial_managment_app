package com.udea.financial.infrastructure.entrypoint.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.udea.financial.application.FinancialManagementAppApplication;
import com.udea.financial.infrastructure.driven.persistence.entity.UserEntity;
import com.udea.financial.infrastructure.driven.persistence.repository.UserRepository;
import com.udea.financial.infrastructure.entrypoint.rest.dto.LoginRequestDTO;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = FinancialManagementAppApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthLoginTest {

    private static final String LOGIN_URL = "/api/auth/login";
    private static final String TEST_EMAIL    = "test.user@example.com";
    private static final String TEST_PASSWORD = "securePass123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        userRepository.deleteAll();
        userRepository.flush(); // Asegura la limpieza física en H2
    }

    /**
     * Helper para persistir un usuario y limpiar el contexto de persistencia.
     * Esto fuerza a que el UseCase lea el estado real de la DB.
     */
    private UserEntity persistUser(int attempts, LocalDateTime lockTime) {
        UserEntity user = UserEntity.builder()
                .name("Test User")
                .email(TEST_EMAIL)
                .password(passwordEncoder.encode(TEST_PASSWORD))
                .registrationDate(LocalDateTime.now())
                .failedLoginAttempts(attempts)
                .lockTime(lockTime)
                .build();

        UserEntity saved = userRepository.saveAndFlush(user);
        entityManager.clear(); // CRÍTICO: Evita que Hibernate use la versión en caché
        return saved;
    }

    private String json(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    @Test
    @DisplayName("CP-02-01: Login exitoso devuelve 200 y reinicia intentos")
    void cp0201_successfulLogin_returns200WithTokenAndResetsFailedAttempts() throws Exception {
        persistUser(2, null);
        LoginRequestDTO request = new LoginRequestDTO(TEST_EMAIL, TEST_PASSWORD);

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());

        UserEntity updated = userRepository.findByEmail(TEST_EMAIL).orElseThrow();
        assert updated.getFailedLoginAttempts() == 0;
    }

    @Test
    @DisplayName("CP-02-02: Password incorrecto devuelve 401 e incrementa intentos")
    void cp0202_wrongPassword_returns401AndIncrementsFailedAttempts() throws Exception {
        persistUser(0, null);
        LoginRequestDTO request = new LoginRequestDTO(TEST_EMAIL, "wrongPassword!");

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isUnauthorized());

        UserEntity updated = userRepository.findByEmail(TEST_EMAIL).orElseThrow();
        assert updated.getFailedLoginAttempts() == 1;
    }

    @Test
    @DisplayName("CP-02-03: Usuario inexistente devuelve 401")
    void cp0203_nonExistentUser_returns401WithoutRevealingEmailAbsence() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO("ghost@example.com", "anyPassword");

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("CP-02-04: Quinto intento fallido bloquea la cuenta (423)")
    void cp0204_fifthFailedAttempt_locksAccountAndSetsLockTime() throws Exception {
        // Arrange: Usuario con 4 intentos previos
        UserEntity initialUser = persistUser(4, null);
        Long userId = initialUser.getIdUser();

        LoginRequestDTO request = new LoginRequestDTO(TEST_EMAIL, "wrongPassword!");

        // Act & Assert: El login número 5 debe disparar el status 423
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andDo(print())
                .andExpect(status().isLocked())
                .andExpect(jsonPath("$.errorCode").value(423));

        // Verificación en base de datos
        UserEntity finalStatus = userRepository.findById(userId).orElseThrow();
        assert finalStatus.getFailedLoginAttempts() >= 5;
        assert finalStatus.getLockTime() != null;
    }

    @Test
    @DisplayName("CP-02-05: Login en cuenta bloqueada devuelve 423")
    void cp0205_loginOnLockedAccount_returns423WhileLockPeriodActive() throws Exception {
        // Arrange: Usuario ya bloqueado (5 intentos y lockTime presente)
        persistUser(5, LocalDateTime.now());

        LoginRequestDTO request = new LoginRequestDTO(TEST_EMAIL, TEST_PASSWORD);

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isLocked())
                .andExpect(jsonPath("$.errorCode").value(423));
    }
}