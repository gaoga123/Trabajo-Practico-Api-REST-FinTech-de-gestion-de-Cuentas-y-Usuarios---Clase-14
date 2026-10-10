package com.tpfintech.tpcuentas.dto;

import com.tpfintech.tpcuentas.model.EstadoCuenta;
import com.tpfintech.tpcuentas.model.TipoCuenta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
