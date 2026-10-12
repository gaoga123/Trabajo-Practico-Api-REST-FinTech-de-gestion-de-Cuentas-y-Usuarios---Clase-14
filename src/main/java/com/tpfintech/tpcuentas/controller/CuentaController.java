package com.tpfintech.tpcuentas.controller;

import com.tpfintech.tpcuentas.dto.CuentaRequest;
import com.tpfintech.tpcuentas.dto.CuentaResponse;
import com.tpfintech.tpcuentas.model.Cuenta;
import com.tpfintech.tpcuentas.service.CuentaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Cuentas", description = "Gestión de cuentas bancarias")
@RestController
@RequestMapping("/cuentas")
public class CuentaController {

    private final CuentaService cuentaService;

    public CuentaController(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    @PostMapping
    public ResponseEntity<CuentaResponse> crear(
            @Valid @RequestBody CuentaRequest request) {

        Cuenta cuenta = new Cuenta(
                request.numeroCuenta(),
                request.tipo(),
                request.saldo(),
                request.moneda(),
                null);

        Cuenta creada = cuentaService.crear(cuenta, request.usuarioId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(convertir(creada));
    }

    @GetMapping
    public ResponseEntity<List<CuentaResponse>> listarTodas() {
        List<CuentaResponse> cuentas = cuentaService.listarTodas()
                .stream()
                .map(this::convertir)
                .toList();

        return ResponseEntity.ok(cuentas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuentaResponse> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                convertir(cuentaService.buscarPorId(id)));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<CuentaResponse>> listarPorUsuario(
            @PathVariable Long usuarioId) {

        List<CuentaResponse> cuentas = cuentaService.listarPorUsuario(usuarioId)
                .stream()
                .map(this::convertir)
                .toList();

        return ResponseEntity.ok(cuentas);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CuentaResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CuentaRequest request) {

        Cuenta datos = new Cuenta(
                request.numeroCuenta(),
                request.tipo(),
                request.saldo(),
                request.moneda(),
                null);

        Cuenta actualizada = cuentaService.actualizar(
                id, datos, request.usuarioId());

        return ResponseEntity.ok(convertir(actualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        cuentaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private CuentaResponse convertir(Cuenta cuenta) {
        return new CuentaResponse(
                cuenta.getId(),
                cuenta.getNumeroCuenta(),
                cuenta.getTipo(),
                cuenta.getSaldo(),
                cuenta.getMoneda(),
                cuenta.getEstado(),
                cuenta.getFechaCreacion(),
                cuenta.getUsuario().getId());
    }
}
