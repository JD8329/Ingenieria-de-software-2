# Planificación del Sprint 1 — AgroValle Connect

## Meta del sprint

Permitir que un agricultor se registre y publique un lote de cosecha con cantidad,
precio y fecha validados y persistidos. El sprint compromete **10 Story Points**:
HU-01 (5 SP) y HU-02 (5 SP).

## Historias seleccionadas

| Historia | Prioridad | Puntos | Resultado esperado |
|---|---:|---:|---|
| HU-01: Registro e identificación de agricultores | Must Have | 5 | Crear y persistir un agricultor con datos válidos; responder `201 Created`. |
| HU-02: Publicación de lotes de cosecha | Must Have | 5 | Asociar y persistir un lote con datos válidos y fecha no anterior al día actual; responder `201 Created` y su identificador. |
| **Total** |  | **10** |  |

## Descomposición técnica e ISO/IEC 25010

### HU-01 — Registro e identificación de agricultores

| ID | Tarea técnica | Característica ISO/IEC 25010 | Verificación |
|---|---|---|---|
| HU01-T1 | Mantener la entidad JPA `Agricultor` y su persistencia con Spring Data JPA/PostgreSQL. | Mantenibilidad — modularidad; adecuación funcional — completitud funcional. | Guardar y recuperar los datos del agricultor en una prueba con H2. |
| HU01-T2 | Validar los campos obligatorios del registro y evitar duplicados de cédula o correo. | Fiabilidad — tolerancia a fallos; adecuación funcional — corrección funcional. | Casos válidos, campos inválidos y duplicados en pruebas automatizadas. |
| HU01-T3 | Exponer el registro REST y responder con el estado y representación acordados. | Adecuación funcional — corrección funcional; seguridad — confidencialidad. | `POST /api/v1/agricultores/registro` responde `201`; la contraseña no se devuelve. |

### HU-02 — Publicación de lotes de cosecha

| ID | Tarea técnica | Característica ISO/IEC 25010 | Verificación |
|---|---|---|---|
| HU02-T1 | Mantener la entidad JPA `LoteCosecha`, su relación con `Agricultor` y el repositorio. | Mantenibilidad — modularidad; adecuación funcional — completitud funcional. | Prueba de persistencia y asociación con un agricultor existente. |
| HU02-T2 | Validar tipo, cantidad positiva, precio positivo y fecha de cosecha no anterior al día actual. | Fiabilidad — integridad y consistencia de datos. | Pruebas de validación para datos aceptados y rechazados. |
| HU02-T3 | Exponer la publicación REST y retornar `201 Created` con el ID del lote. | Adecuación funcional — corrección funcional; seguridad — responsabilidad/identificación. | Prueba de integración sobre `POST /api/v1/productos`. |
| HU02-T4 | Emitir JWT al validar credenciales del agricultor y proteger la publicación; derivar la identidad desde el sujeto autenticado. | Seguridad — autenticidad, responsabilidad y control de acceso. | Login válido genera token firmado; publicación con token válido responde `201`; sin token o con token inválido responde `401`. |

> El alcance del sprint incorpora el mínimo de HU-11 requerido por HU-02: login de agricultores y emisión/verificación de JWT. La contraseña se almacena con BCrypt y el secreto HS256 se inyecta mediante `JWT_SECRET`; no se debe simular un token en las pruebas ni usar un secreto por defecto en producción.

## Tarea obligatoria: traducción BDD a pruebas automatizadas JUnit 5

**ID:** T-TEST-01
**Historia:** HU-02
**Calidad:** fiabilidad (integridad de datos) y mantenibilidad (capacidad de ser probado).

### Escenario BDD del Backlog

- **Given** un agricultor registrado que inicia sesión con credenciales válidas.
- **When** publica un lote con token JWT válido y fecha de cosecha igual o posterior al día actual.
- **Then** se persiste asociado al agricultor autenticado y la API responde `201 Created` con el ID.

### Traducción a pruebas

La prueba `Sprint1ApiIntegrationTest` prepara un agricultor con contraseña BCrypt
en H2, obtiene el token mediante `POST /api/v1/auth/login` y envía `POST
/api/v1/productos` con el encabezado de autorización. Verifica `201`, un ID de
respuesta y la persistencia del lote. Los escenarios complementarios verifican
credenciales inválidas, solicitud sin token, fecha pasada, valores no positivos y
un agricultor eliminado después de emitir su token.

## Criterios de terminación del sprint

- Las pruebas JUnit 5 de los escenarios acordados pasan.
- Checkstyle pasa como parte de `./mvnw verify`.
- La cobertura de líneas JaCoCo del proyecto es al menos 60%.
- La integración se realiza mediante Pull Request con revisión visible de un par.
- El DoD y el Backlog reflejan el estado real de las historias.
- La autenticación JWT queda implementada y probada; HU-02 no se marca como
  terminada hasta que el PR correspondiente reciba revisión e integración.
