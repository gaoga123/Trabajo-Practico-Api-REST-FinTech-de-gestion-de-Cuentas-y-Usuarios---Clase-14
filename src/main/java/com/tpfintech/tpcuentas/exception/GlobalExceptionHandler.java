package com.tpfintech.tpcuentas.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;

// Centralizo las respuestas de error para mantener los controladores simples.
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> manejarRecursoNoEncontrado(
            RecursoNoEncontradoException e) {

        return construirRespuesta(
                HttpStatus.NOT_FOUND,
                e.getMessage());
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<Map<String, Object>> manejarReglaNegocio(
            ReglaNegocioException e) {

        return construirRespuesta(
                HttpStatus.CONFLICT,
                e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidacion(
            MethodArgumentNotValidException e) {

        StringBuilder mensaje = new StringBuilder(
                "La solicitud contiene datos inválidos.");

        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            mensaje.append(" ")
                    .append(error.getField())
                    .append(": ")
                    .append(error.getDefaultMessage());
        }

        return construirRespuesta(
                HttpStatus.BAD_REQUEST,
                mensaje.toString());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> manejarRestricciones(
            ConstraintViolationException e) {

        return construirRespuesta(
                HttpStatus.BAD_REQUEST,
                "Uno o más parámetros no cumplen las validaciones.");
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<Map<String, Object>> manejarSolicitudIncorrecta(
            Exception e) {

        return construirRespuesta(
                HttpStatus.BAD_REQUEST,
                "La solicitud contiene un formato o parámetro inválido.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> manejarIntegridad(
            DataIntegrityViolationException e) {

        return construirRespuesta(
                HttpStatus.CONFLICT,
                "La operación entra en conflicto con una restricción de datos.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarErrorInesperado(
            Exception e) {

        return construirRespuesta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error interno al procesar la solicitud.");
    }

    private ResponseEntity<Map<String, Object>> construirRespuesta(
            HttpStatus estado,
            String mensaje) {

        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("fecha", LocalDateTime.now());
        cuerpo.put("estado", estado.value());
        cuerpo.put("error", estado.getReasonPhrase());
        cuerpo.put("mensaje", mensaje);

        return ResponseEntity.status(estado).body(cuerpo);
    }
}
