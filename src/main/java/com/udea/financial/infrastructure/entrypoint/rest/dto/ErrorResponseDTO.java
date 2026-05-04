package com.udea.financial.infrastructure.entrypoint.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standard error response")
public class ErrorResponseDTO {

    @Schema(description = "HTTP status code", example = "404")
    private int errorCode;

    @Schema(description = "Error message", example = "User not found with id: 1")
    private String message;

    @Schema(description = "Detailed error information")
    private List<String> details;
}
