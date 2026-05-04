package com.udea.financial.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.udea.financial")
@EnableJpaRepositories(basePackages = "com.udea.financial.infrastructure.driven.persistence.repository")
@EntityScan(basePackages = "com.udea.financial.infrastructure.driven.persistence.entity")
public class FinancialManagementAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinancialManagementAppApplication.class, args);
	}

}
