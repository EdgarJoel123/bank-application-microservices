package com.devsu.hackerearth.backend.client.model.dto;

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
@Schema(description = "Información parcial para actualización del cliente")
public class PartialClientDto {

    @JsonProperty("isActive")
    @Schema(
            description = "Indica si el cliente se encuentra activo",
            example = "true"
    )
    private boolean isActive;
}