# Sprint 3 — Historias HU-05 y HU-06

## Alcance

Este incremento implementa el contacto comprador-productor por lote (HU-05) y la
reserva directa de existencias mediante checkout (HU-06). El checkout recibe
directamente los lotes y cantidades; todavía no hay una API para mantener un
carrito entre solicitudes.

Como el proyecto aún no cuenta con inicio de sesión ni registro de comerciantes,
ambos flujos reciben un `comercianteId` temporal. Este valor identifica la
operación, pero **no autentica al usuario ni autoriza el acceso**. Debe
reemplazarse por la identidad derivada del token cuando se implementen HU-11 y
HU-12. Las notificaciones se guardan en la base de datos; no se envían por correo
ni por un canal externo.

## HU-05: Contacto directo entre comprador y productor

### Criterios de aceptación implementados

- Un mensaje referencia un lote existente y conserva el ID temporal del
  comerciante, el contenido y la fecha de creación.
- El agricultor asociado al lote recibe una notificación persistida de tipo
  `MENSAJE`.
- Se puede consultar el historial del comerciante para un lote.
- Una solicitud inválida retorna `400`; un lote inexistente retorna `404`.

### API

`POST /api/v1/mensajes` — crea el mensaje y la notificación; responde `201`.

```json
{
  "comercianteId": 3,
  "loteId": 15,
  "contenido": "¿Cuándo estaría disponible el despacho?"
}
```

`GET /api/v1/mensajes?loteId=15&comercianteId=3` — consulta el historial
ordenado cronológicamente.

`GET /api/v1/notificaciones/agricultores/7` — consulta las notificaciones
persistidas para el agricultor.

## HU-06: Generación y reserva de pedido directo

### Criterios de aceptación implementados

- `POST /api/v1/pedidos` recibe uno o más lotes con cantidad solicitada en kg.
- Solo se aceptan pedidos de hasta 100 lotes activos con existencias
  suficientes; cantidades no positivas, lotes repetidos y pedidos vacíos se
  rechazan.
- El inventario, el pedido, sus detalles y las notificaciones se procesan en una
  sola transacción. Los lotes se bloquean al leerlos para evitar reservar dos
  veces la misma existencia concurrentemente.
- El pedido conserva el precio unitario aplicado, calcula subtotales y total en
  COP con dos decimales, queda en estado `RESERVADO` y notifica a cada agricultor
  involucrado.
- Una insuficiencia o lote inactivo retorna `409`; un lote inexistente retorna
  `404`; un cuerpo inválido retorna `400`.

### API

`POST /api/v1/pedidos` — confirma el checkout y responde `201`.

```json
{
  "comercianteId": 3,
  "items": [
    { "loteId": 15, "cantidadKg": 20 },
    { "loteId": 18, "cantidadKg": 5.5 }
  ]
}
```

La respuesta incluye el ID del pedido, el estado, el total y los detalles con
precio y subtotal. La actualización del inventario y la creación de las
notificaciones se revierten si ocurre un error durante la operación.

## Verificación

Las pruebas unitarias cubren el guardado y notificación de mensajes, la reserva
exitosa, el descuento de inventario, el rechazo de existencias insuficientes y
la detección de lotes repetidos. Ejecutar `mvn test` desde la raíz del proyecto.
