package com.tpfintech.tpcuentas.service;

import com.tpfintech.tpcuentas.exception.ReglaNegocioException;
import com.tpfintech.tpcuentas.exception.RecursoNoEncontradoException;
import com.tpfintech.tpcuentas.model.Cuenta;
import com.tpfintech.tpcuentas.model.EstadoCuenta;
import com.tpfintech.tpcuentas.model.Usuario;
import com.tpfintech.tpcuentas.repository.CuentaRepository;
import com.tpfintech.tpcuentas.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.List;

// Centralizo las reglas de negocio de las cuentas para no mezclarlas con el controlador.
@Service
// Uso transacciones para agrupar las operaciones de persistencia y mantener la
// consistencia de los datos.
@Transactional
public class CuentaService {

    private final CuentaRepository cuentaRepository;
    private final UsuarioRepository usuarioRepository;

    // Inyecto ambos repositorios por constructor para declarar las dependencias del
    // servicio.
    public CuentaService(
            CuentaRepository cuentaRepository,
            UsuarioRepository usuarioRepository) {
        this.cuentaRepository = cuentaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Cuenta crear(Cuenta cuenta, Long usuarioId) {
        // Evito registrar números de cuenta duplicados.
        if (cuentaRepository.existsByNumeroCuenta(cuenta.getNumeroCuenta())) {
            throw new ReglaNegocioException(
                    "Ya existe una cuenta con ese número.");
        }

        // Valido estas reglas también en el servicio para no depender únicamente del
        // DTO.
        validarSaldo(cuenta.getSaldo());
        validarMoneda(cuenta.getMoneda());

        // Busco al usuario y distingo el caso en que no existe.
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el usuario con ID: " + usuarioId));

        // Impido crear cuentas para usuarios que no están activos.
        if (!usuario.estaActivo()) {
            throw new ReglaNegocioException(
                    "No se pueden crear cuentas para un usuario inactivo.");
        }

        // Asigno la relación y el estado inicial antes de guardar la cuenta.
        cuenta.setUsuario(usuario);
        cuenta.setEstado(EstadoCuenta.ACTIVA);

        try {
            // Fuerzo la escritura para detectar restricciones de la base de datos.
            return cuentaRepository.saveAndFlush(cuenta);
        } catch (DataIntegrityViolationException e) {
            // Traduzco el error de persistencia a una excepción de negocio.
            throw new ReglaNegocioException(
                    "No se pudo crear la cuenta por un conflicto con los datos.");
        }
    }

    // Marco la consulta como de solo lectura porque no necesito modificar datos.
    @Transactional(readOnly = true)
    public List<Cuenta> listarTodas() {
        return cuentaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Cuenta buscarPorId(Long id) {
        // Uso una excepción específica para informar cuando la cuenta no existe.
        return cuentaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró la cuenta con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Cuenta> listarPorUsuario(Long usuarioId) {
        // Verifico la existencia del usuario para distinguirlo de un usuario sin
        // cuentas.
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new RecursoNoEncontradoException(
                    "No se encontró el usuario con ID: " + usuarioId);
        }

        return cuentaRepository.findByUsuario_Id(usuarioId);
    }

    public Cuenta actualizar(Long id, Cuenta datosActualizados, Long usuarioId) {
        // Recupero la cuenta existente para modificarla sin perder su identidad.
        Cuenta cuenta = buscarPorId(id);

        // Permito conservar el número actual, pero impido usar el de otra cuenta.
        cuentaRepository.findByNumeroCuenta(datosActualizados.getNumeroCuenta())
                .filter(existente -> !existente.getId().equals(id))
                .ifPresent(existente -> {
                    throw new ReglaNegocioException(
                            "El número de cuenta ya está en uso.");
                });

        validarSaldo(datosActualizados.getSaldo());
        validarMoneda(datosActualizados.getMoneda());

        // Compruebo que el usuario asociado exista y pueda tener una cuenta.
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el usuario con ID: " + usuarioId));

        if (!usuario.estaActivo()) {
            throw new ReglaNegocioException(
                    "No se puede asignar una cuenta a un usuario inactivo.");
        }

        // Actualizo los campos permitidos y mantengo la entidad recuperada.
        cuenta.setNumeroCuenta(datosActualizados.getNumeroCuenta());
        cuenta.setTipo(datosActualizados.getTipo());
        cuenta.setSaldo(datosActualizados.getSaldo());
        cuenta.setMoneda(datosActualizados.getMoneda());
        cuenta.setUsuario(usuario);

        try {
            // Fuerzo la escritura para detectar restricciones de la base de datos.
            return cuentaRepository.saveAndFlush(cuenta);
        } catch (DataIntegrityViolationException e) {
            // Mantengo el error de persistencia dentro del modelo de excepciones del
            // servicio.
            throw new ReglaNegocioException(
                    "No se pudo actualizar la cuenta por un conflicto con los datos.");
        }
    }

    public void eliminar(Long id) {
        // Busco primero la cuenta para informar si no existe antes de eliminarla.
        Cuenta cuenta = buscarPorId(id);
        cuentaRepository.delete(cuenta);
    }

    private void validarSaldo(BigDecimal saldo) {
        // Rechazo saldos nulos o negativos para mantener una regla de negocio uniforme.
        if (saldo == null || saldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new ReglaNegocioException(
                    "El saldo no puede ser nulo ni negativo.");
        }
    }

    private void validarMoneda(String moneda) {
        // Exijo tres letras mayúsculas para mantener un formato uniforme para los
        // códigos de moneda.
        if (moneda == null || !moneda.matches("[A-Z]{3}")) {
            throw new ReglaNegocioException(
                    "La moneda debe ser un código de tres letras mayúsculas, por ejemplo ARS.");
        }
    }
}