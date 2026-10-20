package com.tpfintech.tpcuentas.service;

import com.tpfintech.tpcuentas.exception.ReglaNegocioException;
import com.tpfintech.tpcuentas.exception.RecursoNoEncontradoException;
import com.tpfintech.tpcuentas.model.Usuario;
import com.tpfintech.tpcuentas.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

// Centralizo las reglas de negocio de los usuarios en esta capa.
@Service
// Uso transacciones para agrupar las operaciones de persistencia.
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    // Inyecto el repositorio por constructor para declarar la dependencia del
    // servicio.
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario crear(Usuario usuario) {
        // Evito registrar dos usuarios con el mismo email.
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new ReglaNegocioException(
                    "Ya existe un usuario con ese email.");
        }

        // También controlo que el teléfono no esté asociado a otro usuario.
        if (usuarioRepository.existsByTelefono(usuario.getTelefono())) {
            throw new ReglaNegocioException(
                    "Ya existe un usuario con ese teléfono.");
        }

        return usuarioRepository.save(usuario);
    }

    // Marco la consulta como de solo lectura porque no necesito modificar datos.
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        // Uso una excepción específica para informar cuando el usuario no existe.
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el usuario con ID: " + id));
    }

    public Usuario actualizar(Long id, Usuario datosActualizados) {
        // Recupero el usuario existente para conservar su identidad y sus datos no
        // modificables.
        Usuario usuario = buscarPorId(id);

        // Permito conservar el email actual, pero impido asignar el de otro usuario.
        usuarioRepository.findByEmail(datosActualizados.getEmail())
                .filter(existente -> !Objects.equals(existente.getId(), id))
                .ifPresent(existente -> {
                    throw new ReglaNegocioException(
                            "El email ya pertenece a otro usuario.");
                });

        // Compruebo el teléfono solo si cambió respecto del valor actual.
        if (!Objects.equals(usuario.getTelefono(), datosActualizados.getTelefono())
                && usuarioRepository.existsByTelefono(datosActualizados.getTelefono())) {
            throw new ReglaNegocioException(
                    "El teléfono ya pertenece a otro usuario.");
        }

        // Actualizo los campos permitidos sin reemplazar la entidad existente.
        usuario.setNombre(datosActualizados.getNombre());
        usuario.setApellido(datosActualizados.getApellido());
        usuario.setEmail(datosActualizados.getEmail());
        usuario.setTelefono(datosActualizados.getTelefono());

        return usuarioRepository.save(usuario);
    }

    public void desactivar(Long id) {
        Usuario usuario = buscarPorId(id);

        // Delego el cambio de estado en la entidad para centralizar su comportamiento.
        usuario.desactivar();
        usuarioRepository.save(usuario);
    }

    public void activar(Long id) {
        Usuario usuario = buscarPorId(id);

        // Reutilizo el método de la entidad para mantener encapsulada la lógica del
        // estado.
        usuario.activar();
        usuarioRepository.save(usuario);
    }
}