# Artefactos de diseño — AgroValle Connect

Este directorio organiza los artefactos solicitados para el Sprint de Diseño.
La [documentación integral](../documentacion-integral-agrovalle.md) reúne la
explicación completa de normalización, prototipos y arquitectura. Esta carpeta
contiene los archivos de consulta directa y diagramas.

## Modelado de datos

- [Modelo relacional normalizado y correspondencia JPA](modelo-datos-der.md)
- [Modelo ER canónico en notación Chen](diagrama-er-chen.md)
- [Diagrama DER en 3FN (PNG)](modelo-datos-der.png)
- [Diagrama ER conceptual de 17 tablas](../figuras/prototipado/modelo-er-conceptual.png)
- [DDL PostgreSQL ejecutable](schema.sql)
- [Contexto y decisiones del DDL](../documentacion-integral-agrovalle.md#28-script-postgresql-propuesto)

El modelo de cinco tablas del documento de normalización es el modelo relacional
de referencia en 3FN y coincide con su DDL PostgreSQL. El diagrama de 17 tablas
del prototipo representa una visión conceptual ampliada. El backend Sprint 1
persiste dos entidades y no se presenta como implementación de los dos modelos.

## Arquitectura MVC y patrones

- [Arquitectura MVC de cuatro capas y patrones](arquitectura-mvc-gof.md)
- [Implementación del patrón Observer](implementacion-observer.md)
- [Diagrama de componentes actualizado](diagrama-componentes.svg)
- [Diagrama de secuencia HU-01: registro de agricultor](diagrama-secuencia-hu01.png)
- [Diagrama de secuencia HU-02/HU-11: login y publicación](diagrama-secuencia.png)
- [Diagrama de secuencia HU-02: publicación con activaciones y duplicados](diagrama-secuencia-hu02-publicacion.md)
- [Diagrama de despliegue con puertos](diagrama-despliegue.svg)

## Estado frente a la guía

| Entregable | Estado documental | Alcance o evidencia pendiente |
|---|---|---|
| Modelo relacional y DER en 3FN | Modelo de referencia de cinco tablas documentado en [`modelo-datos-der.md`](modelo-datos-der.md), con [`schema.sql`](schema.sql), DER relacional y diagrama Chen coherentes. | El mapeo a JPA identifica las entidades ejecutables y las diferencias con el esquema relacional; no afirma que las cinco tablas estén desplegadas. |
| Arquitectura de cuatro capas, SOLID y GoF | [`arquitectura-mvc-gof.md`](arquitectura-mvc-gof.md) documenta responsabilidades y SOLID; [`implementacion-observer.md`](implementacion-observer.md) detalla el patrón Observer. | El evento se registra después del commit; otros observadores pueden suscribirse sin modificar el servicio. |
| Diagramas y wireframes | UML exportado a PNG y cinco mockups en [`mockups-ui.md`](mockups-ui.md). | La matriz de mockups identifica HU, API/DTO y si el elemento está implementado o es solo prototipo. |
| IEEE 29148 frente a BDD | [`ieee29148-req01.md`](../ieee29148-req01.md) formaliza HU-01 y compara ambas técnicas. | Los casos de validación y duplicidad de HU-01 tienen pruebas de integración automatizadas. |
| Pruebas, CI y Scrumban | Suite JUnit 5 con pruebas MockMvc y Mockito; CI en `.github/workflows/ci.yml`; tablero público y límites WIP en [`scrumban.md`](../scrumban.md). | La protección de ramas requiere un administrador; las capturas de Actions/cobertura y los datos de reuniones deben incluirse o confirmarse para el PDF. |
| Cierre de sprint | [`sprint-1-review.md`](../sprint-1-review.md), [`sprint-2-review.md`](../sprint-2-review.md), [`sprint-2-retrospective.md`](../sprint-2-retrospective.md) y [`dod.md`](../dod.md). | La revisión técnica enlaza CI; los datos de reuniones y la retrospectiva se completan con el equipo y evidencia real. |

## Prototipos de interfaz

- [Flujo de pantallas](../figuras/prototipado/flujo-pantallas.png)
- [Wireframes de las cinco pantallas](mockups-ui.md)

## Requisitos y ejecución del sprint

- [Requisito HU-01: IEEE 29148 y comparación con BDD](../ieee29148-req01.md)
- [Planificación del Sprint 1](../sprint-1-planning.md)
- [Revisión y evidencia técnica](../sprint-1-review.md)
- [Revisión del Sprint 2](../sprint-2-review.md)
- [Retrospectiva del Sprint 2](../sprint-2-retrospective.md)
- [Definition of Done](../dod.md)

## Observación sobre alcance

La planificación documenta HU-01 y HU-02 para el Sprint 1; durante el Sprint 2
se implementaron también HU-03 y HU-04, documentadas en
[`sprint-2-hu03.md`](../sprint-2-hu03.md) y
[`sprint-2-hu04.md`](../sprint-2-hu04.md). No se declara implementada HU-07.
