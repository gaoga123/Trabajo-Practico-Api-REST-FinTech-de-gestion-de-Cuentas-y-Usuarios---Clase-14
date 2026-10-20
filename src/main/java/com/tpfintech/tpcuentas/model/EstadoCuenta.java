package com.tpfintech.tpcuentas.model;

// Defino los estados posibles para evitar representar el estado de una cuenta con texto libre.
public enum EstadoCuenta {
    ACTIVA,
    BLOQUEADA,
    CERRADA
}