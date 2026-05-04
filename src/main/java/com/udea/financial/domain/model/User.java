package com.udea.financial.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long idUser;
    private String name;
    private String email;
    private String password;
    private LocalDateTime registrationDate;
    private int failedLoginAttempts;
    private LocalDateTime lockTime;
}
