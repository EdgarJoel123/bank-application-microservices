package com.devsu.hackerearth.backend.account.model.dto;

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
@Schema(description = "Información parcial para actualización de la cuenta")
public class PartialAccountDto {

    @JsonProperty("isActive")
    @Schema(
            description = "Indica si la cuenta se encuentra activa",
            example = "true"
    )
    private boolean isActive;
}