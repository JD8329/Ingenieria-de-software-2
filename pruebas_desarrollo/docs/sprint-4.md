# Sprint 4 — HU-07 y HU-08

## Objetivo

Completar el ciclo logístico de los pedidos reservados y permitir que el
comerciante confirme la recepción y califique el servicio.

**Alcance:** HU-07 (8 puntos) y HU-08 (3 puntos), 11 puntos de historia.

## Flujo entregado

El ciclo de estado queda definido así:

`RESERVADO → EN_TRANSITO → ENTREGADO → FINALIZADO`

- El agricultor dueño programa el despacho de un pedido reservado con
  transportista, ruta y fecha estimada de recepción.
- El comerciante dueño confirma la recepción de un pedido en tránsito mediante
  `POST /api/v1/pedidos/{id}/recibir`. Esta transición explícita resuelve el
  estado `ENTREGADO` requerido por HU-08.
- El comerciante dueño califica el pedido entregado de 1 a 5 y deja comentarios.
  La calificación se guarda en el pedido y actualiza el promedio persistido del
  agricultor. El pedido pasa a `FINALIZADO`.
- Las rutas, fecha estimada, calificación, comentarios y promedio se devuelven
  al consultar los pedidos. La fecha estimada se representa como fecha local
  (`AAAA-MM-DD`), sin conversión de zona horaria.
- Las solicitudes se validan y solo puede actuar el agricultor o comerciante
  asociado al pedido. Una transición fuera de secuencia responde `409 Conflict`;
  una identidad sin relación con el pedido responde `403 Forbidden`.

## API

| Método | Ruta | Acceso | Resultado |
|---|---|---|---|
| `POST` | `/api/v1/pedidos/{id}/despacho` | Agricultor dueño | Guarda transportista, ruta y fecha; cambia `RESERVADO` a `EN_TRANSITO`; `200`. |
| `POST` | `/api/v1/pedidos/{id}/recibir` | Comerciante dueño | Confirma entrega y cambia `EN_TRANSITO` a `ENTREGADO`; `200`. |
| `POST` | `/api/v1/pedidos/{id}/calificar` | Comerciante dueño | Guarda calificación y comentarios, actualiza el promedio y cambia `ENTREGADO` a `FINALIZADO`; `200`. |
| `GET` | `/api/v1/pedidos` | Agricultor o comerciante autenticado | Incluye los datos logísticos y de calificación de sus pedidos. |

### Ejemplo de despacho

```json
{
  "transportista": "Transportes Valle",
  "ruta": "Palmira - Cali",
  "fechaEstimadaRecepcion": "2026-10-20"
}
```

### Ejemplo de calificación

```json
{
  "calificacion": 5,
  "comentarios": "Entrega puntual y producto en buen estado"
}
```

El campo `calificacionPromedioAgricultor` se calcula a partir de las
calificaciones guardadas en pedidos del agricultor. El modelo `Transaccion` existente conserva el
historial de precios de lotes; por ello, el cierre comercial de HU-08 se
representa como estado `FINALIZADO` del pedido y no altera ese historial.

## Verificación

`Sprint4ApiIntegrationTest` cubre el despacho, visibilidad de la ruta por el
comprador, confirmación de recepción, calificación de varios pedidos y
recalculo del promedio. También verifica el rechazo de propietarios incorrectos,
transiciones fuera de secuencia y datos inválidos.

La implementación técnica y las pruebas no representan por sí mismas una
aprobación formal por pares.
