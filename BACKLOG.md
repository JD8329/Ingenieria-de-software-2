# Product Backlog — AgroValle Connect

Formato de estimación: **Story Points** (escala de Fibonacci: 1, 2, 3, 5, 8, 13)
Priorización: **MoSCoW** (Must have, Should have, Could have, Won't have)

---

## HU-01: Registro e Identificación de Agricultores
**Prioridad:** Must have | **Story Points:** 5 | **Estado:**  Completada

Como Agricultor, quiero registrarme en la plataforma ingresando mi cédula,
nombre y finca por municipio, para ofrecer mis cosechas directamente a
comercios urbanos.

- **Given** que un agricultor no registrado accede al endpoint `/api/v1/auth/register`
  **When** envía un JSON con su nombre completo, cédula válida y ubicación geográfica dentro del Valle del Cauca (ej. Dagua)
  **Then** el sistema retorna un código 201 Created, persiste la información en PostgreSQL y le asigna el rol de PRODUCCIÓN

---

## HU-02: Publicación de Lotes de Cosecha
**Prioridad:** Must have | **Story Points:** 5

Como Agricultor autenticado, quiero publicar lotes de cosechas disponibles
detallando cantidad, precio y fecha de recolección, para que los
comerciantes puedan visualizarlos y comprarlos.

- **Given** un agricultor autenticado con un token JWT válido
  **When** envía POST a `/api/v1/productos` con tipo de producto, cantidad (kg), precio unitario y fecha_cosecha futura
  **Then** el sistema valida que la fecha no sea anterior al día actual y retorna 201 Created con el ID único del lote

---

## HU-03: Visualización y Cálculo de Precios Promedio Regionales
**Prioridad:** Should have | **Story Points:** 3

Como Usuario de la plataforma, quiero consultar el precio promedio regional
por producto en el Valle del Cauca, para negociar a precios justos y
transparentes.

- **Given** al menos 50 transacciones registradas de "Café" en las últimas 24 horas dentro del departamento
  **When** el usuario solicita `/api/v1/precios/promedio?producto=cafe`
  **Then** el sistema calcula la media aritmética y despliega el valor en COP con status 200 OK

---

## HU-04: Filtro de Productos por Municipio de Origen y Categoría
**Prioridad:** Must have | **Story Points:** 5

Como Comerciante urbano, quiero filtrar el catálogo de productos por
municipio (ej. Dagua, Buga, Palmira) y categoría, para abastecerme
eficientemente según mi ubicación y necesidad.

- **Given** un comerciante navegando el catálogo
  **When** aplica municipio="Dagua" y categoria="Frutas" en `/api/v1/productos/buscar`
  **Then** el sistema retorna únicamente los lotes activos de Dagua correspondientes a frutas, con stock y precio

---

## HU-05: Contacto Directo entre Comprador y Productor
**Prioridad:** Could have | **Story Points:** 3

Como Comerciante, quiero enviar mensajes directos al agricultor de un lote
específico, para resolver dudas técnicas o acordar detalles del despacho.

- **Given** un comerciante autenticado viendo la ficha técnica de un lote
  **When** envía un mensaje vía `/api/v1/mensajes` especificando el ID del lote
  **Then** el sistema notifica al agricultor y almacena el historial del chat en base de datos

---

## HU-06: Generación y Reserva de Pedido Directo
**Prioridad:** Must have | **Story Points:** 8

Como Comerciante urbano, quiero consolidar un carrito y emitir una orden de
compra directa sobre los productos seleccionados, para asegurar el
inventario a un precio acordado sin intermediarios.

- **Given** un comerciante autenticado con ítems en su carrito
  **When** hace checkout con POST a `/api/v1/pedidos`
  **Then** el sistema valida disponibilidad, descuenta stock, emite la orden en estado RESERVADO y notifica al agricultor

---

## HU-07: Programación y Trazabilidad de Ruta de Despacho
**Prioridad:** Should have | **Story Points:** 8

Como Agricultor, quiero confirmar el alistamiento del lote y programar la
ruta de envío, para brindar seguimiento logístico en tiempo real al
comprador.

- **Given** un agricultor con un pedido confirmado en estado RESERVADO
  **When** ingresa datos de transporte y fecha estimada en `/api/v1/pedidos/{id}/despacho`
  **Then** el estado cambia a EN_TRANSITO y el comprador ve la ruta y fecha estimada de recepción en Cali

---

## HU-08: Confirmación de Recepción y Calificación del Servicio
**Prioridad:** Should have | **Story Points:** 3

Como Comerciante urbano, quiero confirmar la recepción de la mercancía y
calificar la calidad del producto, para generar reputación transparente en
la plataforma.

- **Given** un pedido en estado ENTREGADO asignado a un comerciante
  **When** envía calificación (1-5) y comentarios a `/api/v1/pedidos/{id}/calificar`
  **Then** el sistema actualiza la calificación promedio del agricultor y marca la transacción como finalizada (200 OK)

---

## HU-09: Notificación Automática de Cambios de Estado del Pedido
**Prioridad:** Should have | **Story Points:** 5

Como Usuario (Agricultor/Comerciante), quiero recibir notificaciones
automáticas ante cada cambio de estado de mi pedido, para estar informado
de la evolución logística.

- **Given** la implementación del patrón Observer para eventos del módulo de pedidos
  **When** el estado de un pedido cambia (ej. RESERVADO → EN_TRANSITO)
  **Then** el sistema dispara un evento Observer que notifica de inmediato al comprador y al productor

---

## HU-10: Reportes y Panel de Control de Ventas por Municipio
**Prioridad:** Could have | **Story Points:** 5

Como Administrador / Productor, quiero consultar un panel de control con
métricas de ventas acumuladas por municipio, para analizar la demanda y
planificar futuras siembras.

- **Given** un usuario con rol de administración o producción autenticado
  **When** consulta `/api/v1/reportes/ventas-municipio`
  **Then** el sistema procesa los volúmenes de venta por municipio y los retorna organizados (200 OK)

---

## HU-11: Inicio de Sesión con JWT
**Prioridad:** Must have | **Story Points:** 5

Como Usuario registrado (Agricultor o Comerciante), quiero iniciar sesión
con mi correo y contraseña, para acceder de forma segura a las funciones
protegidas de la plataforma.

- **Given** un usuario registrado
  **When** envía credenciales correctas a `/api/v1/auth/login`
  **Then** el sistema retorna un token JWT válido con status 200 OK
- **Given** credenciales incorrectas
  **When** intenta iniciar sesión
  **Then** el sistema retorna 401 Unauthorized sin generar token

---

## HU-12: Registro de Comerciantes
**Prioridad:** Must have | **Story Points:** 3

Como Comerciante urbano, quiero registrarme en la plataforma con mis datos
de negocio, para poder explorar el catálogo y realizar pedidos.

- **Given** un comerciante no registrado
  **When** envía POST a `/api/v1/auth/register` con datos válidos y rol COMERCIANTE
  **Then** el sistema crea la cuenta, la persiste en PostgreSQL y retorna 201 Created

---

## HU-13: Edición y Baja de Lotes Publicados
**Prioridad:** Should have | **Story Points:** 3

Como Agricultor, quiero editar o dar de baja un lote publicado, para
mantener actualizado mi inventario disponible.

- **Given** un lote propio existente sin pedidos activos asociados
  **When** el agricultor actualiza cantidad, precio o lo desactiva vía `/api/v1/productos/{id}`
  **Then** el sistema guarda los cambios y retorna 200 OK
- **Given** un lote que no pertenece al agricultor autenticado
  **When** intenta modificarlo
  **Then** el sistema retorna 403 Forbidden

---

## HU-14: Perfil Público del Agricultor
**Prioridad:** Could have | **Story Points:** 3

Como Comerciante, quiero ver el perfil público de un agricultor con su
calificación promedio y lotes activos, para decidir con quién negociar.

- **Given** un agricultor con lotes publicados y calificaciones previas
  **When** un comerciante consulta `/api/v1/agricultores/{id}/perfil`
  **Then** el sistema retorna sus datos públicos, calificación promedio y catálogo activo

---

## HU-15: Recuperación de Contraseña
**Prioridad:** Should have | **Story Points:** 5

Como Usuario registrado, quiero recuperar mi contraseña a través de mi
correo, para no perder acceso a mi cuenta en caso de olvido.

- **Given** un usuario registrado que olvidó su contraseña
  **When** solicita recuperación vía `/api/v1/auth/recuperar-password` con su correo
  **Then** el sistema envía un enlace de restablecimiento válido por tiempo limitado

---

## Resumen de Estimación (Story Points totales)

| Prioridad | HUs | Story Points |
|---|---|---|
| Must have | HU-01, 02, 04, 06, 11, 12 | 31 |
| Should have | HU-03, 07, 08, 09, 13, 15 | 27 |
| Could have | HU-05, 10, 14 | 11 |
| **Total** | **15 HU** | **69** |

## Verificación INVEST

Cada historia fue revisada contra los criterios INVEST (Independiente,
Negociable, Valiosa, Estimable, Pequeña, Evaluable) por el equipo completo
antes de ser priorizada.