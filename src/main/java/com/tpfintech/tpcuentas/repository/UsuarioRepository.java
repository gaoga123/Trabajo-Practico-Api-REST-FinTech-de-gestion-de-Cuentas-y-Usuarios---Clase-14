package com.tpfintech.tpcuentas.repository;

import com.tpfintech.tpcuentas.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Delego el acceso a los datos en Spring Data JPA para evitar consultas CRUD repetitivas.
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Uso Optional porque puede no existir un usuario con ese email.
    Optional<Usuario> findByEmail(String email);

    // Verifico si el email ya está registrado para controlar su unicidad.
    boolean existsByEmail(String email);

    // Verifico si el teléfono ya está registrado cuando las reglas de negocio lo
    // requieren.
    boolean existsByTelefono(String telefono);
}