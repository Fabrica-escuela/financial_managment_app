package com.udea.financial.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.udea.financial")
public class FinancialManagementAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinancialManagementAppApplication.class, args);
	}

}
