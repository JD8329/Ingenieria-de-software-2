# Sprint 5 — Historias HU-09 y HU-10

## Alcance

Este incremento completa la visibilidad operativa y de demanda del producto:

- HU-09 implementa notificaciones automáticas ante cambios de estado de un pedido.
- HU-10 incorpora un reporte agregado de ventas por municipio.

Las operaciones continúan siguiendo el patrón de la API REST existente y no dependen
de correo electrónico ni de servicios externos. Las notificaciones se persisten en la
base de datos y pueden consultarse por agricultor.

## HU-09: Notificación automática de cambios de estado

### Objetivo de negocio

Como usuario productor o comerciante, quiero recibir notificaciones ante cada cambio
de estado de mi pedido para estar informado de la logística y cerrar cada operación
con trazabilidad.

### Criterios de aceptación implementados

- Cuando un pedido pasa de `RESERVADO` a `EN_TRANSITO`, se genera una notificación
  persistida para cada agricultor asociado al pedido.
- Cuando un pedido pasa a `FINALIZADO`, se genera una notificación persistida para
  cada agricultor asociado al pedido.
- La notificación conserva tipo `CAMBIO_ESTADO`, el ID del pedido, el estado anterior,
  el estado nuevo y la fecha de creación.
- Los productores con varios lotes en el mismo pedido reciben una sola notificación
  por el cambio de estado.
- El cambio de estado ya validado por la lógica de negocio no puede publicar
  notificaciones duplicadas durante una ejecución normal de Spring.

### Diseño Observer

Se utiliza el publicación de eventos de Spring:

- `CambioEstadoPedidoEvent`: describe el pedido y la transición de estado.
- `PedidoService`: publica el evento después de guardar el nuevo estado.
- `NotificacionCambioEstadoObserver`: escucha el evento y crea las notificaciones.

La implementación conserva un mecanismo de respaldo en `PedidoService` para las
pruebas unitarias y ejecutores sin contenedor Spring. En ambos casos se persiste
una notificación por agricultor distinto involucrado en el pedido.

### Ejemplos de notificación

```json
{
  "tipo": "CAMBIO_ESTADO",
  "mensaje": "Pedido 40 cambió de RESERVADO a EN_TRANSITO",
  "referenciaId": 40
}
```

La consulta existente permite recuperar el historial:

`GET /api/v1/notificaciones/agricultores/{agricultorId}`

### Reglas de negocio

- Solo las operaciones que tengan una transición válida publican el evento.
- El observador identifica los agricultores desde los detalles reales del pedido.
- Una notificación no modifica el estado ni el inventario del pedido.
- Los cambios deben continuar sujetos a los criterios de autorización y estado
  definidos en los sprints anteriores.

## HU-10: Reportes y panel de ventas por municipio

### Objetivo de negocio

Como administrador o productor, quiero consultar métricas de ventas por municipio
para analizar demanda y planificar siembras.

### Criterios de aceptación implementados

- `GET /api/v1/reportes/ventas-municipio` responde `200 OK`.
- El municipio se obtiene del agricultor asociado a cada detalle vendido.
- Se calcula la cantidad de pedidos únicos por municipio.
- Se calcula la cantidad total de kilogramos vendidos por municipio.
- Se calcula el valor total vendido por municipio en pesos colombianos.
- Los municipios se devuelven ordenados y los valores monetarios usan dos decimales.

### Contrato de respuesta

```json
[
  {
    "municipio": "PALMIRA",
    "cantidadPedidos": 2,
    "cantidadKg": 35.00,
    "totalVentas": 87500.00
  }
]
```

### Reglas de agrupación

- Cada detalle del pedido aporta su cantidad y subtotal al municipio del agricultor
  propietario del lote.
- Un pedido que tenga lotes del mismo municipio se cuenta una sola vez.
- Un pedido con lotes de municipios diferentes aporta métricas a cada municipio.
- El reporte es de solo lectura y no modifica inventario ni pedidos.

### API propuesta

`GET /api/v1/reportes/ventas-municipio`

No requiere parámetros en esta versión del sprint. Una versión posterior puede
añadir rangos de fecha, categoría de producto o filtro por productor sin cambiar
el esquema base del resultado.

## Verificación

Pruebas mínimas del sprint:

- la transición `RESERVADO -> EN_TRANSITO` crea una notificación por estado;
- la transición a `FINALIZADO` crea una notificación por estado;
- un agricultor aparece una sola vez aunque participe en varios detalles del pedido;
- el reporte agrupa cantidad de pedidos, kilogramos y valor por municipio;
- el endpoint `/api/v1/reportes/ventas-municipio` responde `200 OK`.

Ejecutar:

```bash
./mvnw.cmd test
```
