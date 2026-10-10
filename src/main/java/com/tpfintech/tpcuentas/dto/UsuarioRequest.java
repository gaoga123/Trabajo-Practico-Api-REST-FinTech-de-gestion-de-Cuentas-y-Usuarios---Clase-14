package com.tpfintech.tpcuentas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(

        @NotBlank(message = "El nombre es obligatorio.") @Size(max = 100, message = "El nombre no puede superar los 100 caracteres.") String nombre,

        @NotBlank(message = "El apellido es obligatorio.") @Size(max = 100, message = "El apellido no puede superar los 100 caracteres.") String apellido,

        @NotBlank(message = "El email es obligatorio.") @Email(message = "El formato del email no es válido.") @Size(max = 150, message = "El email no puede superar los 150 caracteres.") String email,

        @NotBlank(message = "El teléfono es obligatorio.") @Size(max = 30, message = "El teléfono no puede superar los 30 caracteres.") String telefono

) {
}
