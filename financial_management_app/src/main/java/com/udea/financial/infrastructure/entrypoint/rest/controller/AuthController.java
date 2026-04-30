package com.udea.financial.infrastructure.entrypoint.rest.controller;

import com.udea.financial.domain.model.User;
import com.udea.financial.domain.usecase.AuthUseCase;
import com.udea.financial.infrastructure.driven.security.jwt.JwtProvider;
import com.udea.financial.infrastructure.entrypoint.rest.constants.SwaggerConstants;
import com.udea.financial.infrastructure.entrypoint.rest.dto.ErrorResponseDTO;
import com.udea.financial.infrastructure.entrypoint.rest.dto.LoginRequestDTO;
import com.udea.financial.infrastructure.entrypoint.rest.dto.LoginResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = SwaggerConstants.TAG_AUTH, description = SwaggerConstants.TAG_AUTH_DESCRIPTION)
public class AuthController {

    private final AuthUseCase authUseCase;
    private final JwtProvider jwtProvider;

    @Operation(summary = SwaggerConstants.AUTH_LOGIN_SUMMARY, description = SwaggerConstants.AUTH_LOGIN_DESCRIPTION)
    @ApiResponses({
            @ApiResponse(responseCode = SwaggerConstants.CODE_200, description = SwaggerConstants.RESP_OK,
                    content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = SwaggerConstants.CODE_401, description = SwaggerConstants.RESP_UNAUTHORIZED,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = SwaggerConstants.CODE_423, description = SwaggerConstants.RESP_LOCKED,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        log.info("Login attempt for email: {}", request.getEmail());
        User user = authUseCase.login(request.getEmail(), request.getPassword());
        String token = jwtProvider.generateToken(user.getEmail(), user.getIdUser());
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }
}
