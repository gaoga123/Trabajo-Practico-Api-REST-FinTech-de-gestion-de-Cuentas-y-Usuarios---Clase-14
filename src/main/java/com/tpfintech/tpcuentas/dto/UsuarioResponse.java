package com.tpfintech.tpcuentas.dto;

import java.time.LocalDateTime;

// Defino una respuesta específica para exponer los datos del usuario sin devolver directamente la entidad.
public record UsuarioResponse(
                Long id,
                String nombre,
                String apellido,
                String email,
                String telefono,
                LocalDateTime fechaAlta,
                boolean activo) {
}