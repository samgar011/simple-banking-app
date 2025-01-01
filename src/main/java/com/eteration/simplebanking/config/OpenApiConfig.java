package com.eteration.simplebanking.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Simple Banking API")
                        .description("API documentation for the Simple Banking application")
                        .version("1.0.0"))
                .externalDocs(new ExternalDocumentation()
                        .description("Project Documentation")
                        .url("http://localhost:8080/swagger-ui/index.html"));
    }
}
