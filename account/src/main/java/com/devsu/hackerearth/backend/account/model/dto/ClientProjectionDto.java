package com.devsu.hackerearth.backend.account.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Información proyectada del cliente utilizada por el microservicio de cuentas")
public class ClientProjectionDto {

    @Schema(
            description = "Identificador único del cliente",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Nombre completo del cliente",
            example = "Juan Pérez"
    )
    private String name;

    @JsonProperty("isActive")
    @Schema(
            description = "Indica si el cliente se encuentra activo",
            example = "true"
    )
    private boolean isActive;
}