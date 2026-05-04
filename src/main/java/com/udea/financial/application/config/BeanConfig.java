package com.udea.financial.application.config;

import com.udea.financial.domain.gateway.IPasswordEncryptor;
import com.udea.financial.domain.gateway.IUserRepository;
import com.udea.financial.domain.usecase.AuthUseCase;
import com.udea.financial.domain.usecase.UserUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public UserUseCase userUseCase(IUserRepository userRepository, IPasswordEncryptor passwordEncryptor) {
        return new UserUseCase(userRepository, passwordEncryptor);
    }

    @Bean
    public AuthUseCase authUseCase(IUserRepository userRepository, IPasswordEncryptor passwordEncryptor) {
        return new AuthUseCase(userRepository, passwordEncryptor);
    }
}
