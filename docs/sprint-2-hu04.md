# Sprint 2 — HU-04: filtro del catálogo por municipio y categoría

## Objetivo

Permitir que un comerciante consulte lotes activos del catálogo por municipio
de origen, categoría o ambos. La búsqueda no distingue mayúsculas y
minúsculas en la categoría, y utiliza los municipios admitidos por el modelo
`Municipio`.

## API

`GET /api/v1/productos/buscar` es público y admite los filtros opcionales:

```text
GET /api/v1/productos/buscar?municipio=DAGUA&categoria=Frutas
```

Se puede enviar únicamente `municipio` o únicamente `categoria`. Si se omiten
ambos filtros o se envía una categoría vacía, el servicio responde `400 Bad
Request`. Los valores de municipio deben corresponder al enum existente:
`DAGUA`, `PALMIRA`, `BUGA`, `TULUA`, `CAICEDONIA` o `JAMUNDI`.

La respuesta es `200 OK` con una lista de lotes que incluye producto,
categoría, municipio, cantidad disponible, precio unitario y estado. Solo se
devuelven lotes activos que coinciden con todos los filtros indicados.

## Compatibilidad y persistencia

`LoteCosecha` ahora almacena una categoría de texto opcional de hasta 50
caracteres. Se permite omitirla al publicar un lote para conservar el contrato
de HU-02. Los lotes existentes sin categoría siguen apareciendo en el catálogo
general y en búsquedas por municipio, pero no coinciden con un filtro de
categoría.

La respuesta existente de lotes incorpora los campos `categoria` y
`municipio` sin retirar los campos anteriores. El filtro se aplica en la
consulta a la base de datos, restringiendo allí tanto el estado activo como los
valores solicitados.

## Pruebas

Las pruebas de integración verifican filtros combinados y por separado,
comparación de categoría sin distinguir mayúsculas, exclusión de lotes
inactivos, disponibilidad de precio y cantidad, compatibilidad con lotes sin
categoría y rechazo de solicitudes sin filtros.
