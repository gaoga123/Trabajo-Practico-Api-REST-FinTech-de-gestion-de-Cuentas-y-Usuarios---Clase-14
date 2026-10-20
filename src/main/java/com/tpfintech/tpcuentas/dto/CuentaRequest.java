package com.tpfintech.tpcuentas.dto;

import com.tpfintech.tpcuentas.model.TipoCuenta;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

// Uso un record para representar los datos de entrada sin agregar código repetitivo.
public record CuentaRequest(

                // Valido el número y su longitud antes de procesar la solicitud.
                @NotBlank(message = "El número de cuenta es obligatorio.") @Size(max = 30, message = "El número de cuenta no puede superar los 30 caracteres.") String numeroCuenta,

                @NotNull(message = "El tipo de cuenta es obligatorio.") TipoCuenta tipo,

                // Uso BigDecimal para representar importes monetarios con precisión decimal.
                @NotNull(message = "El saldo es obligatorio.") @DecimalMin(value = "0.00", message = "El saldo no puede ser negativo.") BigDecimal saldo,

                // Exijo el formato de tres letras mayúsculas para mantener un criterio uniforme
                // en la moneda.
                @NotBlank(message = "La moneda es obligatoria.") @Pattern(regexp = "[A-Z]{3}", message = "La moneda debe tener tres letras mayúsculas, por ejemplo ARS.") String moneda,

                // Recibo el ID del usuario para asociar la cuenta con un usuario existente.
                @NotNull(message = "El ID del usuario es obligatorio.") Long usuarioId

) {
}