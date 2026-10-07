package com.devsu.hackerearth.backend.client.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información del cliente")
public class ClientDto {

    @Schema(
            description = "Identificador único del cliente",
            example = "1"
    )
    private Long id;

    @NotBlank(message = "El DNI es obligatorio")
    @Schema(
            description = "Número de identificación del cliente",
            example = "1801234567"
    )
    private String dni;

    @NotBlank(message = "El nombre es obligatorio")
    @Schema(
            description = "Nombre completo del cliente",
            example = "Juan Pérez"
    )
    private String name;

    
    @NotBlank(message = "La contraseña es obligatoria")
@Schema(
        description = "Contraseña asociada al cliente",
        example = "123456"
)
private String password;

    @Schema(
            description = "Género del cliente",
            example = "M"
    )
    private String gender;

    @Schema(
            description = "Edad del cliente",
            example = "30"
    )
    private int age;

    @Schema(
            description = "Dirección de residencia del cliente",
            example = "Ambato, Tungurahua"
    )
    private String address;

    @Schema(
            description = "Número telefónico del cliente",
            example = "0999999999"
    )
    private String phone;

    @JsonProperty("isActive")
    @Schema(
            description = "Indica si el cliente se encuentra activo",
            example = "true"
    )
    private boolean isActive;
}