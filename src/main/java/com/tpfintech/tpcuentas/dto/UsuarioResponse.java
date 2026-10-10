package com.tpfintech.tpcuentas.dto;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String nombre,
        String apellido,
        String email,
        String telefono,
        LocalDateTime fechaAlta,
        boolean activo) {
}
