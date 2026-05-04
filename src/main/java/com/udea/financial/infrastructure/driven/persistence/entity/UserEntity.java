package com.udea.financial.infrastructure.driven.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUser;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, updatable = false)
    private LocalDateTime registrationDate;

    @Column(nullable = false)
    private int failedLoginAttempts;

    private LocalDateTime lockTime;

    @PrePersist
    protected void onCreate() {
        this.registrationDate = LocalDateTime.now();
        this.failedLoginAttempts = 0;
    }
}
