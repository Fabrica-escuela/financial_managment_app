package com.udea.financial.infrastructure.entrypoint.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.udea.financial.application.FinancialManagementAppApplication;
import com.udea.financial.infrastructure.driven.persistence.repository.UserRepository;
import com.udea.financial.infrastructure.entrypoint.rest.dto.UserRequestDTO;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = FinancialManagementAppApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserRegistrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        userRepository.flush();
    }

    private String json(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    @Test
    @DisplayName("CP-01-01: Registro exitoso")
    void cp0101_successfulRegistration_returns201() throws Exception {
        UserRequestDTO request = new UserRequestDTO("John Doe", "john@example.com", "securePass123");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("CP-01-02: Email duplicado devuelve 409")
    void cp0102_duplicateEmail_returns409Conflict() throws Exception {
        // Registrar primero
        UserRequestDTO first = new UserRequestDTO("Jane Doe", "duplicate@example.com", "securePass123");
        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(json(first)));

        // Intentar registrar de nuevo con el mismo email
        UserRequestDTO second = new UserRequestDTO("Another Jane", "duplicate@example.com", "anotherPass456");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(second)))
                .andExpect(status().isConflict())
                // CORRECCIÓN: Ajuste al mensaje real en español
                .andExpect(jsonPath("$.details[0]").value("Ya existe un registro con este correo electrónico"));
    }

    @Test
    @DisplayName("CP-01-03: Password débil devuelve 400")
    void cp0103_weakPassword_returns400BadRequest() throws Exception {
        UserRequestDTO request = new UserRequestDTO("Weak User", "weak.user@example.com", "abc12");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isBadRequest())
                // CORRECCIÓN: Ajuste al mensaje real en español
                .andExpect(jsonPath("$.message").value("La validación de los datos ha fallado"))
                .andExpect(jsonPath("$.details[0]").value(containsString("Password must be at least 8 characters")));
    }

    @Test
    @DisplayName("CP-01-04: Campos vacíos devuelve 400")
    void cp0104_emptyRequiredFields_returns400WithFieldDetails() throws Exception {
        UserRequestDTO request = new UserRequestDTO("", "", "");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isBadRequest())
                // CORRECCIÓN: Ajuste al mensaje real en español
                .andExpect(jsonPath("$.message").value("La validación de los datos ha fallado"))
                .andExpect(jsonPath("$.details", hasItem(containsString("Name is required"))))
                .andExpect(jsonPath("$.details", hasItem(containsString("Email is required"))));
    }
}