package com.tpfintech.tpcuentas.controller;

import com.tpfintech.tpcuentas.dto.UsuarioRequest;
import com.tpfintech.tpcuentas.dto.UsuarioResponse;
import com.tpfintech.tpcuentas.model.Usuario;
import com.tpfintech.tpcuentas.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Usuarios", description = "Gestión de usuarios")
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(
            @Valid @RequestBody UsuarioRequest request) {

        Usuario usuario = new Usuario(
                request.nombre(),
                request.apellido(),
                request.email(),
                request.telefono());

        Usuario creado = usuarioService.crear(usuario);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(convertir(creado));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarTodos() {
        List<UsuarioResponse> usuarios = usuarioService.listarTodos()
                .stream()
                .map(this::convertir)
                .toList();

        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                convertir(usuarioService.buscarPorId(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequest request) {

        Usuario datos = new Usuario(
                request.nombre(),
                request.apellido(),
                request.email(),
                request.telefono());

        Usuario actualizado = usuarioService.actualizar(id, datos);

        return ResponseEntity.ok(convertir(actualizado));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<UsuarioResponse> activar(
            @PathVariable Long id) {

        usuarioService.activar(id);

        return ResponseEntity.ok(
                convertir(usuarioService.buscarPorId(id)));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<UsuarioResponse> desactivar(
            @PathVariable Long id) {

        usuarioService.desactivar(id);

        return ResponseEntity.ok(
                convertir(usuarioService.buscarPorId(id)));
    }

    private UsuarioResponse convertir(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getFechaAlta(),
                usuario.isActivo());
    }
}
