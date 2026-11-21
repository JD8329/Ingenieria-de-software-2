# Segundo Corte — evidencias y estado de entrega

Este documento consolida enlaces verificables para el Segundo Corte. Distingue
los artefactos del repositorio de la evidencia de reuniones y configuraciones
externas que el equipo todavía debe confirmar. No reemplaza el PDF
institucional solicitado ni la retrospectiva del equipo.

## Enlaces públicos y verificación

- **Repositorio:** [JD8329/Ingenieria-de-software-2](https://github.com/JD8329/Ingenieria-de-software-2)
- **GitHub Project:** [Agrovalle-connet](https://github.com/users/JD8329/projects/1)
- **Actions:** [CI exitosa de `main`, commit `09885244d334d93ddd1eeb190799ac78766b208a`](https://github.com/JD8329/Ingenieria-de-software-2/actions/runs/40323762565)
- **Verificación local del mismo commit:** `.\mvnw.cmd --batch-mode --no-transfer-progress clean verify`, ejecutada el 28 de octubre de 2026: 41 pruebas aprobadas, sin fallos ni errores; Checkstyle sin violaciones; JaCoCo 95,4% de instrucciones, 80,6% de ramas y 95,8% de líneas. El reporte HTML local se genera en `target/site/jacoco/index.html`.

## Matriz de evidencias

| Criterio | Evidencia verificable | Estado / alcance |
|---|---|---|
| IEEE 29148 y reformulación normativa de HU-01 | [`ieee29148-req01.md`](ieee29148-req01.md) | Documentado con comparación frente a BDD y trazabilidad de pruebas. |
| Modelo ER, DER normalizado en 3FN y esquema PostgreSQL | [`diseno/modelo-datos-der.md`](diseno/modelo-datos-der.md), [`diseno/diagrama-er-chen.md`](diseno/diagrama-er-chen.md), [`diseno/modelo-datos-der.png`](diseno/modelo-datos-der.png), [`diseno/schema.sql`](diseno/schema.sql) | Modelo de referencia de cinco tablas; el documento identifica explícitamente que no equivale al esquema JPA actual. El DDL no se afirma como desplegado. |
| MVC de cuatro capas, UML de despliegue, componentes y secuencias | [`diseno/arquitectura-mvc-gof.md`](diseno/arquitectura-mvc-gof.md), [`diseno/diagrama-despliegue.svg`](diseno/diagrama-despliegue.svg), [`diseno/diagrama-componentes.svg`](diseno/diagrama-componentes.svg), [secuencia HU-02 con activaciones](diseno/diagrama-secuencia-hu02-publicacion.md) | Artefactos de diseño documentados; el diagrama Mermaid de publicación es fuente renderizable/editable y no una captura exportada. |
| Mockups de interfaz | [`diseno/mockups-ui.md`](diseno/mockups-ui.md) y [`figuras/prototipado/`](figuras/prototipado/) | Prototipos documentados; no se presentan como una interfaz web implementada. |
| SOLID y patrón GoF conductual | [`diseno/arquitectura-mvc-gof.md`](diseno/arquitectura-mvc-gof.md), [`diseno/implementacion-observer.md`](diseno/implementacion-observer.md) | Los cinco principios se relacionan con código; Observer se publica mediante `ApplicationEventPublisher` y el listener actúa después del commit. |
| Pruebas, TDD, CI y cobertura | [Workflow CI](../.github/workflows/ci.yml), [evidencia TDD de sesión 8](evidencias/tdd-sesion-8.md), [PR #23](https://github.com/JD8329/Ingenieria-de-software-2/pull/23) y [CI de PR #23](https://github.com/JD8329/Ingenieria-de-software-2/actions/runs/39514340093) | RED reproducido ahora contra el servicio anterior en worktree aislado; GREEN verificado en unit e integración. No existe registro histórico de commits por fase; JaCoCo es evidencia separada. |
| Tablero y límites WIP | [GitHub Project](https://github.com/users/JD8329/projects/1), [`scrumban.md`](scrumban.md) | Las opciones de estado incluyen `In progress (wip:3)` y `Code Review / Testing (wip:2)`; la revisión no acredita aplicación automática de los límites. |
| Bitácora Daily Scrum | [`bitacora-daily-scrum.md`](bitacora-daily-scrum.md) | Artefacto existente; el equipo debe validar que entradas y fechas correspondan a reuniones realmente realizadas. |
| Sprint Review y retrospectiva | [`sprint-2-review.md`](sprint-2-review.md), [`sprint-2-retrospective.md`](sprint-2-retrospective.md) | La revisión técnica se documenta; los datos de la reunión real están pendientes. La retrospectiva se difiere y no se edita en esta tarea. |
| Protección de `main` | [`scrumban.md`](scrumban.md) | Pendiente: la API respondió 404 y el equipo no tiene permisos de administrador. No se afirma que la protección esté activa. |

## Resumen técnico para el PDF

El backend REST de AgroValle Connect amplió el catálogo con dos capacidades del
Sprint 2. HU-03 permite que un agricultor autenticado registre reportes de
precios y que se consulte públicamente el promedio de un producto dentro de
una ventana de 24 horas, siempre que existan al menos 50 transacciones. HU-04
permite filtrar lotes activos por municipio, categoría o ambos, manteniendo
compatibilidad con lotes sin categoría. Las historias tienen pruebas MockMvc
para sus criterios y validaciones.

La arquitectura conserva las cuatro capas REST, servicio, repositorio y
persistencia. La publicación de lotes usa el patrón Observer con eventos de
Spring: el listener registra el evento después de confirmar la transacción. La
configuración CI ejecuta Maven, Checkstyle y JaCoCo; la verificación local del
commit actual aprobó 41 pruebas y superó los umbrales de 60% de instrucciones
y ramas. El CI remoto de `main` también terminó con éxito para ese commit.

Los detalles de la revisión del equipo, sus asistentes y aceptación docente
deben agregarse solo con evidencia de la reunión real. La rama protegida, las
capturas institucionales y los datos administrativos de portada siguen
pendientes; este resumen técnico no afirma que sustituyan esos requisitos.

## PDF institucional pendiente

La guía solicita un único archivo llamado
`GrupoXX_Entrega_Segundo_Corte_AgroValle.pdf`, con portada institucional,
número de grupo, nombres completos, números de documento, enlaces públicos,
captura actual de Actions, evidencia de JaCoCo y resumen de Sprint 2 de máximo
dos páginas. El repositorio no contiene el número de grupo ni los números de
documento del equipo; no se inventan ni se publican números de documento para
rellenar esos campos. Los nombres disponibles en el README son:

- Juan David Gonzalez — documento: completar en el PDF institucional.
- Steven Andres Guacheta — documento: completar en el PDF institucional.
- Andres Felipe Lopez — documento: completar en el PDF institucional.
- Nestor Javier Hernandez — documento: completar en el PDF institucional.

Por ello, el PDF final no se declara listo. Antes de exportarlo, el equipo debe:

1. Completar el número de grupo y los datos de portada usando el formato
   institucional, sin subir números de documento al repositorio público.
2. Adjuntar capturas reales de la ejecución exitosa de Actions y del reporte
   JaCoCo generado para el commit que se entregue.
3. Redactar y validar el resumen del Sprint 2 en no más de dos páginas y
   confirmar los datos de la revisión real.
4. Verificar con una persona administradora la protección de `main` según
   [`scrumban.md`](scrumban.md).
