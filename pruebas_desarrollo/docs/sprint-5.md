# Sprint 5 — HU-09 y HU-10

## Objetivo

Notificar a las partes cuando cambia el estado de un pedido y exponer métricas
de ventas completadas por municipio.

**Alcance:** HU-09 (5 puntos) y HU-10 (5 puntos), 10 puntos de historia. La
implementación técnica y las pruebas no representan por sí mismas una
aprobación formal por pares.

## HU-09 — Notificaciones de cambios de estado

- Cada cambio de estado publica `PedidoEstadoCambiadoEvent`.
- Un listener síncrono del evento guarda una notificación para el agricultor y
  otra para el comerciante asociados al pedido. La notificación y el cambio de
  estado pertenecen a la misma transacción.
- Se notifica la reserva inicial (`RESERVADO`) y las transiciones
  `EN_TRANSITO`, `ENTREGADO` y `FINALIZADO`.
- Las notificaciones son persistentes y se consultan en
  `GET /api/v1/notificaciones`; no se envían por correo ni mediante push.

## HU-10 — Reporte de ventas por municipio

`GET /api/v1/reportes/ventas-municipio` devuelve una fila para cada municipio
configurado, incluso cuando todavía no registra ventas.

| Campo | Descripción |
|---|---|
| `municipio` | Municipio del agricultor productor. |
| `pedidosFinalizados` | Cantidad de pedidos finalizados. |
| `volumenVendidoKg` | Suma de kilogramos en los detalles de esos pedidos. |
| `totalVentas` | Suma de cantidad por precio unitario histórico de los detalles. |

Solo se consideran pedidos en estado `FINALIZADO`; reservas en curso o pedidos
entregados aún no calificados no cuentan como ventas completadas. El endpoint
requiere autenticación y el rol `PRODUCCION`, que es el rol de consulta de
reportes disponible en el sistema. La aplicación aún no tiene un rol ni un
flujo de autenticación de administración, por lo que no se incorpora ese flujo
como parte de estas historias.

## API

| Método | Ruta | Acceso | Resultado |
|---|---|---|---|
| `GET` | `/api/v1/reportes/ventas-municipio` | Agricultor productor autenticado | Métricas agregadas por municipio; `200`. |
| `GET` | `/api/v1/notificaciones` | Usuario autenticado | Lista las notificaciones propias, incluidas las de cambios de pedido. |

Fragmento de ejemplo de respuesta:

```json
[
  {
    "municipio": "DAGUA",
    "pedidosFinalizados": 2,
    "volumenVendidoKg": 35.5,
    "totalVentas": 71000.0
  },
  {
    "municipio": "PALMIRA",
    "pedidosFinalizados": 0,
    "volumenVendidoKg": 0.0,
    "totalVentas": 0.0
  }
]
```

## Verificación

`Sprint5ApiIntegrationTest` verifica la notificación a ambas partes en las tres
transiciones logísticas y el reporte con varios municipios, agregación de
detalles, exclusión de pedidos no finalizados, municipios sin ventas y control
de acceso. `Sprint3ApiIntegrationTest` comprueba que el checkout notifique a
ambas partes por cada pedido reservado.
