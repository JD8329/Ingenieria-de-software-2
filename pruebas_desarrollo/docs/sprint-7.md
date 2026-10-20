# Sprint 7 — HU-12 y HU-13

## Objetivo

Revalidar el registro de comerciantes (HU-12) y completar la edición y baja
lógica de lotes publicados (HU-13).

**Alcance:** HU-12 (3 puntos) y HU-13 (3 puntos), 6 puntos de historia. HU-12
ya se había implementado como dependencia de HU-05 y HU-06 durante el Sprint 3;
en este sprint se confirma su comportamiento mediante pruebas de integración.
La implementación y las pruebas no representan aprobación formal por pares.

## HU-12 — Registro de comerciantes

`POST /api/v1/auth/register` es público y recibe nombre completo, nombre del
negocio, NIT, correo, contraseña y teléfono opcional. Con datos válidos,
persiste el comerciante y responde `201 Created`; la contraseña se almacena
cifrada y no se expone en la respuesta. Los correos duplicados entre
agricultores y comerciantes, y los NIT duplicados, se rechazan con `409
Conflict`. La cuenta puede iniciar sesión en `POST /api/v1/auth/login`.

## HU-13 — Edición y baja de lotes publicados

Las operaciones requieren un JWT de agricultor con rol `PRODUCCION`. El
servicio bloquea el lote durante la operación, comprueba que pertenezca al
agricultor autenticado y rechaza cambios si hay pedidos asociados que sigan
activos (`RESERVADO`, `EN_TRANSITO` o `ENTREGADO`). Los pedidos `FINALIZADO`
son históricos y no impiden actualizar el lote.

| Método | Ruta | Resultado |
|---|---|---|
| `PATCH` | `/api/v1/productos/{loteId}` | Actualiza parcialmente `cantidadKg`, `precioUnitario` o ambos; responde `200 OK` con el lote actualizado. Se requiere al menos un campo y sus valores deben ser mayores que cero. |
| `DELETE` | `/api/v1/productos/{loteId}` | Realiza baja lógica (`activo: false`) y responde `200 OK` con el lote desactivado. No elimina el registro. |

Un lote inexistente responde `404`; un lote de otro agricultor, `403`; una
operación sobre un lote con pedidos activos, `409`; y una actualización sin
campos o con valores inválidos, `400`. Los lotes desactivados dejan de
aparecer en las consultas públicas que listan lotes activos.

## Verificación

Se ejecutó la suite de integración enfocada:

```text
./mvnw "-Dtest=Sprint7ApiIntegrationTest,Sprint3ApiIntegrationTest" test
```

Resultado: **13 pruebas aprobadas**, 0 fallos y 0 errores. Las pruebas del
Sprint 7 verifican el registro, persistencia y autenticación del comerciante;
la actualización y baja lógica del lote; el control de rol y propiedad; el
rechazo de cambios para pedidos `RESERVADO`, `EN_TRANSITO` y `ENTREGADO`; la
actualización permitida con pedidos `FINALIZADO`; y la validación de una
actualización vacía.

También se ejecutó `./mvnw verify`: **43 pruebas aprobadas**, 0 fallos,
0 errores, 0 omitidas y **0 violaciones de Checkstyle**. La cobertura mínima
configurada en JaCoCo también pasó.
