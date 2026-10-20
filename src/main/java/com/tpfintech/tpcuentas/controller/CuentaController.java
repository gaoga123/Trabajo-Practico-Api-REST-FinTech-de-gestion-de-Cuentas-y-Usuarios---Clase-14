package com.tpfintech.tpcuentas.controller;

import com.tpfintech.tpcuentas.dto.CuentaRequest;
import com.tpfintech.tpcuentas.dto.CuentaResponse;
import com.tpfintech.tpcuentas.model.Cuenta;
import com.tpfintech.tpcuentas.service.CuentaService;

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

import java.util.List;

/**
 * Controlador REST para la gestión de cuentas bancarias.
 *
 * Expone operaciones para crear, consultar, actualizar y eliminar
 * cuentas, así como para consultar las cuentas asociadas a un usuario.
 *
 * La lógica de negocio y las reglas de validación se delegan
 * en la capa de servicio.
 */
@Tag(name = "Cuentas", description = "Operaciones para la administración y consulta "
                + "de cuentas bancarias.")
@RestController
@RequestMapping(value = "/cuentas", produces = MediaType.APPLICATION_JSON_VALUE)
public class CuentaController {

        private final CuentaService cuentaService;

        // Inyecto el servicio por constructor para mantener explícita la dependencia
        // del controlador.
        public CuentaController(CuentaService cuentaService) {
                this.cuentaService = cuentaService;
        }

        /**
         * Registra una nueva cuenta bancaria.
         *
         * @param request datos de la cuenta y del usuario asociado.
         * @return la cuenta creada.
         */
        @Operation(summary = "Crear una cuenta bancaria", description = """
                        Registra una nueva cuenta bancaria asociada a un usuario existente.
                        Los datos recibidos se validan antes de ejecutar la operación.
                        Si la creación se completa correctamente, devuelve la cuenta
                        creada junto con su identificador y los datos generados
                        por el sistema.
                        """)
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Cuenta creada correctamente.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CuentaResponse.class), examples = @ExampleObject(value = """
                                        {
                                          "id": 1,
                                          "numeroCuenta": "000123456789",
                                          "tipo": "CAJA_AHORRO",
                                          "saldo": 15000.00,
                                          "moneda": "ARS",
                                          "estado": "ACTIVA",
                                          "fechaCreacion": "2026-10-12T17:30:00",
                                          "usuarioId": 1
                                        }
                                        """))),
                        @ApiResponse(responseCode = "400", description = "Solicitud inválida. Los datos enviados "
                                        + "no cumplen las validaciones establecidas."),
                        @ApiResponse(responseCode = "404", description = "No se encontró el usuario al que "
                                        + "se desea asociar la cuenta."),
                        @ApiResponse(responseCode = "409", description = "Conflicto con el estado actual del sistema, "
                                        + "por ejemplo, si el número de cuenta ya existe.")
        })
        @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<CuentaResponse> crear(
                        @Valid @RequestBody CuentaRequest request) {

                // Transformo el DTO de entrada en una entidad y delego las reglas al servicio.
                Cuenta cuenta = new Cuenta(
                                request.numeroCuenta(),
                                request.tipo(),
                                request.saldo(),
                                request.moneda(),
                                null);

                Cuenta creada = cuentaService.crear(cuenta, request.usuarioId());

                // Devuelvo 201 Created porque la solicitud generó un recurso nuevo.
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(convertir(creada));
        }

        /**
         * Obtiene todas las cuentas bancarias registradas.
         *
         * @return listado de cuentas.
         */
        @Operation(summary = "Listar todas las cuentas", description = """
                        Recupera las cuentas bancarias registradas en el sistema.
                        Devuelve una colección de objetos de respuesta.
                        Si no existen cuentas registradas, devuelve una lista vacía.
                        """)
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = CuentaResponse.class)), examples = @ExampleObject(value = """
                                        [
                                          {
                                            "id": 1,
                                            "numeroCuenta": "000123456789",
                                            "tipo": "CAJA_AHORRO",
                                            "saldo": 15000.00,
                                            "moneda": "ARS",
                                            "estado": "ACTIVA",
                                            "fechaCreacion": "2026-10-12T17:30:00",
                                            "usuarioId": 1
                                          }
                                        ]
                                        """)))
        })
        @GetMapping
        public ResponseEntity<List<CuentaResponse>> listarTodas() {

                // Convierto cada entidad a DTO para no exponer directamente los modelos de
                // persistencia.
                List<CuentaResponse> cuentas = cuentaService.listarTodas()
                                .stream()
                                .map(this::convertir)
                                .toList();

                return ResponseEntity.ok(cuentas);
        }

        /**
         * Busca una cuenta por su identificador.
         *
         * @param id identificador de la cuenta.
         * @return la cuenta encontrada.
         */
        @Operation(summary = "Consultar una cuenta por ID", description = """
                        Recupera los datos de una cuenta bancaria a partir
                        de su identificador único.
                        """)
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Cuenta encontrada correctamente.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CuentaResponse.class), examples = @ExampleObject(value = """
                                        {
                                          "id": 1,
                                          "numeroCuenta": "000123456789",
                                          "tipo": "CAJA_AHORRO",
                                          "saldo": 15000.00,
                                          "moneda": "ARS",
                                          "estado": "ACTIVA",
                                          "fechaCreacion": "2026-10-12T17:30:00",
                                          "usuarioId": 1
                                        }
                                        """))),
                        @ApiResponse(responseCode = "400", description = "El identificador proporcionado no es válido."),
                        @ApiResponse(responseCode = "404", description = "No existe una cuenta con el identificador indicado.")
        })
        @GetMapping("/{id}")
        public ResponseEntity<CuentaResponse> buscarPorId(
                        @Parameter(name = "id", description = "Identificador único de la cuenta bancaria.", required = true, example = "1", schema = @Schema(type = "integer", format = "int64", minimum = "1")) @PathVariable Long id) {

                return ResponseEntity.ok(
                                convertir(cuentaService.buscarPorId(id)));
        }

        /**
         * Obtiene las cuentas asociadas a un usuario.
         *
         * @param usuarioId identificador del usuario.
         * @return cuentas asociadas al usuario.
         */
        @Operation(summary = "Listar cuentas por usuario", description = """
                        Recupera todas las cuentas bancarias asociadas
                        al identificador de un usuario.
                        Si el usuario existe, pero no posee cuentas,
                        devuelve una lista vacía.
                        """)
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Consulta completada correctamente.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = CuentaResponse.class)))),
                        @ApiResponse(responseCode = "400", description = "El identificador del usuario no es válido."),
                        @ApiResponse(responseCode = "404", description = "No se encontró el usuario indicado, "
                                        + "si el servicio valida su existencia.")
        })
        @GetMapping("/usuario/{usuarioId}")
        public ResponseEntity<List<CuentaResponse>> listarPorUsuario(
                        @Parameter(name = "usuarioId", description = "Identificador único del usuario propietario.", required = true, example = "1", schema = @Schema(type = "integer", format = "int64", minimum = "1")) @PathVariable Long usuarioId) {

                // Mantengo la búsqueda en el servicio y adapto el resultado al formato público
                // de la API.
                List<CuentaResponse> cuentas = cuentaService
                                .listarPorUsuario(usuarioId)
                                .stream()
                                .map(this::convertir)
                                .toList();

                return ResponseEntity.ok(cuentas);
        }

        /**
         * Actualiza los datos de una cuenta existente.
         *
         * @param id      identificador de la cuenta que se actualizará.
         * @param request nuevos datos de la cuenta.
         * @return la cuenta actualizada.
         */
        @Operation(summary = "Actualizar una cuenta bancaria", description = """
                        Actualiza los datos de una cuenta existente utilizando
                        su identificador y los datos enviados en el cuerpo
                        de la solicitud.
                        El servicio aplica las reglas de negocio correspondientes.
                        """)
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Cuenta actualizada correctamente.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CuentaResponse.class), examples = @ExampleObject(value = """
                                        {
                                          "id": 1,
                                          "numeroCuenta": "000123456789",
                                          "tipo": "CAJA_AHORRO",
                                          "saldo": 20000.00,
                                          "moneda": "ARS",
                                          "estado": "ACTIVA",
                                          "fechaCreacion": "2026-10-12T17:30:00",
                                          "usuarioId": 1
                                        }
                                        """))),
                        @ApiResponse(responseCode = "400", description = "Solicitud inválida o datos que no cumplen "
                                        + "las reglas de validación."),
                        @ApiResponse(responseCode = "404", description = "No se encontró la cuenta o el usuario asociado."),
                        @ApiResponse(responseCode = "409", description = "Conflicto con una regla de negocio "
                                        + "o con la unicidad del número de cuenta.")
        })
        @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<CuentaResponse> actualizar(
                        @Parameter(name = "id", description = "Identificador único de la cuenta que se actualizará.", required = true, example = "1", schema = @Schema(type = "integer", format = "int64", minimum = "1")) @PathVariable Long id,

                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos que se utilizarán para actualizar la cuenta.", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CuentaRequest.class), examples = @ExampleObject(value = """
                                        {
                                          "numeroCuenta": "000123456789",
                                          "tipo": "CAJA_AHORRO",
                                          "saldo": 20000.00,
                                          "moneda": "ARS",
                                          "usuarioId": 1
                                        }
                                        """))) @Valid @RequestBody CuentaRequest request) {

                // Construyo los datos recibidos sin resolver acá las reglas de actualización.
                Cuenta datos = new Cuenta(
                                request.numeroCuenta(),
                                request.tipo(),
                                request.saldo(),
                                request.moneda(),
                                null);

                Cuenta actualizada = cuentaService.actualizar(
                                id,
                                datos,
                                request.usuarioId());

                return ResponseEntity.ok(convertir(actualizada));
        }

        /**
         * Elimina una cuenta bancaria.
         *
         * @param id identificador de la cuenta que se eliminará.
         */
        @Operation(summary = "Eliminar una cuenta bancaria", description = """
                        Solicita la eliminación de la cuenta identificada.
                        Si la operación se completa correctamente, devuelve
                        una respuesta sin contenido.
                        La posibilidad de eliminar una cuenta depende
                        de las reglas de negocio implementadas en el servicio.
                        """)
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Cuenta eliminada correctamente. "
                                        + "La respuesta no contiene un cuerpo."),
                        @ApiResponse(responseCode = "400", description = "El identificador proporcionado no es válido."),
                        @ApiResponse(responseCode = "404", description = "No se encontró la cuenta indicada."),
                        @ApiResponse(responseCode = "409", description = "La cuenta no puede eliminarse debido "
                                        + "a una restricción o regla de negocio.")
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> eliminar(
                        @Parameter(name = "id", description = "Identificador único de la cuenta que se eliminará.", required = true, example = "1", schema = @Schema(type = "integer", format = "int64", minimum = "1")) @PathVariable Long id) {

                cuentaService.eliminar(id);

                // Uso 204 No Content para indicar que la operación terminó sin devolver un
                // cuerpo.
                return ResponseEntity.noContent().build();
        }

        /**
         * Convierte la entidad de dominio en el DTO de respuesta.
         *
         * @param cuenta entidad que se expondrá mediante la API.
         * @return representación de la cuenta para el consumidor.
         */
        private CuentaResponse convertir(Cuenta cuenta) {
                // Centralizo la conversión para mantener uniforme la respuesta de todos los
                // endpoints.
                return new CuentaResponse(
                                cuenta.getId(),
                                cuenta.getNumeroCuenta(),
                                cuenta.getTipo(),
                                cuenta.getSaldo(),
                                cuenta.getMoneda(),
                                cuenta.getEstado(),
                                cuenta.getFechaCreacion(),
                                cuenta.getUsuario().getId());
        }
}
