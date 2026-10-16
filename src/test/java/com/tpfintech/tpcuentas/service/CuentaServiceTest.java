package com.tpfintech.tpcuentas.service;

import com.tpfintech.tpcuentas.exception.ReglaNegocioException;
import com.tpfintech.tpcuentas.exception.RecursoNoEncontradoException;
import com.tpfintech.tpcuentas.model.Cuenta;
import com.tpfintech.tpcuentas.model.EstadoCuenta;
import com.tpfintech.tpcuentas.model.TipoCuenta;
import com.tpfintech.tpcuentas.model.Usuario;
import com.tpfintech.tpcuentas.repository.CuentaRepository;
import com.tpfintech.tpcuentas.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CuentaServiceTest {

        // Simulo los repositorios para probar la lógica del servicio sin acceder a
        // MySQL.
        @Mock
        private CuentaRepository cuentaRepository;

        @Mock
        private UsuarioRepository usuarioRepository;

        // Dejo que Mockito inyecte los repositorios simulados en el servicio que estoy
        // probando.
        @InjectMocks
        private CuentaService cuentaService;

        private Usuario usuario;
        private Cuenta cuenta;

        @BeforeEach
        void setUp() {
                // Reutilizo los mismos datos iniciales para mantener las pruebas independientes
                // y consistentes.
                usuario = new Usuario(
                                "Juan",
                                "Perez",
                                "juan@example.com",
                                "1123456789");

                cuenta = new Cuenta(
                                "CTA-000001",
                                TipoCuenta.CAJA_AHORRO,
                                new BigDecimal("15000.00"),
                                "ARS",
                                null);
        }

        @Test
        void crearCuentaConDatosValidos() {
                // Simulo las consultas necesarias para crear una cuenta válida.
                when(cuentaRepository.existsByNumeroCuenta("CTA-000001"))
                                .thenReturn(false);
                when(usuarioRepository.findById(1L))
                                .thenReturn(Optional.of(usuario));
                when(cuentaRepository.save(any(Cuenta.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                Cuenta resultado = cuentaService.crear(cuenta, 1L);

                // Compruebo tanto los datos de la cuenta como su relación con el usuario.
                assertNotNull(resultado);
                assertEquals("CTA-000001", resultado.getNumeroCuenta());
                assertEquals(new BigDecimal("15000.00"), resultado.getSaldo());
                assertEquals(EstadoCuenta.ACTIVA, resultado.getEstado());
                assertSame(usuario, resultado.getUsuario());

                // Verifico que el servicio haya solicitado guardar la cuenta creada.
                verify(cuentaRepository).save(cuenta);
        }

        @Test
        void crearCuentaConSaldoNegativoLanzaExcepcion() {
                cuenta.setSaldo(new BigDecimal("-100.00"));

                when(cuentaRepository.existsByNumeroCuenta("CTA-000001"))
                                .thenReturn(false);

                // Compruebo que la regla de negocio rechace un saldo negativo.
                assertThrows(
                                ReglaNegocioException.class,
                                () -> cuentaService.crear(cuenta, 1L));

                // Me aseguro de que no se guarde la cuenta ni se consulte al usuario.
                verify(cuentaRepository, never()).save(any(Cuenta.class));
                verifyNoInteractions(usuarioRepository);
        }

        @Test
        void crearCuentaConUsuarioInexistenteLanzaExcepcion() {
                when(cuentaRepository.existsByNumeroCuenta("CTA-000001"))
                                .thenReturn(false);
                when(usuarioRepository.findById(999L))
                                .thenReturn(Optional.empty());

                // Compruebo que no pueda asociar una cuenta a un usuario inexistente.
                assertThrows(
                                RecursoNoEncontradoException.class,
                                () -> cuentaService.crear(cuenta, 999L));

                // Evito que se persista una cuenta si falla la búsqueda del usuario.
                verify(cuentaRepository, never()).save(any(Cuenta.class));
        }

        @Test
        void crearCuentaParaUsuarioInactivoLanzaExcepcion() {
                usuario.desactivar();

                when(cuentaRepository.existsByNumeroCuenta("CTA-000001"))
                                .thenReturn(false);
                when(usuarioRepository.findById(1L))
                                .thenReturn(Optional.of(usuario));

                // Compruebo que un usuario inactivo no pueda abrir cuentas nuevas.
                assertThrows(
                                ReglaNegocioException.class,
                                () -> cuentaService.crear(cuenta, 1L));

                verify(cuentaRepository, never()).save(any(Cuenta.class));
        }

        @Test
        void buscarCuentaInexistenteLanzaExcepcion() {
                when(cuentaRepository.findById(999L))
                                .thenReturn(Optional.empty());

                // Verifico que la búsqueda comunique el error cuando la cuenta no existe.
                assertThrows(
                                RecursoNoEncontradoException.class,
                                () -> cuentaService.buscarPorId(999L));
        }
}