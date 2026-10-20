package com.tpfintech.tpcuentas.exception;

// Separo los errores de negocio de los errores relacionados con recursos inexistentes.
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        // Conservo el mensaje para explicar qué regla de negocio impidió la operación.
        super(mensaje);
    }
}