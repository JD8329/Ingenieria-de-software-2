# Sprint 2 — HU-03: precios promedio regionales

## Objetivo

Permitir consultar el precio promedio regional por producto usando los
registros de precio de los municipios del Valle del Cauca. El cálculo usa la
media aritmética de las transacciones registradas en las 24 horas anteriores.
Solo se publica el resultado cuando existen al menos 50 registros para el
producto consultado.

## API

### Registrar una transacción de precio

`POST /api/v1/precios/transacciones` requiere autenticación JWT y registra el
productor autenticado como responsable del dato. El servidor asigna la fecha y
hora de registro.

```json
{
  "producto": "Cafe",
  "municipio": "PALMIRA",
  "precioUnitarioCop": 12500.50
}
```

La respuesta es `201 Created` e incluye el identificador, el producto, el
municipio, el precio unitario en COP y la fecha de registro. Producto y
municipio son obligatorios; el precio debe ser positivo y admite hasta dos
decimales.

### Consultar el promedio

`GET /api/v1/precios/promedio?producto=cafe` es de consulta pública y compara
el nombre del producto sin distinguir mayúsculas y minúsculas. El resultado
incluye el número de transacciones utilizadas, el promedio redondeado a dos
decimales en COP y el periodo aplicado:

```json
{
  "producto": "cafe",
  "cantidadTransacciones": 50,
  "precioPromedioCop": 12500.50,
  "periodoHoras": 24
}
```

Si hay menos de 50 registros recientes del producto, la respuesta es `422
Unprocessable Entity` con un mensaje explicativo. Las transacciones de otros
productos o anteriores a las últimas 24 horas no se incluyen.

## Persistencia y pruebas

La entidad `TransaccionPrecio` guarda producto, municipio, precio, fecha de
registro y productor que reportó el dato. Un índice compuesto por producto y
fecha apoya la consulta temporal. La consulta agregada se ejecuta en la base de
datos y calcula conteo y promedio en una sola operación.

Las pruebas de integración cubren el umbral de 50 registros, el promedio
aritmético, la ventana temporal, la separación por producto, el registro
autenticado, la validación de datos y el rechazo de solicitudes sin JWT.

## Alcance y evolución

El flujo de pedidos y ventas aún no existe (HU-06 está pendiente). Por tanto,
este registro captura precios reportados por un agricultor autenticado; no
representa una compra cerrada ni descuenta inventario. La fecha la asigna el
servidor y el reporte queda asociado al agricultor para preservar trazabilidad.
Cuando se implemente el flujo de pedidos, el registro deberá integrarse con
transacciones de compra confirmadas para reemplazar o complementar el reporte
manual.
