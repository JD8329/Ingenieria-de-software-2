# Arquitectura MVC en cuatro capas y patrones de diseño

**Sistema:** backend REST de AgroValle Connect
**Tecnologías:** Java 17, Spring Boot, Spring MVC, Spring Security, Spring Data
JPA y PostgreSQL.

## 1. Estilo arquitectónico

El backend sigue una arquitectura MVC en capas para una API REST. No incluye
una vista web MVC: el cliente consume endpoints HTTP y envía o recibe JSON.

```mermaid
flowchart TB
    client["Cliente REST<br/>HTTP / JSON"]
    subgraph presentation["Capa 1 · Presentación y API REST"]
        controllers["AgricultorController<br/>AuthController<br/>LoteCosechaController"]
        dto["DTO de solicitud y respuesta"]
    end
    subgraph domain["Capa 2 · Servicio y dominio"]
        services["AgricultorService<br/>AuthService<br/>LoteCosechaService"]
        models["Agricultor<br/>LoteCosecha<br/>Municipio · Rol"]
    end
    subgraph access["Capa 3 · Acceso a datos"]
        repos["AgricultorRepository<br/>LoteCosechaRepository"]
        jpa["Spring Data JPA / Hibernate"]
    end
    subgraph persistence["Capa 4 · Persistencia"]
        db[("PostgreSQL")]
    end
    client --> controllers
    controllers --> dto
    controllers --> services
    services --> models
    services --> repos
    repos --> jpa
    jpa --> db
    security["Seguridad transversal<br/>Spring Security · JWT · BCrypt"] -.-> controllers
    errors["Validación y errores<br/>Bean Validation · GlobalExceptionHandler"] -.-> controllers
```

## 2. Responsabilidades por capa

| Capa | Paquetes y componentes | Responsabilidad | Estado |
|---|---|---|---|
| 1. Presentación / REST | `controllers`, `dto` | Recibir solicitudes, ejecutar validación de entrada, delegar al servicio y producir respuestas/códigos HTTP. | Implementada. |
| 2. Servicio / dominio | `services`, `models`, `exceptions` | Aplicar las reglas de registro, autenticación y publicación; representar entidades y errores de dominio. | Implementada para HU-01, login de soporte y HU-02. |
| 3. Repositorio / acceso a datos | `repositories` | Abstraer consultas y operaciones CRUD mediante `JpaRepository`. | Implementada para agricultor y lote. |
| 4. Persistencia / PostgreSQL | PostgreSQL, Hibernate / JPA | Guardar los datos y hacer cumplir las restricciones declaradas por las entidades y la base. | Configurada; pruebas usan H2. |

Los servicios delimitan sus operaciones con `@Transactional`; las consultas
usan transacciones de solo lectura y las escrituras tienen transacción de
lectura/escritura.

## 3. Componentes y UML

Los diagramas de componentes, secuencia y despliegue se encuentran junto a
este documento. El diagrama de componentes vigente incluye el observer de
eventos de dominio:

- `diagrama-componentes.svg`
- `diagrama-secuencia-hu01.png`
- `diagrama-secuencia.png`
- `diagrama-despliegue.svg`

El diagrama `diagrama-secuencia-hu01.png` documenta el registro. El diagrama
`diagrama-secuencia.png` documenta el login y la publicación autenticada del
lote (HU-02 y soporte JWT de HU-11). El flujo de registro HU-01 se describe
también en la
[documentación integral](../documentacion-integral-agrovalle.md#46-flujo-de-registro-de-agricultor).

## 4. Patrones GoF y Enterprise

| Patrón | Aplicación | Precisión sobre el estado actual |
|---|---|---|
| Repository | `AgricultorRepository` y `LoteCosechaRepository` extienden `JpaRepository`. | Implementado; Spring Data crea las implementaciones concretas. |
| Inyección de dependencias / IoC | Spring administra controladores, servicios y configuración. | Implementada mediante inyección por constructor. |
| Singleton (scope de Spring) | Los beans de Spring usan scope singleton por defecto. | El contenedor administra una instancia compartida por contexto; no se implementa manualmente el patrón GoF Singleton. |
| DTO | Records como `AgricultorRegistroRequest`, `LoginRequest`, `TokenResponse` y `LoteCosechaResponse`. | Implementado para delimitar contratos JSON. `AgricultorResponse` no devuelve contraseña, pero actualmente contiene cédula y correo. |
| Método fábrica estático | `AgricultorResponse.desde(...)` y `LoteCosechaResponse.desde(...)` construyen respuestas desde entidades. | Se usa un método fábrica estático como técnica de creación. No se considera el patrón GoF Factory Method: no hay creador abstracto ni jerarquía de fábricas. |
| Data Mapper | Conversión de entidad a DTO mediante `desde(...)`. | La conversión reside en el record de respuesta; no existe un Data Mapper independiente. |
| Builder | No existe un builder propio para entidades o DTO del dominio. | No está implementado ni se requiere para la construcción actual: los DTO son records y los objetos tienen pocos datos. No se incorpora solo para afirmar uso de un patrón. |
| Observer (GoF) / eventos de dominio | `LoteCosechaService` publica `LoteCosechaPublicadoEvent` mediante `ApplicationEventPublisher`; `LoteCosechaPublicadoListener` observa el evento después del commit y registra la publicación. | Implementado en producción. El servicio no conoce sus observadores y no notifica un lote cuya transacción no se confirmó. |

El proceso de implementación y su flujo están detallados en
[Implementación del patrón Observer](implementacion-observer.md).

Ejemplo de Repository:

```java
public interface LoteCosechaRepository
        extends JpaRepository<LoteCosecha, Long> {
    List<LoteCosecha> findByActivoTrue();
    List<LoteCosecha> findByAgricultorIdAndActivoTrue(Long agricultorId);
}
```

Ejemplo de método fábrica del DTO:

```java
public record LoteCosechaResponse(
        Long id,
        String tipoProducto,
        Double cantidadKg,
        Double precioUnitario,
        LocalDate fechaCosecha,
        Boolean activo,
        Long agricultorId) {

    public static LoteCosechaResponse desde(LoteCosecha lote) {
        return new LoteCosechaResponse(
                lote.getId(),
                lote.getTipoProducto(),
                lote.getCantidadKg(),
                lote.getPrecioUnitario(),
                lote.getFechaCosecha(),
                lote.getActivo(),
                lote.getAgricultor().getId());
    }
}
```

## 5. Evidencia de principios SOLID

| Principio | Aplicación verificable |
|---|---|
| Responsabilidad única (SRP) | Los controladores traducen HTTP, los servicios aplican reglas, los repositorios acceden a datos y los DTO forman el contrato JSON. El listener atiende el evento sin incorporar esa tarea al servicio. |
| Abierto/cerrado (OCP) | Se pueden agregar observadores de `LoteCosechaPublicadoEvent` sin cambiar `LoteCosechaService`; Spring administra la suscripción. |
| Sustitución de Liskov (LSP) | Los servicios dependen de contratos de repositorio; las implementaciones de Spring Data y los dobles Mockito se sustituyen respetando esos contratos. |
| Segregación de interfaces (ISP) | Los componentes reciben dependencias orientadas a la capacidad que usan (`AgricultorRepository`, `LoteCosechaRepository`, `PasswordEncoder` y `ApplicationEventPublisher`), no un servicio global. |
| Inversión de dependencias (DIP) | Los servicios se construyen por constructor con interfaces de repositorio y APIs de Spring; no instancian persistencia ni observadores concretos. |

Las pruebas unitarias aisladas de `AgricultorService` y
`LoteCosechaService` usan `@Mock`, `@InjectMocks` y el patrón
Arrange-Act-Assert (AAA), sin levantar la aplicación.

## 6. Seguridad y configuración transversal

- `SeguridadConfig` define rutas públicas de login y registro, publicación de
  productos autenticada y acceso sin sesión de servidor.
- `AuthService` verifica la contraseña con BCrypt y emite un token JWT HS256 con
  una hora de duración.
- `JWT_SECRET` se inyecta desde configuración; debe tener al menos 32 bytes.
- La validación de tokens comprueba firma, expiración y emisor.
- `GlobalExceptionHandler` transforma errores de validación, JSON y persistencia
  a respuestas HTTP.
- No hay autorización por roles, ni se configura TLS o CORS en el repositorio.

Para detalles del modelo persistido, API, despliegue y trazabilidad del backlog,
consúltese la [documentación integral](../documentacion-integral-agrovalle.md).
