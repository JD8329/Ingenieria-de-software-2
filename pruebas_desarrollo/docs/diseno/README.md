# Artefactos de diseño — AgroValle Connect

Este directorio organiza los artefactos solicitados para el Sprint de Diseño.
La [documentación integral](../documentacion-integral-agrovalle.md) reúne la
explicación completa de normalización, prototipos y arquitectura. Esta carpeta
contiene los archivos de consulta directa y diagramas.

## Modelado de datos

- [Modelo relacional normalizado y correspondencia JPA](modelo-datos-der.md)
- [Diagrama DER en 3FN (PNG)](modelo-datos-der.png)
- [Diagrama ER conceptual de 17 tablas](../figuras/prototipado/modelo-er-conceptual.png)
- [DDL PostgreSQL propuesto](../documentacion-integral-agrovalle.md#28-script-postgresql-propuesto)

El modelo de cinco tablas del documento de normalización es el modelo relacional
de referencia en 3FN y coincide con su DDL PostgreSQL. El diagrama de 17 tablas
del prototipo representa una visión conceptual ampliada. El backend Sprint 1
persiste dos entidades y no se presenta como implementación de los dos modelos.

## Arquitectura MVC y patrones

- [Arquitectura MVC de cuatro capas y patrones](arquitectura-mvc-gof.md)
- [Diagrama de componentes](diagrama-componentes.png)
- [Diagrama de secuencia HU-01: registro de agricultor](diagrama-secuencia-hu01.png)
- [Diagrama de secuencia HU-02/HU-11: login y publicación](diagrama-secuencia.png)
- [Diagrama de despliegue](diagrama-despliegue.png)

## Estado frente a la guía

| Entregable | Estado documental | Alcance o evidencia pendiente |
|---|---|---|
| Modelo relacional y DER en 3FN | Modelo de referencia de cinco tablas documentado en [`modelo-datos-der.md`](modelo-datos-der.md), con DER y DDL PostgreSQL coherentes. | El mapeo a JPA identifica las dos entidades ejecutables y las diferencias con el esquema relacional; no afirma que las cinco tablas estén desplegadas. |
| Arquitectura de cuatro capas y patrones | [`arquitectura-mvc-gof.md`](arquitectura-mvc-gof.md) documenta responsabilidades, patrones aplicados y no aplicados. | Builder se justifica como no utilizado: los DTO son records y los objetos tienen construcción simple; no se fuerza un patrón sin necesidad. |
| Diagramas y wireframes | UML exportado a PNG, cinco mockups y una interfaz web responsive para el módulo del agricultor en [`mockups-ui.md`](mockups-ui.md). | La matriz identifica HU y API/DTO; documenta qué funciones consumen el backend y cuáles se filtran en el navegador. |
| IEEE 29148 frente a BDD | [`ieee29148-req01.md`](../ieee29148-req01.md) formaliza HU-01 y compara ambas técnicas. | Los casos de validación y duplicidad de HU-01 tienen pruebas de integración automatizadas. |
| Cierre ejecutable Sprint 1 | [`sprint-1-review.md`](../sprint-1-review.md), [`sprint-1-planning.md`](../sprint-1-planning.md) y [`dod.md`](../dod.md). | El alcance ejecutable documentado es HU-01/HU-02. HU-04/HU-07 no se agregan ni se declaran implementadas en este cierre. |
| Iteración 2 — HU-03/HU-04 | [`sprint-2.md`](../sprint-2.md). | Implementación técnica de precios promedio y filtros; revisar sus límites de registro de ventas y la verificación final en el documento. |

## Prototipos de interfaz

- [Flujo de pantallas](../figuras/prototipado/flujo-pantallas.png)
- [Wireframes de las cinco pantallas](mockups-ui.md)
- Interfaz web implementada en `src/main/resources/static`; se sirve junto con Spring Boot en `http://localhost:8080`.
- [Guía sencilla para iniciar el front y el back](../INICIO-FACIL.md)

## Requisitos y ejecución del sprint

- [Requisito HU-01: IEEE 29148 y comparación con BDD](../ieee29148-req01.md)
- [Planificación del Sprint 1](../sprint-1-planning.md)
- [Revisión y evidencia técnica](../sprint-1-review.md)
- [Definition of Done](../dod.md)

## Observación sobre alcance

La planificación del repositorio documenta HU-01 y HU-02 como compromiso del
Sprint 1. La guía docente menciona HU-01, HU-07 y HU-04 para la culminación
ejecutable. Para este cierre se conserva el alcance ya planificado e
implementado, HU-01/HU-02; HU-04 y HU-07 no se agregan retroactivamente ni se
declaran cumplidas. La diferencia queda registrada para no atribuir historias
que no hicieron parte del trabajo ejecutado.
