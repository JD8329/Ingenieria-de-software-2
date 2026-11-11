# Sprint 4 — Historias HU-07 y HU-08

## Alcance

Este incremento consolida la logística de entrega y la retroalimentación del servicio en la experiencia del comercio directo. Se documentan dos historias del backlog de producto:

- HU-07: programación y trazabilidad de ruta de despacho
- HU-08: confirmación de recepción y calificación del servicio

La finalidad del sprint es permitir que el agricultor programe la salida del pedido y que el comerciante confirme la entrega, cierre la operación y registre una valoración que impacte la reputación del productor.

## HU-07: Programación y trazabilidad de ruta de despacho

### Objetivo de negocio

Como agricultor, quiero confirmar el alistamiento del lote y programar la ruta de envío, para dar seguimiento logístico en tiempo real.

### Criterios de aceptación esperados

- Un pedido en estado `RESERVADO` puede ser aceptado para despacho por parte del agricultor responsable.
- El agricultor debe registrar los datos del transporte, la fecha estimada de entrega y, cuando aplique, la ruta o puntos de tránsito.
- El sistema cambia el estado del pedido a `EN_TRANSITO` y almacena la programación del despacho.
- El comprador puede consultar la información de la entrega y la fecha estimada de recepción.
- Si el pedido no pertenece al agricultor autenticado o el estado no permite el despacho, se rechaza la operación con error de negocio o autorización.
- El historial del pedido debe conservar los cambios de estado para trazabilidad posterior.

### API propuesta

`POST /api/v1/pedidos/{id}/despacho`

Permite registrar la programación del transporte y la ruta estimada.

```json
{
  "transportadora": "Transporte Valle Verde",
  "conductor": "Luis Pérez",
  "vehiculo": "Toyota H100",
  "fechaEstimadaEntrega": "2026-11-15T09:00:00",
  "ruta": [
    "Palmira",
    "Cali",
    "Jamundí"
  ],
  "observaciones": "Salida con horario matutino y entrega estimada en 6 horas."
}
```

Respuesta esperada:

```json
{
  "pedidoId": 42,
  "estado": "EN_TRANSITO",
  "fechaEstimadaEntrega": "2026-11-15T09:00:00",
  "ruta": ["Palmira", "Cali", "Jamundí"]
}
```

`GET /api/v1/pedidos/{id}/seguimiento`

Consulta la ruta y el estado del pedido en tiempo real, mostrando el último evento logístico y la fecha estimada.

### Reglas de negocio

- Solo se puede programar despacho cuando el pedido está en `RESERVADO`.
- La fecha estimada de entrega debe ser válida y posterior a la fecha actual.
- El inventario ya queda descontado en la reserva inicial; el despacho solo cambia la logística y la trazabilidad.
- El sistema debe registrar el cambio de estado y notificar al comprador sobre la nueva etapa del pedido.
- La operación debe quedar persistida para auditoría, seguimiento y soporte posterior.

## HU-08: Confirmación de recepción y calificación del servicio

### Objetivo de negocio

Como comerciante urbano, quiero confirmar la recepción de la mercancía y calificar la calidad, para generar reputación transparente.

### Criterios de aceptación esperados

- Un pedido en estado `ENTREGADO` o equivalente de cierre logístico puede ser confirmado por el comerciante responsable.
- El comerciante registra una calificación del servicio en una escala de 1 a 5 y, opcionalmente, un comentario de texto.
- El sistema marca la transacción como finalizada y actualiza la calificación promedio del agricultor.
- La calificación debe quedar asociada al agricultor y a la operación específica del pedido.
- Si la calificación está fuera del rango permitido o el pedido no corresponde al comerciante, la solicitud se rechaza.
- El sistema debe conservar una bitácora de la valoración para futuras consultas y análisis de reputación.

### API propuesta

`POST /api/v1/pedidos/{id}/calificar`

Registra la recepción y la valoración del servicio.

```json
{
  "comercianteId": 3,
  "calificacion": 5,
  "comentario": "La calidad fue excelente y la entrega fue puntual."
}
```

Respuesta esperada:

```json
{
  "pedidoId": 42,
  "estado": "FINALIZADO",
  "calificacionPromedioAgricultor": 4.8,
  "comentarioRegistrado": "La calidad fue excelente y la entrega fue puntual."
}
```

`GET /api/v1/agricultores/{id}/perfil`

Debe mostrar la calificación promedio del agricultor y la reputación asociada a sus entregas previas.

### Reglas de negocio

- La calificación debe ser un entero entre 1 y 5.
- El comentario, si se incluye, debe ser válido y estar ligado a la operación concreta.
- La operación solo puede calificarse cuando el pedido se encuentra en una etapa de entrega o cierre válida.
- La reputación del agricultor se calcula en función del promedio ponderado de todas sus calificaciones históricas.
- La transacción se marca como finalizada para evitar nuevas modificaciones en la valoración y cerrar el ciclo comercial.

## Consideraciones de implementación

- La trazabilidad del pedido debe conservar los cambios de estado junto con la fecha y el actor responsable.
- Las notificaciones deben registrarse en la base de datos para que el comprador reciba actualizaciones del estado del pedido y del despacho.
- La calificación debe ser consistente con la reputación pública del agricultor y compatible con las métricas de demanda del catálogo.
- El flujo aún depende del tratamiento de autenticación y autorización iniciado en HU-11 y HU-12; por ello, las operaciones deben diseñarse para utilizar el usuario autenticado en lugar de un identificador temporal.

## Verificación

La verificación del sprint debe centrarse en pruebas de integración y validación de negocio para confirmar:

- cambio correcto de `RESERVADO` a `EN_TRANSITO`
- trazabilidad del despacho y consulta de seguimiento
- validación del rango de calificación y límite de comentarios
- actualización de la calificación promedio del agricultor
- cierre y finalización del pedido sin reabrir nuevas operaciones

Se recomienda ejecutar `mvn test` desde la raíz del proyecto para validar el comportamiento de la lógica asociada a pedidos, despachos y reputación.
