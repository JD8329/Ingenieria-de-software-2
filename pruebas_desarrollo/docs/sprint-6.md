# Sprint 6 — Revalidación de HU-10 y HU-11

## Objetivo

Reintentar la implementación de HU-10 (reportes de ventas por municipio) y
HU-11 (inicio de sesión con JWT), comprobar el comportamiento existente y
corregir discrepancias de alcance y documentación.

**Alcance revisado:** HU-10 (5 puntos) y HU-11 (5 puntos). Ambas historias ya
tenían implementación técnica en sprints anteriores: HU-10 en el Sprint 5 y
HU-11 como soporte de HU-02 en el Sprint 1. La revisión no requirió duplicar
funcionalidad ni modificar código de producción. Esta documentación no afirma
una nueva implementación ni una aprobación formal por pares.

## HU-10 — Reporte de ventas por municipio

`GET /api/v1/reportes/ventas-municipio` devuelve las métricas para todos los
municipios configurados. El reporte cuenta pedidos `FINALIZADO`, suma los
kilogramos de sus detalles y calcula el total con la cantidad y el precio
unitario histórico de cada detalle. Los municipios sin ventas aparecen con
valores en cero.

El endpoint requiere autenticación y el rol `PRODUCCION`. Aunque la redacción
anterior del backlog mencionaba administración, el sistema no cuenta con un rol
administrativo ni un flujo de autenticación para ese perfil. Se mantiene el
alcance ya acordado en el Sprint 5: el productor autenticado consulta el
reporte; agregar administración requeriría una historia y un alcance propios.

## HU-11 — Inicio de sesión con JWT

`POST /api/v1/auth/login` valida correo y contraseña para agricultores y
comerciantes. Credenciales válidas producen una respuesta `200` con un token
de acceso firmado con HS256 y los campos `accessToken`, `tokenType` y
`expiresIn`; las inválidas producen `401` sin token. Los tokens incluyen
sujeto, rol, emisor y expiración. La clave de firma se configura mediante
`JWT_SECRET` y requiere al menos 32 bytes; no se usa una clave predeterminada.

## Verificación

Se ejecutó:

```text
./mvnw "-Dtest=Sprint1ApiIntegrationTest,Sprint5ApiIntegrationTest" test
```

Resultado: **27 pruebas aprobadas**, 0 fallos, 0 errores y 0 omitidas.

- `Sprint1ApiIntegrationTest` cubre el rechazo de credenciales inválidas y
  utiliza el flujo de login exitoso para obtener tokens de acceso válidos,
  comprobar su respuesta y autorizar operaciones protegidas.
- `Sprint5ApiIntegrationTest` verifica el reporte, sus agregaciones, la
  inclusión de municipios sin ventas, la exclusión de pedidos no finalizados y
  el acceso para productor frente a comerciante y solicitudes sin token.
  También usa el login para autenticar a ambos perfiles.

## Resultado y límites

La revalidación confirmó que la implementación actual satisface el alcance
técnico documentado para estas historias; no se añadieron cambios de código.
El panel visual y el acceso administrativo al reporte no forman parte de la API
implementada ni se consideran entregados en este sprint. La revisión técnica y
las pruebas no sustituyen la aprobación formal por pares requerida por el DoD.
