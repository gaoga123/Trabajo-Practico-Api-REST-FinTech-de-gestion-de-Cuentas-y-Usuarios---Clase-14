package com.tpfintech.tpcuentas.repository;

import com.tpfintech.tpcuentas.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// Aprovecho JpaRepository para reutilizar las operaciones de persistencia sin implementarlas manualmente.
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

    // Uso Optional porque puede no existir una cuenta con ese número.
    Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);

    // Compruebo si el número ya está registrado antes de intentar crear o
    // actualizar una cuenta.
    boolean existsByNumeroCuenta(String numeroCuenta);

    // Aprovecho la navegación por la relación con Usuario para buscar sus cuentas
    // por ID.
    List<Cuenta> findByUsuario_Id(Long usuarioId);
}