package com.devsu.hackerearth.backend.account.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI accountOpenAPI() {
        return new OpenAPI()
                .servers(Collections.singletonList(
                        new Server().url(".")
                ))
                .info(new Info()
                        .title("Account Microservice API")
                        .description("API para gestión de cuentas, transacciones y estados de cuenta")
                        .version("1.0.0"));
    }
}