package com.tpfintech.tpcuentas.dto;

import com.tpfintech.tpcuentas.model.TipoCuenta;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CuentaRequest(

        @NotBlank(message = "El número de cuenta es obligatorio.") @Size(max = 30, message = "El número de cuenta no puede superar los 30 caracteres.") String numeroCuenta,

        @NotNull(message = "El tipo de cuenta es obligatorio.") TipoCuenta tipo,

        @NotNull(message = "El saldo es obligatorio.") @DecimalMin(value = "0.00", message = "El saldo no puede ser negativo.") BigDecimal saldo,

        @NotBlank(message = "La moneda es obligatoria.") @Pattern(regexp = "[A-Z]{3}", message = "La moneda debe tener tres letras mayúsculas, por ejemplo ARS.") String moneda,

        @NotNull(message = "El ID del usuario es obligatorio.") Long usuarioId

) {
}
