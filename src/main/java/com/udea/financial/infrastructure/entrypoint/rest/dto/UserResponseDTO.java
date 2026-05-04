package com.udea.financial.infrastructure.entrypoint.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response body with user information")
public class UserResponseDTO {

    @Schema(description = "Unique identifier of the user", example = "1")
    private Long idUser;

    @Schema(description = "Full name of the user", example = "John Doe")
    private String name;

    @Schema(description = "Email address of the user", example = "john.doe@email.com")
    private String email;

    @Schema(description = "Date and time when the user was registered", example = "2025-01-15T10:30:00")
    private LocalDateTime registrationDate;
}
