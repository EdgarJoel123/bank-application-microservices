package com.devsu.hackerearth.backend.account.model.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información del estado de cuenta de un cliente")
public class BankStatementDto {

    @Schema(
            description = "Fecha de la transacción",
            example = "2026-10-07T10:30:00.000+00:00"
    )
    private Date date;

    @Schema(
            description = "Nombre del cliente",
            example = "Juan Pérez"
    )
    private String client;

    @Schema(
            description = "Número de la cuenta bancaria",
            example = "001-001"
    )
    private String accountNumber;

    @Schema(
            description = "Tipo de cuenta",
            example = "SAVINGS"
    )
    private String accountType;

    @Schema(
            description = "Saldo inicial de la cuenta",
            example = "1000.00"
    )
    private String initialAmount;

    @JsonProperty("isActive")
    @Schema(
            description = "Indica si la cuenta se encuentra activa",
            example = "true"
    )
    private boolean isActive;

    @Schema(
            description = "Tipo de transacción realizada",
            example = "DEPOSIT"
    )
    private String transactionType;

    @Schema(
            description = "Valor de la transacción",
            example = "500.00"
    )
    private double amount;

    @Schema(
            description = "Saldo resultante después de la transacción",
            example = "1500.00"
    )
    private double balance;
}