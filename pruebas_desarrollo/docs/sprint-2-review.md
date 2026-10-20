# Revisión y demo del Sprint 2 — AgroValle Connect

## Estado de la revisión

El historial del repositorio registra la implementación de HU-03 y HU-04 en el
commit `91378c0`, del 17 de octubre de 2026, y su integración en `main`. No se
encontró acta de Sprint Review, evidencia de demo ni comentarios o aceptación
del Product Owner. Por tanto, este documento presenta el incremento y la
verificación técnica disponibles; **no acredita una revisión formal ni
aceptación funcional por parte del Product Owner**.

| Campo | Registro |
|---|---|
| Historias del alcance | HU-03 (3 puntos) y HU-04 (5 puntos), 8 puntos en total |
| Fecha de integración técnica | 17/10/2026 |
| Commit de implementación | `91378c0` |
| Tipo de evidencia | Documentación del sprint, historial de Git y resultado de pruebas consignado en la documentación |
| Demo del incremento | No registrada |
| Comentarios o aceptación del Product Owner | No registrados |

## Incremento presentado

| Historia | Resultado documentado | Estado técnico |
|---|---|---|
| HU-03 — Consulta de precios promedio | Endpoint público `GET /api/v1/precios/promedio?producto={producto}`. Calcula el promedio aritmético de las transacciones de las últimas 24 horas; responde con promedio y cantidad de ventas cuando hay al menos 50, y `422` cuando no se alcanza el mínimo. | Implementada e integrada en `main`; no consta aceptación formal del Product Owner. |
| HU-04 — Filtro de lotes por municipio y categoría | Endpoint público `GET /api/v1/productos/buscar`, con filtros opcionales por municipio y categoría; devuelve lotes activos y compara la categoría sin distinguir mayúsculas/minúsculas. | Implementada e integrada en `main`; no consta aceptación formal del Product Owner. |

El registro de una venta se realiza por el agricultor dueño del lote con JWT.
La fecha la fija el servidor, se descuenta el inventario bajo bloqueo y no se
permite vender más existencias de las disponibles. Agotar el inventario
desactiva el lote. Según las decisiones documentadas, esa transacción no tiene
confirmación independiente de un comprador y, por sí sola, no acredita que la
venta haya ocurrido.

## Verificación técnica registrada

La documentación de [Sprint 2](sprint-2.md) registra que
`./mvnw --batch-mode --no-transfer-progress clean verify` finalizó con:

- 26 pruebas aprobadas, sin fallos ni errores.
- Checkstyle sin violaciones.
- Cobertura JaCoCo por encima del umbral configurado.
- `BUILD SUCCESS`.

La documentación describe pruebas de filtrado combinado, venta autenticada,
propiedad del lote, ajuste de existencias, ventana de 24 horas, mínimo de 50
transacciones y cálculo aritmético. No se encontró un enlace a una ejecución de
CI para este sprint; las cifras anteriores se atribuyen al registro del sprint
y no a una ejecución de CI independiente.

## Temas para confirmar con el Product Owner

- Si un promedio aritmético por transacción, sin ponderación por cantidad,
  representa la necesidad del usuario.
- Si la ventana de 24 horas y el mínimo de 50 transacciones son los criterios
  esperados y qué mensaje debe presentarse cuando no se alcanza el mínimo.
- Si una venta registrada únicamente por el agricultor es evidencia suficiente
  o si se requiere confirmación del comprador en una iteración posterior.

Estos puntos son decisiones documentadas y temas de validación; no se registran
como observaciones recibidas durante una demo.

## Pendiente de completar

- Registrar fecha, participantes y demo cuando se realice la Sprint Review.
- Añadir comentarios, decisiones y aceptación del Product Owner únicamente si
  se reciben.
- Registrar acciones de seguimiento acordadas durante la revisión.
