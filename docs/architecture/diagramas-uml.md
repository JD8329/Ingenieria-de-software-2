# Diagramas UML de AgroValle Connect

**Estado:** diagramas basados en el código del repositorio al 22/11/2026.
El despliegue en staging es una propuesta, no una infraestructura existente.

Este documento consolida los entregables de la Sesión 13: tres diagramas de
secuencia, dos de comunicación (colaboración), uno de componentes y uno de
despliegue. Los ejemplos de la guía se ajustaron a las clases, rutas, modelos y
configuración que realmente existen; por ejemplo, el login corresponde a HU-11,
no a HU-07, y los tokens expiran en 3600 segundos, no en 24 horas.

## 1. Secuencia: login JWT (HU-11)

`AuthController` valida la solicitud y delega en `AuthService`. El servicio
busca primero un agricultor y después un comerciante, compara la contraseña con
BCrypt y genera un JWT HS256 con `subject`, `rol`, emisor y expiración de una
hora.

```mermaid
sequenceDiagram
    autonumber
    actor Usuario
    participant Controller as AuthController
    participant Service as AuthService
    participant Agricultores as AgricultorRepository
    participant Comerciantes as ComercianteRepository
    participant Encoder as PasswordEncoder (BCrypt)
    participant Jwt as JwtEncoder

    Usuario->>Controller: POST /api/v1/auth/login
    Controller->>Controller: Validar LoginRequest (@Valid)
    Controller->>Service: login(request)
    Service->>Agricultores: findByCorreo(correo)
    alt Agricultor encontrado y contraseña válida
        Service->>Encoder: matches(contraseña, hash)
        Encoder-->>Service: true
        Service->>Jwt: firmar claims (subject, rol, issuer, exp)
        Jwt-->>Service: JWT HS256
        Service-->>Controller: TokenResponse
        Controller-->>Usuario: 200 OK + Bearer token
    else No coincide una cuenta de agricultor
        Service->>Comerciantes: findByCorreo(correo)
        alt Comerciante encontrado y contraseña válida
            Service->>Encoder: matches(contraseña, hash)
            Encoder-->>Service: true
            Service->>Jwt: firmar claims (subject, COMERCIANTE, issuer, exp)
            Jwt-->>Service: JWT HS256
            Service-->>Controller: TokenResponse
            Controller-->>Usuario: 200 OK + Bearer token
        else Ninguna cuenta coincide
            Service-->>Controller: Optional.empty()
            Controller-->>Usuario: 401 Unauthorized
        end
    end
```

La configuración real está en `AuthService`, `AuthController` y
`SeguridadConfig`; no existe un `JwtUtil` separado.

## 2. Secuencia: publicar lote autenticado (HU-02)

La seguridad exige `ROLE_PRODUCCION` para `POST /api/v1/productos`. El
controlador obtiene el ID del `subject` JWT; el servicio valida agricultor,
fecha y duplicidad antes de persistir. La publicación del evento de lote se
registra después del commit.

```mermaid
sequenceDiagram
    autonumber
    actor Agricultor
    participant Security as Spring Security Resource Server
    participant Controller as LoteCosechaController
    participant Service as LoteCosechaService
    participant Agricultores as AgricultorRepository
    participant Lotes as LoteCosechaRepository
    participant DB as PostgreSQL
    participant Events as ApplicationEventPublisher
    participant Listener as LoteCosechaPublicadoListener

    Agricultor->>Security: POST /api/v1/productos + Bearer JWT
    Security->>Security: Validar firma, issuer, expiración y ROLE_PRODUCCION
    alt Token ausente, inválido o sin rol requerido
        Security-->>Agricultor: 401/403
    else Token autorizado
        Security->>Controller: Solicitud autenticada (subject)
        Controller->>Controller: Validar LoteCosechaRequest (@Valid)
        Controller->>Service: publicar(subject como agricultorId, datos)
        Service->>Agricultores: findById(agricultorId)
        Agricultores->>DB: SELECT agricultor
        DB-->>Agricultores: Agricultor
        Agricultores-->>Service: Agricultor
        Service->>Service: Validar fecha no anterior a hoy
        Service->>Lotes: existsByAgricultorIdAndTipoProducto...AndActivoTrue
        Lotes->>DB: Consultar duplicado activo
        DB-->>Lotes: existe / no existe
        Lotes-->>Service: Resultado de duplicidad
        alt Fecha inválida o lote duplicado
            Service-->>Controller: Error de validación o ConflictoException
            Controller-->>Agricultor: 400/409
        else Datos válidos y lote no duplicado
            Service->>Lotes: save(lote)
            Lotes->>DB: INSERT lote_cosecha
            DB-->>Lotes: Lote persistido
            Lotes-->>Service: Lote con ID
            Service->>Events: publishEvent(LoteCosechaPublicadoEvent)
            Note over Events,Listener: El listener usa @TransactionalEventListener(AFTER_COMMIT)
            Events-->>Listener: LoteCosechaPublicadoEvent
            Service-->>Controller: Lote guardado
            Controller-->>Agricultor: 201 Created + LoteCosechaResponse
        end
    end
```

## 3. Secuencia: programar despacho y notificar cambio de estado

El endpoint actual es `POST /api/v1/pedidos/{id}/despacho`. Cambia un pedido
`RESERVADO` a `EN_TRANSITO`, guarda el pedido y publica
`CambioEstadoPedidoEvent`; el observer persiste una notificación para cada
agricultor asociado.

```mermaid
sequenceDiagram
    autonumber
    actor Cliente as Cliente API
    participant Controller as PedidoController
    participant Service as PedidoService
    participant Orders as PedidoRepository
    participant Events as ApplicationEventPublisher
    participant Observer as NotificacionCambioEstadoObserver
    participant Notifications as NotificacionRepository
    participant DB as PostgreSQL

    Cliente->>Controller: POST /api/v1/pedidos/{id}/despacho
    Controller->>Service: programarDespacho(id, datos, agricultorId opcional)
    Service->>Orders: findById(id)
    Orders->>DB: SELECT pedido y detalles
    DB-->>Orders: Pedido
    Orders-->>Service: Pedido
    Service->>Service: Validar estado RESERVADO, fecha futura y ruta
    alt Regla de negocio incumplida
        Service-->>Controller: Error de negocio / recurso no encontrado
        Controller-->>Cliente: Error HTTP
    else Pedido válido
        Service->>Service: Cambiar estado a EN_TRANSITO
        Service->>Orders: save(pedido)
        Orders->>DB: UPDATE pedido
        DB-->>Orders: Pedido actualizado
        Orders-->>Service: Pedido actualizado
        Service->>Events: publishEvent(CambioEstadoPedidoEvent)
        Events-->>Observer: @EventListener
        Observer->>Notifications: saveAll(notificaciones por agricultor)
        Notifications->>DB: INSERT notificaciones
        DB-->>Notifications: Notificaciones guardadas
        Observer-->>Events: Evento atendido
        Service-->>Controller: Pedido actualizado
        Controller-->>Cliente: 200 OK + DespachoResponse
    end
```

**Limitación de autorización:** en el código actual `PedidoController` recibe
`agricultorId` como parámetro opcional; no lo obtiene del principal autenticado.
Además, `SeguridadConfig` permite las demás rutas no declaradas explícitamente.
El diagrama describe el comportamiento existente, no una autorización completa.

## 4. Comunicación (colaboración): reservar pedido

Las etiquetas numeradas indican el orden de los mensajes entre objetos. El
checkout recibe `comercianteId` en el cuerpo de la solicitud; no lo deriva de
una identidad autenticada.

```mermaid
flowchart LR
    Cliente["Cliente API"]
    Controller["PedidoController"]
    Service["PedidoService"]
    Lotes["LoteCosechaRepository"]
    Pedidos["PedidoRepository"]
    Notificaciones["NotificacionRepository"]
    DB[("PostgreSQL")]

    Cliente -->|"1. POST /api/v1/pedidos (comercianteId, items)"| Controller
    Controller -->|"2. reservar(PedidoRequest)"| Service
    Service -->|"3. Ordenar IDs y rechazar lotes repetidos"| Service
    Service -->|"4. findByIdForUpdate(loteId)"| Lotes
    Lotes -->|"5. Leer y bloquear lote"| DB
    DB -->|"6. Estado, stock y agricultor"| Lotes
    Service -->|"7. Validar activo/stock; descontar y crear detalle"| Service
    Service -->|"8. save(pedido RESERVADO, total y detalles)"| Pedidos
    Pedidos -->|"9. INSERT pedido y detalles"| DB
    Service -->|"10. saveAll(notificaciones PEDIDO)"| Notificaciones
    Notificaciones -->|"11. INSERT notificaciones"| DB
    Controller -->|"12. 201 Created + PedidoResponse"| Cliente
    Service -.->|"404 / 409 / error: rollback transaccional"| Controller
```

## 5. Comunicación (colaboración): Observer de cambios de pedido

```mermaid
flowchart LR
    Cliente["Cliente API"]
    Controller["PedidoController"]
    Service["PedidoService"]
    Orders["PedidoRepository"]
    Publisher["ApplicationEventPublisher"]
    Observer["NotificacionCambioEstadoObserver"]
    Notifications["NotificacionRepository"]
    DB[("PostgreSQL")]

    Cliente -->|"1. POST /pedidos/{id}/despacho"| Controller
    Controller -->|"2. programarDespacho(...)"| Service
    Service -->|"3. save(pedido con nuevo estado)"| Orders
    Orders -->|"4. UPDATE pedido"| DB
    Service -->|"5. publishEvent(CambioEstadoPedidoEvent)"| Publisher
    Publisher -->|"6. Notificar suscriptores @EventListener"| Observer
    Observer -->|"7. Crear una notificación por agricultor distinto"| Observer
    Observer -->|"8. saveAll(notificaciones CAMBIO_ESTADO)"| Notifications
    Notifications -->|"9. INSERT notificaciones"| DB
    Service -->|"10. Retornar Pedido actualizado"| Controller
    Controller -->|"11. 200 OK + DespachoResponse"| Cliente
```

El observer de cambios de pedido usa `@EventListener` y guarda notificaciones
en la misma operación; no envía correo ni notifica al comerciante. Es distinto
del evento de publicación de lotes, cuyo listener usa `AFTER_COMMIT`.

## 6. Componentes

```mermaid
flowchart LR
    Client["Cliente HTTP / JSON"]
    subgraph API["AgroValle Connect · API REST"]
        Controllers["controllers<br/>AuthController · AgricultorController<br/>LoteCosechaController · PedidoController<br/>PrecioController · ReporteController<br/>MensajeController · NotificacionController"]
        DTO["dto<br/>Requests y Responses"]
        Services["services<br/>Auth · Agricultor · Comerciante<br/>Lotes · Pedidos · Precios<br/>Mensajes · Reportes · Recuperación"]
        Models["models<br/>Agricultor · Comerciante · LoteCosecha<br/>Pedido · DetallePedido · Notificacion<br/>TransaccionPrecio · demás entidades"]
        Repos["repositories<br/>Spring Data JpaRepository"]
        Events["events<br/>CambioEstadoPedidoEvent<br/>NotificacionCambioEstadoObserver<br/>LoteCosechaPublicadoListener"]
        Errors["exceptions<br/>GlobalExceptionHandler"]
        Security["SeguridadConfig<br/>JWT Resource Server · BCrypt"]
    end
    JPA["Hibernate / Spring Data JPA"]
    DB[("PostgreSQL")]

    Client --> Controllers
    Controllers <--> DTO
    Controllers --> Services
    Services --> Models
    Services --> Repos
    Repos --> JPA
    JPA --> DB
    Services -->|"ApplicationEventPublisher"| Events
    Events --> Repos
    Security -.-> Controllers
    Errors -.-> Controllers
```

Los componentes representan las clases y paquetes del backend. La base PostgreSQL
es configurable; el esquema staging no está configurado por este diagrama.

## 7. Despliegue: estado actual y staging propuesto

La línea continua representa configuración o flujo existente. La conexión
punteada es una posibilidad futura, no un despliegue realizado.

```mermaid
flowchart LR
    subgraph Dev["Desarrollo local · disponible"]
        Developer["Desarrollador"]
        App["Aplicación Spring Boot<br/>Java 17"]
        LocalDB[("PostgreSQL configurado por entorno")]
        Developer --> App
        App -->|"JDBC y variables externas"| LocalDB
    end

    subgraph GitHub["GitHub · configurado"]
        Repo["Repositorio AgroValle Connect"]
        Workflow[".github/workflows/ci.yml<br/>Checkstyle · JUnit · JaCoCo"]
        Runner["GitHub Actions<br/>Ubuntu · Java 17"]
        Repo -->|"Pull Request / push"| Workflow
        Workflow --> Runner
    end

    subgraph Future["Staging · propuesto, no configurado"]
        Deploy["Servicio de aplicación PaaS<br/>Proveedor por decidir"]
        StageDB[("PostgreSQL staging<br/>agrovalle_staging")]
        Deploy -->|"JDBC SSL · STAGING_DB_URL"| StageDB
    end

    Runner -.->|"Requeriría workflow de despliegue y aprobación"| Deploy
```

**Estado verificado:** el workflow actual solo ejecuta Checkstyle, JUnit y
JaCoCo; no hay `cd.yml`, despliegue a PaaS, perfil `application-staging.yml`,
ejecución de `schema.sql` en staging ni uso de `STAGING_DB_URL`. La decisión de
proveedor y topología está pendiente; véase el
[ADR-001 propuesto](../architecture-decisions/ADR-001-despliegue-staging-paas.md)
y la [guía de staging](../staging-preproduccion.md).

## Referencias de implementación

- [Configuración de seguridad](../../src/main/java/co/uniajc/agrovalle/agrovalleconnect/config/SeguridadConfig.java)
- [Autenticación](../../src/main/java/co/uniajc/agrovalle/agrovalleconnect/services/AuthService.java)
- [Publicación de lotes](../../src/main/java/co/uniajc/agrovalle/agrovalleconnect/services/LoteCosechaService.java)
- [Pedidos y cambios de estado](../../src/main/java/co/uniajc/agrovalle/agrovalleconnect/services/PedidoService.java)
- [Guía de staging](../staging-preproduccion.md)
