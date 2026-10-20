
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

        // Dejo que Mockito inyecte las dependencias simuladas en el servicio.
        @InjectMocks
        private CuentaService cuentaService;

        private Usuario usuario;
        private Cuenta cuenta;

        @BeforeEach
        void setUp() {
                // Reutilizo datos iniciales para mantener las pruebas independientes y
                // consistentes.
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
                when(cuentaRepository.saveAndFlush(any(Cuenta.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                Cuenta resultado = cuentaService.crear(cuenta, 1L);

                // Compruebo los datos de la cuenta y su relación con el usuario.
                assertNotNull(resultado);
                assertEquals("CTA-000001", resultado.getNumeroCuenta());
                assertEquals(new BigDecimal("15000.00"), resultado.getSaldo());
                assertEquals(EstadoCuenta.ACTIVA, resultado.getEstado());
                assertSame(usuario, resultado.getUsuario());

                // Verifico que se invoque el método de persistencia utilizado por el servicio.
                verify(cuentaRepository).saveAndFlush(cuenta);
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

                // Verifico que no se intente persistir una cuenta inválida.
                verify(cuentaRepository, never()).saveAndFlush(any(Cuenta.class));
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
                verify(cuentaRepository, never()).saveAndFlush(any(Cuenta.class));
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

                // Verifico que la cuenta no llegue a persistirse.
                verify(cuentaRepository, never()).saveAndFlush(any(Cuenta.class));
        }

        @Test
        void buscarCuentaInexistenteLanzaExcepcion() {
                when(cuentaRepository.findById(999L))
                                .thenReturn(Optional.empty());

                // Verifico que la búsqueda informe cuando la cuenta no existe.
                assertThrows(
                                RecursoNoEncontradoException.class,
                                () -> cuentaService.buscarPorId(999L));
        }
}
