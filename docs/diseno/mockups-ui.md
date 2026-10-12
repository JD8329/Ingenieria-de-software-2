# Wireframes de baja fidelidad — módulo del agricultor

Los mockups son propuestas de interacción, no pantallas implementadas. Se
describen los campos y criterios de cada pantalla en la
[documentación integral](../documentacion-integral-agrovalle.md#3-prototipado-del-módulo-del-agricultor).

## Trazabilidad con historias, API y DTO

| Pantalla / flujo | Historia | API o DTO relacionado | Correspondencia con el backend |
|---|---|---|---|
| Login | HU-11, soporte para HU-02 | `POST /api/v1/auth/login`; `LoginRequest`, `TokenResponse` | Implementado. |
| Registro de agricultor | HU-01 | `POST /api/v1/agricultores/registro`; `AgricultorRegistroRequest`, `AgricultorResponse` | Implementado. El backend exige cédula de 6–10 dígitos y municipio del enum; la respuesta incluye cédula y correo, pero no contraseña. |
| Panel principal | HU-02 | `GET /api/v1/productos`; `LoteCosechaResponse` | Consulta implementada para lotes activos; el panel visual sigue siendo un prototipo. |
| Registrar lote | HU-02 | `POST /api/v1/productos`; DTO de solicitud de lote y `LoteCosechaResponse` | Publicación implementada con JWT; el mockup no constituye una interfaz web desplegada. |
| Catálogo de ofertas | Evolución del producto | `GET /api/v1/productos`; `LoteCosechaResponse` | Existe consulta de lotes activos, no filtros por categoría ni una interfaz de catálogo implementada. |

## Flujo de navegación

![Flujo de navegación de las pantallas del agricultor](../figuras/prototipado/flujo-pantallas.png)

## Login

![Wireframe de Login](../figuras/prototipado/login.png)

## Registro de agricultor

![Wireframe de registro de agricultor](../figuras/prototipado/registro-agricultor.png)

## Panel principal

![Wireframe del panel principal](../figuras/prototipado/panel-principal.png)

## Registrar lote

![Wireframe para registrar un lote](../figuras/prototipado/registrar-lote.png)

## Catálogo de ofertas

![Wireframe del catálogo de ofertas](../figuras/prototipado/catalogo-ofertas.png)
