package com.udea.financial.infrastructure.entrypoint.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Response body with JWT token")
public class LoginResponseDTO {

    @Schema(description = "JWT access token")
    private String token;
}
