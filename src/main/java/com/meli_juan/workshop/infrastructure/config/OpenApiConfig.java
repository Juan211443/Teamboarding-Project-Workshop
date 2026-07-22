package com.meli_juan.workshop.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI workshopOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Workshop API")
                        .description("REST API for product and order management")
                        .version("1.0.0"));
    }
}
