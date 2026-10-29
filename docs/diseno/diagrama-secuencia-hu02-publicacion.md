# Secuencia HU-02 — publicación autenticada de un lote

Este diagrama Mermaid editable describe el flujo implementado por
`LoteCosechaController`, `LoteCosechaService` y
`LoteCosechaPublicadoListener`. Incluye los focos de control y las alternativas
de autenticación, validación y duplicidad. No es una captura ni una imagen
exportada.

```mermaid
sequenceDiagram
    actor Agricultor
    participant Filtro as Filtro JWT / Spring Security
    participant Controller as LoteCosechaController
    participant Service as LoteCosechaService
    participant Agricultores as AgricultorRepository
    participant Lotes as LoteCosechaRepository
    participant DB as PostgreSQL
    participant Events as ApplicationEventPublisher
    participant Listener as LoteCosechaPublicadoListener

    Agricultor->>Filtro: POST /api/v1/productos (solicitud autenticada)
    activate Filtro
    alt token ausente o inválido
        Filtro-->>Agricultor: 401 Unauthorized
        deactivate Filtro
    else token válido
        Filtro->>Controller: Solicitud autenticada con subject
        deactivate Filtro
        activate Controller
        Note over Controller: @Valid valida el cuerpo antes de invocar el servicio
        alt cuerpo inválido
            Controller-->>Agricultor: 400 Bad Request
        else cuerpo válido
            Controller->>Service: publicarLote(agricultorId, lote)
            activate Service
            Service->>Agricultores: findById(agricultorId)
            activate Agricultores
            Agricultores->>DB: SELECT agricultor
            activate DB
            DB-->>Agricultores: Agricultor
            deactivate DB
            Agricultores-->>Service: Agricultor
            deactivate Agricultores
            Service->>Lotes: existsByAgricultorIdAndTipoProductoIgnoreCaseAndFechaCosechaAndActivoTrue(...)
            activate Lotes
            Lotes->>DB: Consultar lote activo del mismo producto y fecha
            activate DB
            DB-->>Lotes: existe / no existe
            deactivate DB
            Lotes-->>Service: resultado de duplicidad
            deactivate Lotes
            alt lote activo duplicado
                Service-->>Controller: ConflictoException
                Controller-->>Agricultor: 409 Conflict
            else no existe duplicado
                Service->>Lotes: save(lote)
                activate Lotes
                Lotes->>DB: INSERT lote
                activate DB
                DB-->>Lotes: lote persistido con id
                deactivate DB
                Lotes-->>Service: lote guardado
                deactivate Lotes
                Service->>Events: publishEvent(LoteCosechaPublicadoEvent)
                Service-->>Controller: lote guardado
                deactivate Service
                Note over Events,Listener: Spring invoca el listener al confirmar la transacción
                Events-->>Listener: LoteCosechaPublicadoEvent
                activate Listener
                Listener->>Listener: Registrar publicación
                deactivate Listener
                Controller-->>Agricultor: 201 Created + LoteCosechaResponse
            end
        end
        deactivate Controller
    end
```
