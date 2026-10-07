package com.devsu.hackerearth.backend.account.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información de la cuenta bancaria")
public class AccountDto {

    @Schema(
            description = "Identificador único de la cuenta",
            example = "1"
    )
    private Long id;

    @NotBlank(message = "El número de cuenta es obligatorio")
    @Schema(
            description = "Número de la cuenta bancaria",
            example = "001-001"
    )
    private String number;

    @NotBlank(message = "El tipo de cuenta es obligatorio")
    @Schema(
            description = "Tipo de cuenta",
            example = "SAVINGS"
    )
    private String type;

    @PositiveOrZero(message = "El monto inicial no puede ser negativo")
    @Schema(
            description = "Saldo inicial de la cuenta",
            example = "1000.00"
    )
    private double initialAmount;

    @JsonProperty("isActive")
    @Schema(
            description = "Indica si la cuenta se encuentra activa",
            example = "true"
    )
    private boolean isActive;

    @NotNull(message = "El cliente es obligatorio")
    @Schema(
            description = "Identificador del cliente propietario de la cuenta",
            example = "1"
    )
    private Long clientId;
    
}