# Sprint 7 — HU-13: Edición y baja de lotes publicados

## Historia de usuario

Como agricultor, quiero editar o dar de baja un lote publicado para mantener
actualizado mi inventario.

## Criterios de aceptación

- Un agricultor autenticado puede cambiar la cantidad y/o el precio de un lote
  propio cuando no tenga pedidos activos asociados.
- Un agricultor autenticado puede dar de baja un lote propio bajo la misma
  condición.
- Un usuario no puede modificar un lote ajeno.
- La baja conserva el registro y cambia su estado a inactivo.

## API

Todas las operaciones requieren un JWT con rol `PRODUCCION`; el identificador
del agricultor se obtiene del sujeto (`sub`) del token, no del cuerpo de la
solicitud.

### Actualizar cantidad o precio

`PATCH /api/v1/productos/{id}`

El cuerpo admite uno o ambos campos:

```json
{
  "cantidadKg": 80,
  "precioUnitario": 5200
}
```

Los valores informados deben ser mayores que cero y debe enviarse al menos uno.
Los campos omitidos conservan su valor actual.

### Dar de baja

`DELETE /api/v1/productos/{id}`

La respuesta es `200 OK` con el lote actualizado (`activo: false`). No se borra
físicamente el registro.

### Respuestas de error

- `400 Bad Request`: cuerpo vacío o cantidad/precio no positivo.
- `403 Forbidden`: rol distinto a `PRODUCCION` o lote propiedad de otro
  agricultor.
- `404 Not Found`: el lote no existe.
- `409 Conflict`: existe un pedido asociado que no está en estado `FINALIZADO`.

La comprobación de propiedad, bloqueo del lote y validación de pedidos se
ejecutan en una transacción. Se reutiliza el bloqueo pesimista empleado al
reservar existencias para evitar que la edición o baja compita con una reserva
simultánea.

## Verificación

Las pruebas unitarias cubren cambios parciales, propiedad, pedidos activos y
baja lógica. Las pruebas web/de integración verifican autenticación por rol,
respuestas HTTP y persistencia de los cambios.
