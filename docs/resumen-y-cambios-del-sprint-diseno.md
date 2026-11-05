# Resumen y cambios del Sprint de Diseño

## Propósito y alcance

Este documento consolida los entregables y decisiones de diseño de AgroValle
Connect que están documentados en el repositorio. Se limita a requisitos,
modelos de datos, prototipos de interfaz y arquitectura. No registra avances de
código, pruebas, despliegues, reuniones ni compromisos que no estén respaldados
por la documentación existente.

Los modelos y prototipos descritos son propuestas de diseño. Su documentación
no significa que todas sus entidades o pantallas estén implementadas.

## Resumen del diseño

El diseño aborda tres perspectivas del sistema:

1. **Datos:** normalización del dominio agrícola y comercial y definición de
   modelos relacionales de referencia.
2. **Interacción:** estructura y navegación de baja fidelidad para el módulo del
   agricultor.
3. **Arquitectura:** separación de responsabilidades para la API REST, sus
   componentes y flujos principales.

Además, HU-01 cuenta con una especificación formal orientada a requisitos
verificables y trazables.

## Cambios y entregables de diseño registrados

### Modelo de datos

- Se documentó la normalización hasta tercera forma normal (3FN) y un modelo
  relacional de referencia con cinco entidades: `Municipio`, `Agricultor`,
  `Producto`, `Cosecha` y `Oferta`.
- Se definieron sus llaves, relaciones, cardinalidades y reglas de integridad.
  El diagrama relacional, el diagrama ER en notación Chen y el DDL propuesto
  representan ese modelo de referencia.
- Se mantiene separado un modelo ER conceptual ampliado de 17 tablas y dos
  vistas, que representa una visión de producto más amplia y no el mismo
  esquema que el modelo normalizado de cinco entidades.
- Se documentaron las diferencias entre los modelos propuestos y el modelo de
  persistencia actual, sin presentar los diseños conceptuales como tablas ya
  desplegadas.

### Prototipos de interfaz

- Se definieron cinco wireframes de baja fidelidad para el módulo del
  agricultor: **Login**, **Registro de agricultor**, **Panel principal**,
  **Registrar lote** y **Catálogo de ofertas**.
- Se documentaron los campos, controles, validaciones esperadas, mensajes y
  acciones de cada pantalla, además de su relación con el modelo conceptual.
- Se estableció el flujo entre pantallas: autenticación y registro, acceso
  desde el panel a la publicación de lotes y al catálogo, y retorno al panel o
  al login según la acción.
- El estilo se mantiene intencionalmente en baja fidelidad: escala de grises,
  estructura común, etiquetas sobre los campos, jerarquía entre acciones
  principales y secundarias y áreas diferenciadas para mensajes. No define una
  identidad visual final.
- El alcance visual documentado no incluye pantallas del comerciante, pedidos,
  despachos, calificaciones, mensajería, notificaciones, recuperación de
  contraseña ni detalle de lote. Esos elementos quedan para diseño posterior.

### Arquitectura y diagramas

- Se documentó una arquitectura para la API REST organizada en cuatro capas:
  presentación y API, servicio y dominio, acceso a datos y persistencia.
- Se describieron las responsabilidades y relaciones entre controladores,
  DTO, servicios, modelos, repositorios y base de datos, además de las
  preocupaciones transversales de seguridad, validación y manejo de errores.
- Se registraron diagramas de componentes, secuencia y despliegue para
  representar la estructura del sistema y los flujos principales.
- Se documentó el patrón Observer para desacoplar la publicación de lotes de
  las acciones que reaccionan a ella. El alcance del diseño contempla eventos
  y observadores; no presupone notificaciones visibles ni integraciones
  externas.
- Se relacionaron los principios SOLID y los patrones Repository, DTO,
  inyección de dependencias y Observer con los componentes descritos en la
  documentación de arquitectura.

### Especificación de requisitos

- HU-01 se formalizó como el requisito `REQ-HU01-REG-001`, con propósito,
  actores, interfaz, datos, requisitos funcionales y no funcionales,
  precondiciones, postcondiciones y escenarios BDD.
- Se incorporó trazabilidad entre requisitos y formas de verificación, y se
  dejaron explícitas las decisiones de negocio que requieren confirmación.
- La especificación toma como referencia ISO/IEC/IEEE 29148:2018; no declara
  certificación ni conformidad completa con la norma.

## Decisiones y pendientes de diseño

- La revisión y aprobación de los cinco wireframes por parte del Product Owner
  siguen pendientes de registrar; no se atribuyen observaciones ni una fecha de
  validación.
- Debe acordarse si el modelo de referencia de cinco entidades es una etapa
  intermedia o si el modelo conceptual ampliado de 17 tablas será el objetivo
  futuro.
- Debe definirse cómo alinear la separación conceptual entre cosecha y oferta
  con el prototipo que agrupa su registro y publicación en un mismo formulario.
- Permanecen por confirmar reglas de negocio relacionadas con datos del
  agricultor, productos y categorías, fechas de cosecha, cantidades, stock,
  precios y privacidad.
- Las pantallas y flujos fuera del módulo del agricultor requieren diseño y
  validación en iteraciones posteriores.

## Artefactos de referencia

- [Índice de artefactos de diseño](diseno/README.md)
- [Documentación integral de AgroValle Connect](documentacion-integral-agrovalle.md)
- [Modelo relacional de referencia y correspondencia con JPA](diseno/modelo-datos-der.md)
- [Diagrama ER canónico en notación Chen](diseno/diagrama-er-chen.md)
- [Wireframes de baja fidelidad](diseno/mockups-ui.md)
- [Arquitectura MVC en cuatro capas y patrones](diseno/arquitectura-mvc-gof.md)
- [Implementación documentada del patrón Observer](diseno/implementacion-observer.md)
- [Especificación HU-01 — Registro de agricultores](ieee29148-req01.md)
