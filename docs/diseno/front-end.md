# Interfaz web del módulo del agricultor

## Alcance

La interfaz implementa las cinco pantallas descritas en
[mockups-ui.md](mockups-ui.md): inicio de sesión, registro de agricultor, panel,
publicación de cosecha y catálogo de ofertas. Está construida con HTML, CSS y
JavaScript del navegador, sin un paso de compilación ni dependencias JavaScript
de terceros. Spring Boot entrega los archivos estáticos desde
`src/main/resources/static`; la interfaz y la API usan el mismo origen.

## Funcionalidad

- Registro con nombre, cédula, finca, municipio, correo, contraseña y
  confirmación. Las validaciones reflejan el contrato de registro: cédula de
  6–10 dígitos, contraseña de al menos 8 caracteres y un municipio admitido por
  el servidor.
- Inicio de sesión conectado a JWT. Solo las cuentas con rol `PRODUCCION`
  ingresan al panel del agricultor. El token se mantiene en `sessionStorage` y
  se envía en las solicitudes autenticadas; la autorización real sigue siendo
  responsabilidad de Spring Security.
- Panel con perfil, municipio y hasta cinco lotes activos del agricultor que
  inició sesión.
- Publicación de lote conectado a la API, con producto, categoría, cantidad,
  precio y fecha. Los errores de validación y las respuestas del servidor se
  muestran en pantalla.
- Catálogo público de lotes activos con filtros por producto, municipio y
  categoría, y paginación local de seis resultados por página.
- Diseño adaptable a móvil y escritorio, formularios etiquetados y mensajes
  anunciados a tecnologías de asistencia.

## Correspondencia con la API

| Pantalla o acción | API | Datos utilizados |
|---|---|---|
| Iniciar sesión | `POST /api/v1/auth/login` | `correo`, `contrasena`; almacena `accessToken` en la sesión del navegador. |
| Registrar agricultor | `POST /api/v1/agricultores/registro` | `nombreCompleto`, `cedula`, `correo`, `contrasena`, `telefono`, `ubicacionFinca`, `municipio`. |
| Cargar panel | `GET /api/v1/agricultores/{id}/perfil` y `GET /api/v1/productos/agricultor/{id}` | Nombre, municipio y lotes activos asociados al sujeto del JWT. |
| Publicar cosecha | `POST /api/v1/productos` | `tipoProducto`, `categoria`, `cantidadKg`, `precioUnitario`, `fechaCosecha`; requiere rol `PRODUCCION`. |
| Consultar ofertas | `GET /api/v1/productos` | Lotes activos; filtros y páginas se aplican sobre los resultados en el navegador. |

## Decisiones y límites conocidos

- Los wireframes son conceptuales. El contrato vigente del backend limita la
  cédula a 6–10 dígitos (el wireframe menciona hasta 15) y la finca se envía en
  el campo `ubicacionFinca`.
- El backend permite cosechas con fecha de hoy o futura, aunque el wireframe
  original dice que no debe ser futura. El formulario sigue la regla efectiva
  de la API.
- La solicitud de publicación no admite cantidad de stock separada; el lote se
  publica con `cantidadKg`. Tampoco existe una API independiente para consultar
  productos: las opciones de producto del catálogo se derivan de las ofertas
  activas.
- La API no ofrece paginación ni un filtro por nombre de producto. Por eso el
  filtrado y la paginación del catálogo se realizan en el cliente, luego de
  consultar las ofertas activas.
- La respuesta de ofertas no incluye el nombre del agricultor ni hay un
  endpoint de detalle del lote en el alcance de estos wireframes; el catálogo
  muestra producto, categoría, municipio, cantidad y precio, sin atribuir el
  lote a un nombre que la API no entrega.
- El backend admite comerciantes, pero las pantallas de comerciante (carrito,
  pedidos, despachos, etc.) no forman parte de estos mockups ni de esta
  interfaz. Una cuenta de comerciante no ingresa al panel de agricultor.
- La recuperación de contraseña tiene endpoints de backend, pero no una
  pantalla en el alcance actual.
- El token se elimina al cerrar sesión o al recibir una respuesta `401`.
  `sessionStorage` limita su persistencia a la pestaña/sesión del navegador; no
  reemplaza las protecciones de autorización del servidor.

## Archivos

- `src/main/resources/static/index.html`
- `src/main/resources/static/css/app.css`
- `src/main/resources/static/js/app.js`

Instrucciones para ejecutar la aplicación y abrir esta interfaz:
[Inicio fácil](../inicio-facil.md).
