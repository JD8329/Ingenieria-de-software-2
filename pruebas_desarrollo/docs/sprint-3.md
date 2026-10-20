# Sprint 3 — HU-05 y HU-06

## Objetivo

Habilitar comunicación directa entre comerciantes y agricultores y permitir que
un comerciante reserve productos desde un carrito persistente.

**Alcance:** HU-05 (3 puntos) y HU-06 (8 puntos), 11 puntos de historia. El
registro y el inicio de sesión de comerciantes (HU-12) se implementan como
dependencia técnica de estas historias. Este documento describe el alcance
técnico y no constituye aprobación formal de la persona responsable del
producto ni revisión por pares.

## Decisiones

- Las cuentas de comerciante registran nombre, negocio, NIT, correo, contraseña
  BCrypt y teléfono opcional. El correo se normaliza y no puede coincidir con el
  de un agricultor ya registrado.
- Agricultores y comerciantes usan tokens JWT con la reclamación `rol`. Las
  rutas de productores y comerciantes requieren el rol respectivo.
- Un comerciante puede enviar un mensaje de hasta 2000 caracteres a un lote
  activo. El mensaje y la notificación al agricultor se guardan en la misma
  transacción. El historial queda asociado a lote, comerciante y agricultor; el
  comprador solo consulta sus propios mensajes y el agricultor dueño puede ver
  las conversaciones de sus lotes.
- Las notificaciones se guardan en la aplicación y pueden marcarse como
  leídas. No se envían correos ni notificaciones a dispositivos, y no hay
  entrega en tiempo real.
- El carrito se persiste por comerciante. Agregar un lote acumula cantidades y
  valida contra las existencias conocidas; el carrito no reserva inventario.
- La confirmación de compra ordena y bloquea las filas de lotes, vuelve a
  validar disponibilidad y crea los pedidos, la reserva de existencias y las
  notificaciones en una transacción. Si falla un lote, se revierten los cambios
  y se conserva el carrito.
- Si el carrito mezcla productores, se crea un pedido `RESERVADO` por
  agricultor. El precio unitario vigente al confirmar la compra se conserva
  como precio histórico en el detalle. Los pedidos no se convierten todavía en
  transacciones de venta completada; el despacho, la cancelación y el
  vencimiento de reservas quedan fuera de esta iteración.

## API entregada

| Método | Ruta | Acceso | Resultado |
|---|---|---|---|
| `POST` | `/api/v1/auth/register` | Público | Registra a un comerciante; `201`. |
| `POST` | `/api/v1/auth/login` | Público | Autentica a un agricultor o comerciante y emite un token con rol; `200` / `401`. |
| `POST` | `/api/v1/mensajes` | Comerciante autenticado | Registra mensaje del lote y notifica al agricultor; `201`. |
| `GET` | `/api/v1/mensajes?loteId={id}` | Participante autenticado | Obtiene historial del lote con autorización por participante. |
| `GET` | `/api/v1/notificaciones` | Usuario autenticado | Lista notificaciones propias. |
| `PATCH` | `/api/v1/notificaciones/{id}/leer` | Usuario autenticado | Marca una notificación propia como leída. |
| `GET` | `/api/v1/carrito` | Comerciante autenticado | Consulta su carrito. |
| `POST` | `/api/v1/carrito/items` | Comerciante autenticado | Agrega un elemento o incrementa su cantidad. |
| `DELETE` | `/api/v1/carrito/items/{loteId}` | Comerciante autenticado | Retira un lote del carrito; `204`. |
| `POST` | `/api/v1/pedidos` | Comerciante autenticado | Confirma la compra y responde con los pedidos creados, agrupados por productor; `201`. |
| `GET` | `/api/v1/pedidos` | Agricultor o comerciante autenticado | Consulta pedidos propios según el rol. |

## Verificación

`Sprint3ApiIntegrationTest` verifica el registro y el inicio de sesión de
comerciantes, la confirmación de compra con varios productores, la reducción de
existencias, los pedidos y las notificaciones; la mensajería y su historial;
la autorización por rol; la validación de existencias y la reversión de cambios
cuando no se puede reservar todo el carrito. También se ejecutan las pruebas
anteriores de HU-01 a HU-04 para comprobar que no haya regresiones.
