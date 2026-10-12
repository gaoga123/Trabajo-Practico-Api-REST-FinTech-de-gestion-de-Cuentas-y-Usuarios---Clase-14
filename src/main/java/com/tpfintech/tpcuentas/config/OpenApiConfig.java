
package com.tpfintech.tpcuentas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI configuracionOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Cuentas Fintech")
                        .version("1.0.0")
                        .description("""
                                API REST para la gestión de usuarios y cuentas.
                                Permite crear, consultar, actualizar y administrar
                                usuarios y sus cuentas bancarias.
                                """));
    }
}
