# Sprint 7 — HU-14: Perfil público del agricultor

## Historia de usuario

Como comerciante, quiero consultar el perfil público de un agricultor con su
calificación y lotes activos para decidir con quién negociar.

## API

`GET /api/v1/agricultores/{id}/perfil`

El endpoint es público y retorna `200 OK` con:

- Identificador, nombre, finca y municipio del agricultor.
- Promedio con dos decimales y total de calificaciones.
- Cantidad de lotes activos y `catalogoActivo`, que contiene sus lotes
  disponibles con producto, categoría, cantidad, precio y fecha de cosecha.

No se exponen cédula, correo, teléfono ni contraseña. Los pedidos calificados se
cuentan una sola vez por pedido, aunque incluyan varios lotes del mismo
agricultor. El catálogo excluye lotes inactivos. Si aún no hay calificaciones,
el promedio es `0.00`; si no hay lotes activos, `catalogoActivo` es una lista
vacía.

Un identificador inexistente produce `404 Not Found`.

## Verificación

Las pruebas de integración cubren el acceso público, el cálculo de promedio sin
duplicar pedidos, la exclusión de lotes inactivos, la privacidad de datos, el
perfil sin actividad y el agricultor inexistente.
