package com.devsu.hackerearth.backend.account.model.dto;

import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información de una transacción bancaria")
public class TransactionDto {

    @Schema(
            description = "Identificador único de la transacción",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Fecha en la que se realizó la transacción",
            example = "2026-10-07T10:30:00.000+00:00"
    )
    private Date date;

    @NotBlank(message = "El tipo de transacción es obligatorio")
    @Schema(
            description = "Tipo de transacción",
            example = "DEPOSIT"
    )
    private String type;

    @Schema(
            description = "Valor de la transacción. Los depósitos son positivos y los retiros negativos",
            example = "500.00"
    )
    private double amount;

    @Schema(
            description = "Saldo resultante después de aplicar la transacción",
            example = "1500.00"
    )
    private double balance;

    @NotNull(message = "La cuenta es obligatoria")
    @Schema(
            description = "Identificador de la cuenta asociada a la transacción",
            example = "1"
    )
    private Long accountId;
}