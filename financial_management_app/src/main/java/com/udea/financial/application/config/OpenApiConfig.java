package com.udea.financial.application.config;

import com.udea.financial.infrastructure.entrypoint.rest.constants.SwaggerConstants;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(SwaggerConstants.API_TITLE)
                        .description(SwaggerConstants.API_DESCRIPTION)
                        .version(SwaggerConstants.API_VERSION));
    }
}
