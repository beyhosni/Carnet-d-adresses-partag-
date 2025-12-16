package com.carnet.adresses.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI carnetOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Carnet d'Adresses Partagé API")
                        .description("API pour la gestion de contacts partagés avec support photo et recherche.")
                        .version("v1.0.0"));
    }
}
