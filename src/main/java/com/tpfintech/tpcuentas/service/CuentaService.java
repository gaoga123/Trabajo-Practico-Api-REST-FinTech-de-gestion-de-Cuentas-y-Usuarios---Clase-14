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

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class CuentaService {

    private final CuentaRepository cuentaRepository;
    private final UsuarioRepository usuarioRepository;

    public CuentaService(
            CuentaRepository cuentaRepository,
            UsuarioRepository usuarioRepository) {
        this.cuentaRepository = cuentaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Cuenta crear(Cuenta cuenta, Long usuarioId) {
        if (cuentaRepository.existsByNumeroCuenta(cuenta.getNumeroCuenta())) {
            throw new ReglaNegocioException(
                    "Ya existe una cuenta con ese número.");
        }

        validarSaldo(cuenta.getSaldo());
        validarMoneda(cuenta.getMoneda());

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el usuario con ID: " + usuarioId));

        if (!usuario.estaActivo()) {
            throw new ReglaNegocioException(
                    "No se pueden crear cuentas para un usuario inactivo.");
        }

        cuenta.setUsuario(usuario);
        cuenta.setEstado(EstadoCuenta.ACTIVA);

        return cuentaRepository.save(cuenta);
    }

    @Transactional(readOnly = true)
    public List<Cuenta> listarTodas() {
        return cuentaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Cuenta buscarPorId(Long id) {
        return cuentaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró la cuenta con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Cuenta> listarPorUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new RecursoNoEncontradoException(
                    "No se encontró el usuario con ID: " + usuarioId);
        }

        return cuentaRepository.findByUsuario_Id(usuarioId);
    }

    public Cuenta actualizar(Long id, Cuenta datosActualizados, Long usuarioId) {
        Cuenta cuenta = buscarPorId(id);

        cuentaRepository.findByNumeroCuenta(datosActualizados.getNumeroCuenta())
                .filter(existente -> !existente.getId().equals(id))
                .ifPresent(existente -> {
                    throw new ReglaNegocioException(
                            "El número de cuenta ya está en uso.");
                });

        validarSaldo(datosActualizados.getSaldo());
        validarMoneda(datosActualizados.getMoneda());

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el usuario con ID: " + usuarioId));

        if (!usuario.estaActivo()) {
            throw new ReglaNegocioException(
                    "No se puede asignar una cuenta a un usuario inactivo.");
        }

        cuenta.setNumeroCuenta(datosActualizados.getNumeroCuenta());
        cuenta.setTipo(datosActualizados.getTipo());
        cuenta.setSaldo(datosActualizados.getSaldo());
        cuenta.setMoneda(datosActualizados.getMoneda());
        cuenta.setUsuario(usuario);

        return cuentaRepository.save(cuenta);
    }

    public void eliminar(Long id) {
        Cuenta cuenta = buscarPorId(id);
        cuentaRepository.delete(cuenta);
    }

    private void validarSaldo(BigDecimal saldo) {
        if (saldo == null || saldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new ReglaNegocioException(
                    "El saldo no puede ser nulo ni negativo.");
        }
    }

    private void validarMoneda(String moneda) {
        if (moneda == null || !moneda.matches("[A-Z]{3}")) {
            throw new ReglaNegocioException(
                    "La moneda debe ser un código de tres letras mayúsculas, por ejemplo ARS.");
        }
    }
}
