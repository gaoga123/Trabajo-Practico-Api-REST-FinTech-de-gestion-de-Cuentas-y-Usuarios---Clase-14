# TP FINTECH: API REST de Usuarios y Cuentas Fintech - Clase 14

- Fecha de Entrega: 21-10-2026

## Descripción

API REST desarrollada con Java y Spring Boot para administrar usuarios y sus cuentas bancarias. El proyecto forma parte de un trabajo práctico de la Diplomatura en Desarrollo de Software FinTech: IA y Microservicios, y aplica conceptos de programación orientada a objetos, persistencia relacional, validación de datos, manejo de excepciones, documentación OpenAPI y pruebas automatizadas.

## Características

- Registro, consulta, listado y actualización de usuarios.
- Activación y desactivación de usuarios.
- Creación, consulta, listado, actualización y eliminación de cuentas bancarias.
- Consulta de las cuentas asociadas a un usuario.
- Persistencia relacional mediante MySQL y Spring Data JPA.
- Validación de solicitudes mediante Jakarta Validation.
- Manejo de excepciones personalizadas para representar errores de negocio y recursos inexistentes.
- Uso de bloques `try/catch` en operaciones de persistencia para gestionar violaciones de integridad de datos.
- Manejo centralizado de excepciones HTTP mediante `@RestControllerAdvice`.
- Respuestas de error estructuradas en formato JSON, con código HTTP, descripción y mensaje.
- Validación de solicitudes mediante Jakarta Validation y manejo de datos inválidos.
- Respuestas HTTP y DTO específicos para las operaciones de la API.
- Documentación interactiva mediante Swagger UI y OpenAPI.
- Colección de Postman con solicitudes y pruebas HTTP.
- Pruebas unitarias de servicios mediante JUnit y Mockito.

## Tecnologías

| Tecnología | Uso |
|---|---|
| Java 21 | Lenguaje de programación |
| Spring Boot 4.1.1 | Configuración y ejecución de la aplicación |
| Spring Web MVC | Implementación de los controladores REST |
| Spring Data JPA / Hibernate | Persistencia y mapeo objeto-relacional |
| MySQL 8.4 | Base de datos relacional |
| Jakarta Validation | Validación de datos de entrada |
| Springdoc OpenAPI | Documentación de la API |
| JUnit 5 | Pruebas automatizadas |
| Mockito | Simulación de dependencias durante las pruebas unitarias |
| Maven Wrapper | Compilación, ejecución y gestión de dependencias |

## Requisitos previos

Antes de ejecutar el proyecto, se deben disponer de los siguientes componentes:

- JDK 21.
- MySQL 8.4.
- Git, si se desea clonar el repositorio.
- Una terminal compatible con los comandos de Maven Wrapper.

Para comprobar la instalación de Java, ejecute en PowerShell:

```powershell
java -version
javac -version
```

El proyecto incluye Maven Wrapper, por lo que no es necesario instalar Maven por separado.

## Configuración de la base de datos

La aplicación utiliza una base de datos MySQL denominada `tp_fintech`.

La configuración de conexión se encuentra en:

```text
src/main/resources/application.yml
```

Antes de iniciar la aplicación, se debe verificar que:

1. El servidor MySQL esté iniciado.
2. El usuario configurado disponga de los permisos necesarios.
3. Las credenciales coincidan con las definidas en la configuración.
4. La URL JDBC apunte al servidor y a la base de datos correctos.

La URL de conexión utiliza `createDatabaseIfNotExist=true`. Este parámetro permite que el controlador JDBC intente crear la base de datos cuando no existe, siempre que el usuario de MySQL disponga de los permisos correspondientes.

> **Seguridad:** Contraseña establecida sólo para fines educativos y de evaluación. No se deben publicar contraseñas reales ni otras credenciales en el repositorio. Las credenciales tampoco deben incluirse en capturas de pantalla, documentación pública ni mensajes de commit.

### Estrategia de persistencia

La configuración JPA utiliza `ddl-auto: update` para actualizar el esquema de la base de datos a partir de las entidades.

Esta configuración resulta práctica durante el desarrollo académico, pero no reemplaza una estrategia de migraciones versionadas. Para un entorno productivo, se recomienda gestionar los cambios del esquema mediante migraciones controladas y revisadas.

## Instalación y ejecución

### 1. Obtener el proyecto

Ya que el proyecto se encuentra en un repositorio Git, ejecute:

```powershell
git clone https://github.com/gaoga123/TPApiFinTechClase14.git
cd tp-api-cuentas
```

Si el código fuente ya se encuentra disponible localmente, abra una terminal en la carpeta raíz del proyecto.

### 2. Ejecutar las pruebas y compilar

Desde la raíz del proyecto, ejecute:

```powershell
.\mvnw.cmd clean test
```

Este comando limpia los archivos generados anteriormente, compila el proyecto y ejecuta las pruebas automatizadas.

El resultado esperado si la compilación y todas las pruebas finalizan correctamente es:

```text
BUILD SUCCESS
```

### 3. Iniciar la aplicación

Ejecute:

```powershell
.\mvnw.cmd spring-boot:run
```

Por defecto, la API queda disponible en:

```text
http://localhost:8080
```

Para detener la aplicación, presione `Ctrl + C` en la terminal donde se está ejecutando.

Si la aplicación no inicia, verifique las credenciales de MySQL, la disponibilidad del puerto 8080 y los mensajes de error de la consola.

## Documentación interactiva

Con la aplicación en ejecución, se puede acceder a los siguientes recursos:

| Recurso | URL |
|---|---|
| Swagger UI | [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) |
| Especificación OpenAPI en JSON | [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs) |

Swagger UI permite consultar las operaciones disponibles, revisar sus parámetros, examinar los ejemplos de solicitudes y respuestas y ejecutar peticiones HTTP directamente desde el navegador.

Para probar un endpoint desde Swagger UI:

1. Abra la documentación interactiva.
2. Seleccione el grupo correspondiente: `Usuarios` o `Cuentas`.
3. Despliegue la operación que desea ejecutar.
4. Seleccione **Try it out**.
5. Complete los parámetros y el cuerpo JSON cuando corresponda.
6. Seleccione **Execute**.
7. Revise el código HTTP, el cuerpo de la respuesta y los detalles de la solicitud generada.

Las operaciones que modifican datos deben ejecutarse con valores de prueba y sobre una base de datos de desarrollo.

## Endpoints

Todos los endpoints devuelven JSON cuando la respuesta contiene un cuerpo. Las rutas siguientes corresponden a los controladores implementados en el proyecto.

Los códigos HTTP indicados representan las respuestas esperadas según el resultado de cada operación y las reglas de negocio documentadas. Los errores efectivos dependen también de la validación, el manejo global de excepciones y las restricciones de persistencia configuradas.

### Usuarios

#### Resumen de operaciones

| Método | Endpoint | Descripción | Respuesta exitosa |
|---|---|---|---|
| `POST` | `/usuarios` | Registrar un usuario | `201 Created` |
| `GET` | `/usuarios` | Listar usuarios | `200 OK` |
| `GET` | `/usuarios/{id}` | Consultar un usuario por ID | `200 OK` |
| `PUT` | `/usuarios/{id}` | Actualizar un usuario | `200 OK` |
| `PATCH` | `/usuarios/{id}/activar` | Activar un usuario | `200 OK` |
| `PATCH` | `/usuarios/{id}/desactivar` | Desactivar un usuario | `200 OK` |

#### `POST /usuarios`

Registra un nuevo usuario.

**Cuerpo de la solicitud:**

```json
{
  "nombre": "Juan",
  "apellido": "Perez",
  "email": "juan.perez@example.com",
  "telefono": "1123456789"
}
```

Los campos corresponden a los datos personales y de contacto requeridos por `UsuarioRequest`.

**Respuesta exitosa — `201 Created`:**

```json
{
  "id": 1,
  "nombre": "Juan",
  "apellido": "Perez",
  "email": "juan.perez@example.com",
  "telefono": "1123456789",
  "fechaAlta": "2026-10-12T17:30:00",
  "activo": true
}
```

El identificador y la fecha de alta son generados por el sistema. El usuario se registra inicialmente como activo.

**Respuestas de error previstas:**

- `400 Bad Request`: los datos enviados no cumplen las validaciones.
- `409 Conflict`: el email o el teléfono ya se encuentra registrado, según las reglas de negocio implementadas.

#### `GET /usuarios`

Recupera todos los usuarios registrados.

No requiere parámetros ni cuerpo de solicitud.

**Respuesta exitosa — `200 OK`:**

```json
[
  {
    "id": 1,
    "nombre": "Juan",
    "apellido": "Perez",
    "email": "juan.perez@example.com",
    "telefono": "1123456789",
    "fechaAlta": "2026-10-12T17:30:00",
    "activo": true
  }
]
```

Si no existen usuarios registrados, la operación devuelve una lista vacía.

#### `GET /usuarios/{id}`

Recupera un usuario a partir de su identificador.

**Parámetro de ruta:**

| Parámetro | Tipo | Descripción |
|---|---|---|
| `id` | `Long` | Identificador único del usuario |

Ejemplo:

```http
GET /usuarios/1
```

**Respuestas esperadas:**

- `200 OK`: usuario encontrado.
- `400 Bad Request`: identificador inválido.
- `404 Not Found`: no existe un usuario con el identificador indicado.

#### `PUT /usuarios/{id}`

Actualiza los datos personales y de contacto de un usuario existente.

**Parámetro de ruta:** `id`, identificador del usuario que se desea actualizar.

**Cuerpo de la solicitud:**

```json
{
  "nombre": "Juan Carlos",
  "apellido": "Perez",
  "email": "juan.perez@example.com",
  "telefono": "1123456789"
}
```

La solicitud debe contener los campos requeridos por `UsuarioRequest`. La operación delega en el servicio la comprobación de las reglas de negocio.

**Respuestas esperadas:**

- `200 OK`: usuario actualizado.
- `400 Bad Request`: solicitud inválida o datos que no superan las validaciones.
- `404 Not Found`: usuario inexistente.
- `409 Conflict`: incumplimiento de una regla de negocio, por ejemplo, un email que pertenece a otro usuario.

#### `PATCH /usuarios/{id}/activar`

Activa un usuario existente.

**Parámetro de ruta:** `id`, identificador del usuario que se desea activar.

Ejemplo:

```http
PATCH /usuarios/1/activar
```

No requiere cuerpo de solicitud.

**Respuesta exitosa — `200 OK`:** devuelve el usuario con `activo: true`.

#### `PATCH /usuarios/{id}/desactivar`

Desactiva un usuario existente sin eliminar su registro.

**Parámetro de ruta:** `id`, identificador del usuario que se desea desactivar.

Ejemplo:

```http
PATCH /usuarios/1/desactivar
```

No requiere cuerpo de solicitud.

**Respuesta exitosa — `200 OK`:** devuelve el usuario con `activo: false`.

La desactivación no implica necesariamente la eliminación de sus datos ni de las cuentas bancarias asociadas.

Para ambas operaciones de cambio de estado se contemplan los siguientes errores:

- `400 Bad Request`: identificador inválido.
- `404 Not Found`: usuario inexistente.
- `409 Conflict`: regla de negocio que impide completar la operación, cuando corresponda.

### Cuentas

#### Resumen de operaciones

| Método | Endpoint | Descripción | Respuesta exitosa |
|---|---|---|---|
| `POST` | `/cuentas` | Crear una cuenta | `201 Created` |
| `GET` | `/cuentas` | Listar cuentas | `200 OK` |
| `GET` | `/cuentas/{id}` | Consultar una cuenta por ID | `200 OK` |
| `GET` | `/cuentas/usuario/{usuarioId}` | Listar cuentas de un usuario | `200 OK` |
| `PUT` | `/cuentas/{id}` | Actualizar una cuenta | `200 OK` |
| `DELETE` | `/cuentas/{id}` | Eliminar una cuenta | `204 No Content` |

#### `POST /cuentas`

Registra una nueva cuenta bancaria asociada a un usuario existente y activo.

**Cuerpo de la solicitud:**

```json
{
  "numeroCuenta": "CTA-000001",
  "tipo": "CAJA_AHORRO",
  "saldo": 15000.00,
  "moneda": "ARS",
  "usuarioId": 1
}
```

**Descripción de los campos:**

| Campo | Tipo | Descripción |
|---|---|---|
| `numeroCuenta` | `String` | Número identificador de la cuenta |
| `tipo` | `String` | Tipo de cuenta admitido por el dominio |
| `saldo` | `BigDecimal` | Saldo inicial de la cuenta |
| `moneda` | `String` | Código de moneda de tres letras mayúsculas |
| `usuarioId` | `Long` | Identificador del usuario asociado |

Los tipos admitidos son:

- `CAJA_AHORRO`
- `CUENTA_CORRIENTE`

La moneda debe respetar el formato de tres letras mayúsculas, por ejemplo, `ARS`. El saldo no puede ser negativo.

El usuario indicado en `usuarioId` debe existir y estar activo. El número de cuenta debe ser único.

**Respuesta exitosa — `201 Created`:**

```json
{
  "id": 1,
  "numeroCuenta": "CTA-000001",
  "tipo": "CAJA_AHORRO",
  "saldo": 15000.00,
  "moneda": "ARS",
  "estado": "ACTIVA",
  "fechaCreacion": "2026-10-12T17:30:00",
  "usuarioId": 1
}
```

El identificador y la fecha de creación son generados por el sistema. La cuenta se registra inicialmente en estado `ACTIVA`.

**Respuestas de error previstas:**

- `400 Bad Request`: solicitud inválida o datos que no superan las validaciones.
- `404 Not Found`: el usuario asociado no existe.
- `409 Conflict`: número de cuenta duplicado u otra regla de negocio incumplida.

#### `GET /cuentas`

Recupera todas las cuentas bancarias registradas.

No requiere parámetros ni cuerpo de solicitud.

**Respuesta exitosa — `200 OK`:**

```json
[
  {
    "id": 1,
    "numeroCuenta": "CTA-000001",
    "tipo": "CAJA_AHORRO",
    "saldo": 15000.00,
    "moneda": "ARS",
    "estado": "ACTIVA",
    "fechaCreacion": "2026-10-12T17:30:00",
    "usuarioId": 1
  }
]
```

Si no existen cuentas registradas, se devuelve una lista vacía.

#### `GET /cuentas/{id}`

Recupera una cuenta bancaria a partir de su identificador.

**Parámetro de ruta:** `id`, identificador único de la cuenta.

Ejemplo:

```http
GET /cuentas/1
```

**Respuestas esperadas:**

- `200 OK`: cuenta encontrada.
- `400 Bad Request`: identificador inválido.
- `404 Not Found`: cuenta inexistente.

#### `GET /cuentas/usuario/{usuarioId}`

Recupera las cuentas asociadas a un usuario.

**Parámetro de ruta:** `usuarioId`, identificador del usuario propietario.

Ejemplo:

```http
GET /cuentas/usuario/1
```

**Respuesta exitosa — `200 OK`:** devuelve una lista de cuentas asociadas al usuario.

Si el usuario existe, pero no posee cuentas, se devuelve una lista vacía. La respuesta `404 Not Found` corresponde al caso en que el servicio compruebe que el usuario no existe.

#### `PUT /cuentas/{id}`

Actualiza los datos de una cuenta bancaria existente.

**Parámetro de ruta:** `id`, identificador de la cuenta que se desea actualizar.

**Cuerpo de la solicitud:**

```json
{
  "numeroCuenta": "CTA-000001",
  "tipo": "CAJA_AHORRO",
  "saldo": 20000.00,
  "moneda": "ARS",
  "usuarioId": 1
}
```

La solicitud debe incluir los campos requeridos por `CuentaRequest`. El servicio verifica las reglas de negocio y la validez de los datos asociados.

**Respuestas esperadas:**

- `200 OK`: cuenta actualizada.
- `400 Bad Request`: solicitud inválida o datos que no superan las validaciones.
- `404 Not Found`: cuenta o usuario asociado inexistente.
- `409 Conflict`: número de cuenta duplicado u otra regla de negocio incumplida.

#### `DELETE /cuentas/{id}`

Solicita la eliminación de una cuenta bancaria.

**Parámetro de ruta:** `id`, identificador de la cuenta que se desea eliminar.

Ejemplo:

```http
DELETE /cuentas/1
```

No requiere cuerpo de solicitud.

**Respuestas esperadas:**

- `204 No Content`: cuenta eliminada correctamente. La respuesta no contiene cuerpo.
- `400 Bad Request`: identificador inválido.
- `404 Not Found`: cuenta inexistente.
- `409 Conflict`: una restricción o regla de negocio impide la eliminación, cuando corresponda.

La eliminación se encuentra sujeta a las reglas de negocio y las restricciones de persistencia implementadas en el proyecto.

## Respuestas de error

La API contempla excepciones de negocio y de recursos inexistentes. El manejador global de excepciones se encarga de traducirlas a respuestas HTTP.

| Código | Significado | Ejemplos |
|---|---|---|
| `400 Bad Request` | Solicitud inválida | JSON mal formado o campos que no superan las validaciones |
| `404 Not Found` | Recurso inexistente | Usuario o cuenta no encontrados |
| `409 Conflict` | Conflicto con las reglas de negocio | Email, teléfono o número de cuenta duplicado |

El formato concreto del cuerpo de error depende de la implementación del manejador global de excepciones.

### Ejemplo de respuesta de error

Al consultar una cuenta inexistente, la API puede devolver una respuesta `404 Not Found` con la siguiente estructura:

```json
{
  "fecha": "2026-10-17T19:00:00",
  "estado": 404,
  "error": "Not Found",
  "mensaje": "No se encontró la cuenta con ID: 999"
}
```

La fecha y el identificador son ilustrativos. El mensaje concreto depende de la operación que haya generado el error.

## Manejo de errores y excepciones

La API implementa mecanismos de manejo de errores para responder de forma controlada ante situaciones inválidas, recursos inexistentes y conflictos con las reglas de negocio.

### Excepciones personalizadas

- `RecursoNoEncontradoException`: representa la ausencia de un usuario o una cuenta solicitados.
- `ReglaNegocioException`: representa el incumplimiento de una regla de negocio.

### Uso de `try/catch`

En `CuentaService`, los métodos de creación y actualización de cuentas utilizan bloques `try/catch` para capturar `DataIntegrityViolationException` durante la persistencia.

Cuando se produce una violación de integridad de datos, el servicio la traduce a una excepción de negocio, evitando exponer directamente detalles internos de la base de datos al consumidor de la API.

### Manejo centralizado

La clase `GlobalExceptionHandler`, anotada con `@RestControllerAdvice`, centraliza el tratamiento de las excepciones y las transforma en respuestas HTTP.

Entre los casos gestionados se encuentran:

- Recursos inexistentes: `404 Not Found`.
- Reglas de negocio incumplidas: `409 Conflict`.
- Errores de validación o solicitudes con formato incorrecto: `400 Bad Request`.
- Errores inesperados: `500 Internal Server Error`.

Las respuestas de error utilizan un formato JSON uniforme con información sobre la fecha, el código HTTP, la descripción del error y un mensaje explicativo.

Este enfoque separa la lógica de negocio del tratamiento HTTP y facilita mantener respuestas consistentes en los distintos endpoints.

## Probar con Postman

La colección de solicitudes se encuentra en:

```text
postman/tp-api-cuentas.postman_collection.json
```

### Procedimiento

1. Abra Postman.
2. Seleccione **Import** e importe el archivo de la colección.
3. Verifique que MySQL esté iniciado y que la API se encuentre en ejecución.
4. Ejecute las solicitudes en el orden definido en la colección.
5. Revise el código HTTP, el cuerpo de la respuesta y los resultados de las pruebas de cada solicitud.

La colección contiene operaciones de usuarios y cuentas, junto con verificaciones de algunos escenarios de validación y errores HTTP.

Las solicitudes posteriores pueden depender de los identificadores generados por las anteriores. Por ese motivo, se recomienda comenzar con el registro de un usuario y utilizar el ID obtenido al crear una cuenta.

## Ejecutar las pruebas automatizadas

Para limpiar, compilar y ejecutar la suite de pruebas, utilice:

```powershell
.\mvnw.cmd clean test
```

Los tests unitarios de servicios utilizan JUnit 5 y Mockito para simular los repositorios y comprobar el comportamiento de la lógica de negocio sin depender directamente de consultas reales a la base de datos.

Entre los escenarios contemplados se encuentran:

- Creación de usuarios y cuentas con datos válidos.
- Rechazo de emails o teléfonos duplicados.
- Rechazo de números de cuenta duplicados.
- Validación de saldos negativos.
- Rechazo de operaciones con usuarios inexistentes o inactivos.
- Búsqueda de recursos inexistentes.
- Verificación del cambio de estado de un usuario.

El proyecto también incluye una prueba de contexto de Spring Boot para comprobar que la aplicación pueda inicializarse correctamente.

El resultado esperado cuando la compilación y todas las pruebas finalizan correctamente es `BUILD SUCCESS`.

## Modelo de dominio

### `Persona`

Clase abstracta base que reúne los atributos comunes de una persona: identificador, nombre, apellido y email.

Se utiliza `@MappedSuperclass` para compartir estos atributos con las entidades que heredan de ella, sin crear una tabla independiente para la clase base.

### `Usuario`

Representa a un usuario registrado en el sistema. Extiende `Persona` e implementa la interfaz `Activable`.

Incluye teléfono, fecha de alta y estado de actividad. El usuario se crea inicialmente como activo y puede activarse o desactivarse mediante las operaciones correspondientes.

### `Activable`

Define un contrato común para las operaciones de activación, desactivación y consulta del estado.

### `Cuenta`

Representa una cuenta bancaria vinculada a un usuario. Incluye número de cuenta, tipo, saldo, moneda, estado y fecha de creación.

Se utiliza `BigDecimal` para representar importes monetarios y evitar los problemas de precisión habituales de los tipos de punto flotante binario.

### `TipoCuenta`

Enum que define los tipos de cuenta permitidos:

- `CAJA_AHORRO`
- `CUENTA_CORRIENTE`

### `EstadoCuenta`

Enum que define los estados posibles de una cuenta:

- `ACTIVA`
- `BLOQUEADA`
- `CERRADA`

### Relación entre usuarios y cuentas

La relación se implementa mediante una asociación muchos-a-uno desde `Cuenta` hacia `Usuario`.

Un usuario puede tener varias cuentas y cada cuenta está asociada a un único usuario. La relación se configura con carga diferida (`FetchType.LAZY`) para evitar recuperar automáticamente el usuario relacionado en todas las consultas de cuentas.

## Estructura general del proyecto

```text
src/
├── main/
│   ├── java/com/tpfintech/tpcuentas/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── exception/
│   │   ├── model/
│   │   ├── repository/
│   │   └── service/
│   └── resources/
│       └── application.yml
└── test/
    └── java/com/tpfintech/tpcuentas/
        ├── service/
        └── TpApiCuentasApplicationTests.java

postman/
└── tp-api-cuentas.postman_collection.json
```

### Responsabilidad de cada paquete

| Paquete | Responsabilidad |
|---|---|
| `config` | Configuración de la aplicación y documentación OpenAPI |
| `controller` | Recepción de solicitudes HTTP y construcción de respuestas |
| `dto` | Definición de los datos de entrada y salida de la API |
| `exception` | Excepciones utilizadas para representar errores de negocio y recursos inexistentes |
| `model` | Entidades, enumeraciones e interfaces del dominio |
| `repository` | Acceso a los datos mediante Spring Data JPA |
| `service` | Reglas de negocio y coordinación de las operaciones |

## Decisiones de diseño

- **Separación por capas:** los controladores gestionan las solicitudes HTTP, los servicios concentran la lógica de negocio y los repositorios se encargan del acceso a datos.
- **DTOs:** separan el contrato HTTP de las entidades persistidas y permiten controlar los datos que se reciben y exponen.
- **Inyección de dependencias por constructor:** hace explícitas las dependencias de los componentes y facilita su sustitución durante las pruebas.
- **Spring Data JPA:** reduce el código repetitivo necesario para las operaciones de persistencia.
- **Jakarta Validation:** permite declarar restricciones sobre los datos de entrada y rechazar solicitudes inválidas.
- **Excepciones de negocio:** representan situaciones como reglas incumplidas o recursos inexistentes, en lugar de mezclar esas decisiones con la construcción de respuestas HTTP.
- **Enums persistidos como texto:** facilitan la lectura de los tipos y estados almacenados en la base de datos.
- **`BigDecimal`:** permite representar importes monetarios con precisión decimal.
- **Carga diferida de relaciones:** evita recuperar automáticamente entidades relacionadas cuando no se necesitan.
- **Pruebas con Mockito:** permiten aislar los servicios de la base de datos y verificar sus reglas de negocio.

## Alcance

Este proyecto es una API académica de demostración. No debe considerarse un sistema bancario listo para producción.