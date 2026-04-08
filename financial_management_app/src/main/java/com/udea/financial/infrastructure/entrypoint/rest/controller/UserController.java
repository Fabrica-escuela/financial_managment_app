package com.udea.financial.infrastructure.entrypoint.rest.controller;

import com.udea.financial.domain.usecase.UserUseCase;
import com.udea.financial.infrastructure.entrypoint.rest.constants.SwaggerConstants;
import com.udea.financial.infrastructure.entrypoint.rest.dto.ErrorResponseDTO;
import com.udea.financial.infrastructure.entrypoint.rest.dto.UserRequestDTO;
import com.udea.financial.infrastructure.entrypoint.rest.dto.UserResponseDTO;
import com.udea.financial.infrastructure.entrypoint.rest.mapper.UserRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = SwaggerConstants.TAG_USER, description = SwaggerConstants.TAG_USER_DESCRIPTION)
public class UserController {

    private final UserUseCase userUseCase;
    private final UserRestMapper userRestMapper;

    @Operation(summary = SwaggerConstants.USER_CREATE_SUMMARY, description = SwaggerConstants.USER_CREATE_DESCRIPTION)
    @ApiResponses({
            @ApiResponse(responseCode = SwaggerConstants.CODE_201, description = SwaggerConstants.RESP_CREATED),
            @ApiResponse(responseCode = SwaggerConstants.CODE_400, description = SwaggerConstants.RESP_BAD_REQUEST,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = SwaggerConstants.CODE_409, description = SwaggerConstants.RESP_CONFLICT,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping
    public ResponseEntity<Void> createUser(@Valid @RequestBody UserRequestDTO request) {
        log.info("Creating user with email: {}", request.getEmail());
        userUseCase.saveUser(userRestMapper.toDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = SwaggerConstants.USER_GET_BY_ID_SUMMARY, description = SwaggerConstants.USER_GET_BY_ID_DESCRIPTION)
    @ApiResponses({
            @ApiResponse(responseCode = SwaggerConstants.CODE_200, description = SwaggerConstants.RESP_OK,
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = SwaggerConstants.CODE_404, description = SwaggerConstants.RESP_NOT_FOUND,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userRestMapper.toResponse(userUseCase.findUserById(id)));
    }

    @Operation(summary = SwaggerConstants.USER_GET_BY_EMAIL_SUMMARY, description = SwaggerConstants.USER_GET_BY_EMAIL_DESCRIPTION)
    @ApiResponses({
            @ApiResponse(responseCode = SwaggerConstants.CODE_200, description = SwaggerConstants.RESP_OK,
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = SwaggerConstants.CODE_404, description = SwaggerConstants.RESP_NOT_FOUND,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userRestMapper.toResponse(userUseCase.findUserByEmail(email)));
    }

    @Operation(summary = SwaggerConstants.USER_GET_ALL_SUMMARY, description = SwaggerConstants.USER_GET_ALL_DESCRIPTION)
    @ApiResponses({
            @ApiResponse(responseCode = SwaggerConstants.CODE_200, description = SwaggerConstants.RESP_OK,
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponseDTO.class))))
    })
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userUseCase.allUsers().stream()
                .map(userRestMapper::toResponse)
                .toList();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = SwaggerConstants.USER_DELETE_SUMMARY, description = SwaggerConstants.USER_DELETE_DESCRIPTION)
    @ApiResponses({
            @ApiResponse(responseCode = SwaggerConstants.CODE_200, description = SwaggerConstants.RESP_OK),
            @ApiResponse(responseCode = SwaggerConstants.CODE_404, description = SwaggerConstants.RESP_NOT_FOUND,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Deleting user with id: {}", id);
        userUseCase.deleteUserById(id);
        return ResponseEntity.ok().build();
    }
}
