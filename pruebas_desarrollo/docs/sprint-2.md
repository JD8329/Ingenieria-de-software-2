# Iteración 2 — HU-03 y HU-04

## Objetivo

Consultar precios promedio regionales a partir de ventas registradas y filtrar el
catálogo de lotes activos por municipio de origen y categoría.

**Alcance:** HU-03 (3 puntos) y HU-04 (5 puntos), 8 puntos de historia. Este documento registra
el alcance y las decisiones técnicas del sprint; no representa una aprobación
formal del Product Owner. La implementación está integrada en `main`.

## Decisiones de implementación

- La publicación de un lote requiere categoría. Se almacena como texto y se
  filtra sin distinguir mayúsculas/minúsculas. Las filas anteriores reciben
  `Sin clasificar` como valor predeterminado del esquema.
- El agricultor registra la venta de su propio lote autenticándose con JWT.
  El servidor fija la fecha/hora; el cliente no puede retrofechar ventas.
- Registrar una venta descuenta el stock bajo bloqueo de la fila del lote. No
  se admite vender más stock del disponible; agotarlo desactiva el lote.
- Una transacción representa una venta registrada por el agricultor. No hay aún
  comerciante, pedido ni confirmación de comprador; por tanto, este sprint no
  acredita independientemente que la venta ocurrió.
- El promedio es aritmético por transacción (no ponderado por kilos), distingue
  producto sin diferenciar mayúsculas/minúsculas y usa ventas de las últimas
  24 horas. Con menos de 50 transacciones se responde `422`.
- Los parámetros de municipio y categoría son opcionales en el filtro. Solo se
  devuelven lotes activos; una categoría coincide sin distinguir mayúsculas.

## API entregada

| Método | Ruta | Acceso | Resultado |
|---|---|---|---|
| `POST` | `/api/v1/productos` | Agricultor con JWT | Publica lote con tipo, categoría, existencias, precio y fecha. |
| `GET` | `/api/v1/productos/buscar?municipio=Dagua&categoria=Frutas` | Público | Filtra lotes activos; cada filtro es opcional. |
| `POST` | `/api/v1/productos/{loteId}/transacciones` | Agricultor dueño con JWT | Registra venta, precio realizado y descuenta existencias; `201`. |
| `GET` | `/api/v1/precios/promedio?producto=cafe` | Público | Devuelve cantidad de ventas, promedio COP y ventana consultada; `200` con 50 o más. |

El promedio devuelve `422 Unprocessable Content` cuando no se alcanza el mínimo
de 50 transacciones en la ventana. Un municipio desconocido produce `400`;
categoría y municipio se normalizan sin depender de las mayúsculas.

## Verificación

Las pruebas de integración cubren filtrado combinado, venta autenticada,
propiedad del lote, ajuste de existencias, ventana de 24 horas, umbral de 50 y cálculo
aritmético. `./mvnw --batch-mode --no-transfer-progress clean verify` finalizó
con éxito: 26 pruebas aprobadas, cero fallos/errores, Checkstyle sin
violaciones y cobertura JaCoCo sobre el umbral configurado. La implementación
se integró en `main` mediante el commit `91378c0` del 17 de octubre de 2026. No
se encontró evidencia de revisión formal por pares, demo ni aceptación del
Product Owner. Consulta la [revisión del Sprint 2](sprint-2-review.md) y la
[retrospectiva del Sprint 2](sprint-2-retrospective.md).
