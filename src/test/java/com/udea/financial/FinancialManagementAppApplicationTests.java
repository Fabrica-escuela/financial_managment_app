package com.udea.financial;

import com.udea.financial.application.FinancialManagementAppApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = FinancialManagementAppApplication.class)
@ActiveProfiles("test")
class FinancialManagementAppApplicationTests {

	@Test
	void contextLoads() {
	}

}
