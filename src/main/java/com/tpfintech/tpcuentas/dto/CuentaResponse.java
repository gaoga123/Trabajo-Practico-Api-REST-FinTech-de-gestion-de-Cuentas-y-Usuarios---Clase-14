package com.tpfintech.tpcuentas.dto;

import com.tpfintech.tpcuentas.model.EstadoCuenta;
import com.tpfintech.tpcuentas.model.TipoCuenta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Separo los datos de salida del modelo para controlar qué información expongo en la API.
public record CuentaResponse(
                Long id,
                String numeroCuenta,
                TipoCuenta tipo,
                BigDecimal saldo,
                String moneda,
                EstadoCuenta estado,
                LocalDateTime fechaCreacion,
                Long usuarioId) {
}