package com.tpfintech.tpcuentas.exception;

// Creo una excepción específica para identificar los recursos que no se encuentran.
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        // Aprovecho el mensaje de RuntimeException para describir qué recurso no se
        // encontró.
        super(mensaje);
    }
}