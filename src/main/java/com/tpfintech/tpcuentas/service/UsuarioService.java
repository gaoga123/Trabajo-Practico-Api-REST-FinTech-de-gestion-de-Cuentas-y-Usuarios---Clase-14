package com.tpfintech.tpcuentas.service;

import com.tpfintech.tpcuentas.exception.ReglaNegocioException;
import com.tpfintech.tpcuentas.exception.RecursoNoEncontradoException;
import com.tpfintech.tpcuentas.model.Usuario;
import com.tpfintech.tpcuentas.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario crear(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new ReglaNegocioException(
                    "Ya existe un usuario con ese email.");
        }

        if (usuarioRepository.existsByTelefono(usuario.getTelefono())) {
            throw new ReglaNegocioException(
                    "Ya existe un usuario con ese teléfono.");
        }

        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el usuario con ID: " + id));
    }

    public Usuario actualizar(Long id, Usuario datosActualizados) {
        Usuario usuario = buscarPorId(id);

        usuarioRepository.findByEmail(datosActualizados.getEmail())
                .filter(existente -> !Objects.equals(existente.getId(), id))
                .ifPresent(existente -> {
                    throw new ReglaNegocioException(
                            "El email ya pertenece a otro usuario.");
                });

        if (!Objects.equals(usuario.getTelefono(), datosActualizados.getTelefono())
            && usuarioRepository.existsByTelefono(datosActualizados.getTelefono())) {
            throw new ReglaNegocioException(
                "El teléfono ya pertenece a otro usuario.");
        }

        usuario.setNombre(datosActualizados.getNombre());
        usuario.setApellido(datosActualizados.getApellido());
        usuario.setEmail(datosActualizados.getEmail());
        usuario.setTelefono(datosActualizados.getTelefono());

        return usuarioRepository.save(usuario);
    }

    public void desactivar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.desactivar();
        usuarioRepository.save(usuario);
    }

    public void activar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.activar();
        usuarioRepository.save(usuario);
    }
}
