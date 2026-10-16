package com.tpfintech.tpcuentas.service;

import com.tpfintech.tpcuentas.exception.ReglaNegocioException;
import com.tpfintech.tpcuentas.exception.RecursoNoEncontradoException;
import com.tpfintech.tpcuentas.model.Usuario;
import com.tpfintech.tpcuentas.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

        // Simulo el repositorio para probar el servicio sin depender de la base de
        // datos.
        @Mock
        private UsuarioRepository usuarioRepository;

        // Inyecto el repositorio simulado para aislar la lógica que quiero verificar.
        @InjectMocks
        private UsuarioService usuarioService;

        private Usuario usuario;

        @BeforeEach
        void setUp() {
                // Creo un usuario de referencia para reutilizarlo en las pruebas.
                usuario = new Usuario(
                                "Juan",
                                "Perez",
                                "juan@example.com",
                                "1123456789");
        }

        @Test
        void crearUsuarioConDatosValidos() {
                // Simulo que el email y el teléfono todavía no están registrados.
                when(usuarioRepository.existsByEmail(usuario.getEmail()))
                                .thenReturn(false);
                when(usuarioRepository.existsByTelefono(usuario.getTelefono()))
                                .thenReturn(false);
                when(usuarioRepository.save(usuario))
                                .thenReturn(usuario);

                Usuario resultado = usuarioService.crear(usuario);

                // Verifico los datos principales y el estado inicial del usuario.
                assertNotNull(resultado);
                assertEquals("Juan", resultado.getNombre());
                assertEquals("juan@example.com", resultado.getEmail());
                assertTrue(resultado.estaActivo());

                verify(usuarioRepository).save(usuario);
        }

        @Test
        void crearUsuarioConEmailDuplicadoLanzaExcepcion() {
                // Simulo un email ya registrado para comprobar la regla de unicidad.
                when(usuarioRepository.existsByEmail(usuario.getEmail()))
                                .thenReturn(true);

                assertThrows(
                                ReglaNegocioException.class,
                                () -> usuarioService.crear(usuario));

                // Me aseguro de que el usuario duplicado no llegue a guardarse.
                verify(usuarioRepository, never()).save(any(Usuario.class));
        }

        @Test
        void crearUsuarioConTelefonoDuplicadoLanzaExcepcion() {
                // Permito el email, pero simulo que el teléfono ya pertenece a otro usuario.
                when(usuarioRepository.existsByEmail(usuario.getEmail()))
                                .thenReturn(false);
                when(usuarioRepository.existsByTelefono(usuario.getTelefono()))
                                .thenReturn(true);

                assertThrows(
                                ReglaNegocioException.class,
                                () -> usuarioService.crear(usuario));

                // Compruebo que tampoco se persista el usuario si el teléfono está duplicado.
                verify(usuarioRepository, never()).save(any(Usuario.class));
        }

        @Test
        void buscarUsuarioInexistenteLanzaExcepcion() {
                Long idInexistente = 999L;

                when(usuarioRepository.findById(idInexistente))
                                .thenReturn(Optional.empty());

                // Verifico que la búsqueda falle con la excepción prevista para recursos
                // inexistentes.
                assertThrows(
                                RecursoNoEncontradoException.class,
                                () -> usuarioService.buscarPorId(idInexistente));
        }

        @Test
        void desactivarUsuarioCambiaSuEstado() {
                Long id = 1L;

                when(usuarioRepository.findById(id))
                                .thenReturn(Optional.of(usuario));
                when(usuarioRepository.save(usuario))
                                .thenReturn(usuario);

                usuarioService.desactivar(id);

                // Compruebo que el cambio de estado se refleje en el objeto y se guarde.
                assertFalse(usuario.estaActivo());
                verify(usuarioRepository).save(usuario);
        }

}
