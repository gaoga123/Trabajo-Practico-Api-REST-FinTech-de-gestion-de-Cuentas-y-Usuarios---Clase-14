package com.tpfintech.tpcuentas.controller;

import com.tpfintech.tpcuentas.dto.UsuarioRequest;
import com.tpfintech.tpcuentas.dto.UsuarioResponse;
import com.tpfintech.tpcuentas.model.Usuario;
import com.tpfintech.tpcuentas.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la gestión de usuarios.
 *
 * Expone operaciones para registrar, consultar, actualizar,
 * activar y desactivar usuarios.
 *
 * La lógica de negocio se delega en la capa de servicio,
 * mientras que las respuestas se presentan mediante DTO.
 */
@Tag(name = "Usuarios", description = "Operaciones para el registro, consulta, "
                + "actualización y administración del estado de los usuarios.")
@RestController
@RequestMapping(value = "/usuarios", produces = MediaType.APPLICATION_JSON_VALUE)
public class UsuarioController {

        private final UsuarioService usuarioService;

        public UsuarioController(UsuarioService usuarioService) {
                this.usuarioService = usuarioService;
        }

        /**
         * Registra un nuevo usuario.
         *
         * @param request datos personales y de contacto del usuario.
         * @return el usuario creado.
         */
        @Operation(summary = "Crear un usuario", description = """
                        Registra un nuevo usuario en el sistema.
                        Los datos recibidos se validan antes de ejecutar
                        la operación. Si el registro se completa correctamente,
                        devuelve el usuario creado con su identificador,
                        fecha de alta y estado actual.
                        """)
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Usuario creado correctamente.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UsuarioResponse.class), examples = @ExampleObject(name = "Usuario creado", value = """
                                        {
                                          "id": 1,
                                          "nombre": "Juan",
                                          "apellido": "Perez",
                                          "email": "juan.perez@example.com",
                                          "telefono": "1112345678",
                                          "fechaAlta": "2026-10-12T17:30:00",
                                          "activo": true
                                        }
                                        """))),
                        @ApiResponse(responseCode = "400", description = "Solicitud inválida. Uno o más campos "
                                        + "no cumplen las validaciones establecidas."),
                        @ApiResponse(responseCode = "409", description = "Conflicto con una regla de negocio, "
                                        + "por ejemplo, si el email ya está registrado.")
        })
        @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
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

        /**
         * Obtiene todos los usuarios registrados.
         *
         * @return listado de usuarios.
         */
        @Operation(summary = "Listar todos los usuarios", description = """
                        Recupera los usuarios registrados en el sistema.
                        Devuelve una colección de objetos de respuesta.
                        Si no existen usuarios registrados, devuelve una lista vacía.
                        """)
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = UsuarioResponse.class)), examples = @ExampleObject(name = "Listado de usuarios", value = """
                                        [
                                          {
                                            "id": 1,
                                            "nombre": "Juan",
                                            "apellido": "Perez",
                                            "email": "juan.perez@example.com",
                                            "telefono": "1112345678",
                                            "fechaAlta": "2026-10-12T17:30:00",
                                            "activo": true
                                          },
                                          {
                                            "id": 2,
                                            "nombre": "Maria",
                                            "apellido": "Gomez",
                                            "email": "maria.gomez@example.com",
                                            "telefono": "1198765432",
                                            "fechaAlta": "2026-10-12T18:00:00",
                                            "activo": false
                                          }
                                        ]
                                        """)))
        })
        @GetMapping
        public ResponseEntity<java.util.List<UsuarioResponse>> listarTodos() {

                java.util.List<UsuarioResponse> usuarios = usuarioService.listarTodos()
                                .stream()
                                .map(this::convertir)
                                .toList();

                return ResponseEntity.ok(usuarios);
        }

        /**
         * Busca un usuario por su identificador.
         *
         * @param id identificador único del usuario.
         * @return el usuario encontrado.
         */
        @Operation(summary = "Consultar un usuario por ID", description = """
                        Recupera la información de un usuario a partir
                        de su identificador único.
                        """)
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Usuario encontrado correctamente.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UsuarioResponse.class), examples = @ExampleObject(name = "Usuario encontrado", value = """
                                        {
                                          "id": 1,
                                          "nombre": "Juan",
                                          "apellido": "Perez",
                                          "email": "juan.perez@example.com",
                                          "telefono": "1112345678",
                                          "fechaAlta": "2026-10-12T17:30:00",
                                          "activo": true
                                        }
                                        """))),
                        @ApiResponse(responseCode = "400", description = "El identificador proporcionado no es válido."),
                        @ApiResponse(responseCode = "404", description = "No existe un usuario con el identificador indicado.")
        })
        @GetMapping("/{id}")
        public ResponseEntity<UsuarioResponse> buscarPorId(
                        @Parameter(name = "id", description = "Identificador único del usuario.", required = true, example = "1", schema = @Schema(type = "integer", format = "int64", minimum = "1")) @PathVariable Long id) {

                return ResponseEntity.ok(
                                convertir(usuarioService.buscarPorId(id)));
        }

        /**
         * Actualiza los datos de un usuario existente.
         *
         * @param id      identificador único del usuario.
         * @param request nuevos datos personales y de contacto.
         * @return el usuario actualizado.
         */
        @Operation(summary = "Actualizar un usuario", description = """
                        Actualiza los datos del usuario identificado.
                        La solicitud debe incluir los campos requeridos
                        por UsuarioRequest. La operación delega en el servicio
                        la aplicación de las reglas de negocio.
                        """)
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UsuarioResponse.class), examples = @ExampleObject(name = "Usuario actualizado", value = """
                                        {
                                          "id": 1,
                                          "nombre": "Juan Carlos",
                                          "apellido": "Perez",
                                          "email": "juan.perez@example.com",
                                          "telefono": "1112345678",
                                          "fechaAlta": "2026-10-12T17:30:00",
                                          "activo": true
                                        }
                                        """))),
                        @ApiResponse(responseCode = "400", description = "Solicitud inválida o campos que no "
                                        + "cumplen las validaciones establecidas."),
                        @ApiResponse(responseCode = "404", description = "No se encontró el usuario indicado."),
                        @ApiResponse(responseCode = "409", description = "Conflicto con una regla de negocio, "
                                        + "por ejemplo, si el email ya pertenece a otro usuario.")
        })
        @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<UsuarioResponse> actualizar(
                        @Parameter(name = "id", description = "Identificador único del usuario que se actualizará.", required = true, example = "1", schema = @Schema(type = "integer", format = "int64", minimum = "1")) @PathVariable Long id,

                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del usuario que se utilizarán para "
                                        + "actualizar su información.", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UsuarioRequest.class), examples = @ExampleObject(name = "Datos para actualizar", value = """
                                                        {
                                                          "nombre": "Juan Carlos",
                                                          "apellido": "Perez",
                                                          "email": "juan.perez@example.com",
                                                          "telefono": "1112345678"
                                                        }
                                                        """))) @Valid @RequestBody UsuarioRequest request) {

                Usuario datos = new Usuario(
                                request.nombre(),
                                request.apellido(),
                                request.email(),
                                request.telefono());

                Usuario actualizado = usuarioService.actualizar(id, datos);

                return ResponseEntity.ok(convertir(actualizado));
        }

        /**
         * Activa un usuario existente.
         *
         * @param id identificador único del usuario.
         * @return el usuario con su estado actualizado.
         */
        @Operation(summary = "Activar un usuario", description = """
                        Cambia el estado del usuario indicado a activo.
                        Si la operación se completa correctamente,
                        devuelve la representación actualizada del usuario.
                        La operación está sujeta a las reglas de negocio
                        implementadas en la capa de servicio.
                        """)
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Usuario activado correctamente.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UsuarioResponse.class), examples = @ExampleObject(name = "Usuario activo", value = """
                                        {
                                          "id": 1,
                                          "nombre": "Juan",
                                          "apellido": "Perez",
                                          "email": "juan.perez@example.com",
                                          "telefono": "1112345678",
                                          "fechaAlta": "2026-10-12T17:30:00",
                                          "activo": true
                                        }
                                        """))),
                        @ApiResponse(responseCode = "400", description = "El identificador proporcionado no es válido."),
                        @ApiResponse(responseCode = "404", description = "No se encontró el usuario indicado."),
                        @ApiResponse(responseCode = "409", description = "No se puede activar el usuario debido "
                                        + "a una regla de negocio.")
        })
        @PatchMapping("/{id}/activar")
        public ResponseEntity<UsuarioResponse> activar(
                        @Parameter(name = "id", description = "Identificador único del usuario que se activará.", required = true, example = "1", schema = @Schema(type = "integer", format = "int64", minimum = "1")) @PathVariable Long id) {

                usuarioService.activar(id);

                return ResponseEntity.ok(
                                convertir(usuarioService.buscarPorId(id)));
        }

        /**
         * Desactiva un usuario existente.
         *
         * @param id identificador único del usuario.
         * @return el usuario con su estado actualizado.
         */
        @Operation(summary = "Desactivar un usuario", description = """
                        Cambia el estado del usuario indicado a inactivo.
                        Si la operación se completa correctamente,
                        devuelve la representación actualizada del usuario.
                        Esta operación no implica necesariamente la eliminación
                        de sus datos ni de las cuentas bancarias asociadas.
                        """)
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Usuario desactivado correctamente.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UsuarioResponse.class), examples = @ExampleObject(name = "Usuario inactivo", value = """
                                        {
                                          "id": 1,
                                          "nombre": "Juan",
                                          "apellido": "Perez",
                                          "email": "juan.perez@example.com",
                                          "telefono": "1112345678",
                                          "fechaAlta": "2026-10-12T17:30:00",
                                          "activo": false
                                        }
                                        """))),
                        @ApiResponse(responseCode = "400", description = "El identificador proporcionado no es válido."),
                        @ApiResponse(responseCode = "404", description = "No se encontró el usuario indicado."),
                        @ApiResponse(responseCode = "409", description = "No se puede desactivar el usuario debido "
                                        + "a una regla de negocio.")
        })
        @PatchMapping("/{id}/desactivar")
        public ResponseEntity<UsuarioResponse> desactivar(
                        @Parameter(name = "id", description = "Identificador único del usuario que se desactivará.", required = true, example = "1", schema = @Schema(type = "integer", format = "int64", minimum = "1")) @PathVariable Long id) {

                usuarioService.desactivar(id);

                return ResponseEntity.ok(
                                convertir(usuarioService.buscarPorId(id)));
        }

        /**
         * Convierte la entidad de dominio en el DTO de respuesta.
         *
         * @param usuario entidad que se expondrá mediante la API.
         * @return representación del usuario para el consumidor.
         */
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