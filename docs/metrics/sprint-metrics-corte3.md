# Métricas ágiles — Sprints 1 a 3

**Estado:** informe parcial basado en evidencia del repositorio y GitHub
disponible al 22/11/2026. No reemplaza actas, aceptación de la docente ni
capturas históricas del tablero.

## 1. Velocidad y puntos planificados frente a trabajo integrado

La velocidad se calcula como la suma de Story Points de historias aceptadas
que cumplen el DoD durante un sprint. Que una historia esté implementada o que
un PR se haya fusionado no basta por sí solo para afirmar que sus puntos fueron
completados.

| Sprint | Historias | SP planificados / estimados | Evidencia de integración | Estado para calcular velocidad |
|---|---|---:|---|---|
| 1 | HU-01 (5), HU-02 (5) | 10 | PR #20 y PR #21 fusionados; el resumen de checks consultado informa éxito. | No certificable como velocidad: la revisión del Sprint 1 no registra aprobación formal de pares ni aceptación de la docente/Product Owner. |
| 2 | HU-03 (3), HU-04 (5) | 8 | PR #24 y PR #25 fusionados; el resumen de checks consultado informa al menos un fallo en cada PR. | No certificable como velocidad con la evidencia actual: verificar los checks y completar la aprobación/aceptación faltante. |
| 3 | HU-05 (3), HU-06 (8) | 11 estimados en el backlog; no hay planificación de Sprint 3 que confirme compromiso | PR #35, que agrupa HU-05 a HU-10, fusionado con aprobación y checks exitosos. | No certificable como Done: la API todavía recibe `comercianteId` desde la solicitud en lugar de derivarlo de la identidad autenticada, limitación que afecta los criterios de autorización de HU-05/HU-06. |

Los estados de PR son evidencia de integración técnica, no de aceptación
académica. Por las salvedades de la tabla, este repositorio no permite informar
una velocidad real ni un promedio de velocidad para estos tres sprints. No se
reemplazan los valores faltantes por cero.

### Diferencias frente a las cifras de la Sesión 14

- La planificación versionada registra 10 SP para Sprint 1 y 8 SP para Sprint
  2; no 15 SP para Sprint 2.
- Los 11 SP de Sprint 3 son la suma de las estimaciones de HU-05/HU-06 en el
  backlog. No hay un documento de planificación que confirme 20 SP
  comprometidos.
- Por tanto, el promedio de 15 SP por sprint de las diapositivas no se puede
  reproducir con los documentos del repositorio ni se presenta como métrica del
  equipo.

## 2. Burndown

No hay un registro diario de puntos pendientes por sprint. La bitácora contiene
reuniones semanales y reconstrucciones que no sirven para reconstruir el
burndown. No se fabrica una línea real a partir de fechas de commits o de un
estado actual del tablero.

Para registrar el burndown en adelante, guardar al cierre de cada día de trabajo
los Story Points restantes del alcance aprobado y la fuente del dato:

| Sprint | Fecha / día | SP restantes observados | SP restantes ideales | Bloqueos / cambio de alcance | Enlace a captura o registro |
|---|---|---:|---:|---|---|
| Por completar | Por completar | Por completar | Por completar | Por completar | Por completar |

La línea ideal debe calcularse una vez acordadas la duración y la fecha inicial
del sprint; no debe deducirse después si esas fechas no se registraron.

## 3. Diagrama de flujo acumulado (CFD)

El repositorio documenta estados de Scrumban, pero no conserva conteos diarios
históricos por estado. Una captura actual del tablero tampoco permite inferir
el ancho histórico de las franjas ni identificar retrospectivamente un cuello
de botella.

Registrar un snapshot diario usando las columnas reales del tablero, por
ejemplo:

| Fecha | Sprint backlog / To do | In progress | Code Review / Testing | Done | Enlace a captura o registro |
|---|---:|---:|---:|---:|---|
| Por completar | Por completar | Por completar | Por completar | Por completar | Por completar |

Interpretación futura: un aumento sostenido de elementos en una columna frente
a las columnas siguientes puede señalar una acumulación; debe contrastarse con
el WIP y el contexto del equipo antes de atribuir una causa.

## 4. Lead Time y Cycle Time

Las medias de 4,5 y 1,8 días de las diapositivas no tienen en el repositorio un
conjunto de historias, marcas de tiempo o exportación que permita recalcularlas.
El proyecto tampoco tiene un despliegue de staging configurado; por ello, el
Lead Time hasta staging definido en la guía no puede medirse todavía.

| Métrica | Inicio | Fin | Situación actual |
|---|---|---|---|
| Lead Time | Creación de la historia en el backlog | Despliegue verificable en staging | No calculable: faltan marcas de tiempo consistentes y el primer despliegue aún no se ha verificado. |
| Cycle Time | Entrada de la historia a In Progress | Revisión aprobada y CI exitoso | No calculable como promedio del equipo: no hay exportación histórica de transiciones/timestamps asociada a todas las historias. |

Para obtener promedios, conservar por historia el ID, fecha/hora de inicio y
fin, sprint, enlace al issue/PR y evidencia del despliegue cuando aplique.
Reportar mediana y tamaño de muestra junto al promedio para evitar que pocos
casos oculten la variabilidad.

## 5. Indicadores de calidad y CI

| Indicador | Evidencia del repositorio | Interpretación |
|---|---|---|
| JaCoCo | El workflow ejecuta `jacoco:check@check-coverage`; el DoD exige al menos 60 % en instrucciones, ramas y líneas. La revisión técnica del Segundo Corte registra 95,4 % de instrucciones, 80,6 % de ramas y 95,8 % de líneas para un commit histórico específico. | Son umbrales y una captura histórica, no la cobertura final del commit actual. Ejecutar el workflow sobre el commit a entregar y guardar el reporte. |
| Checkstyle | `.github/workflows/ci.yml` ejecuta `checkstyle:check`. | El repositorio no consolida una tasa histórica de errores por sprint; informar el resultado del run concreto. |
| CI | El workflow contiene un job `build-and-test` con Checkstyle, JUnit y JaCoCo. La consulta de checks de PR #20, #21 y #35 informó éxito; para #24 y #25 informó fallos. | No hay un denominador de todas las ejecuciones en este documento, así que no se afirma una tasa de éxito del 100 %. Revisar los runs antes de presentar. |
| CD / staging | `.github/workflows/ci.yml` incluye `deploy-staging`, condicionado a CI exitoso en `main` y a `STAGING_DEPLOY_ENABLED=true`; el job usa `RENDER_DEPLOY_HOOK_URL`. | La configuración externa de Render, su hook y el primer deploy aún no están verificados. `STAGING_DB_URL` no se inyecta en el runtime. |

## 6. Evidencia pendiente para cerrar el informe

- Confirmar con el equipo qué historias cumplieron todos los criterios del DoD y
  cuáles fueron aceptadas en cada sprint.
- Revisar los runs fallidos de PR #24 y #25 y adjuntar la evidencia pertinente.
- Obtener de GitHub Projects las transiciones o snapshots diarios de trabajo
  para construir burndown y CFD; no reconstruir fechas faltantes.
- Medir Lead Time después del primer deploy verificable y Cycle Time con un historial
  consistente de movimientos, revisión y CI.
- Ejecutar JaCoCo sobre el commit final y adjuntar la captura correspondiente.

## Fuentes

- [Planificación del Sprint 1](../sprint-1-planning.md)
- [Planificación del Sprint 2](../sprint-2-planning.md)
- [Especificación de HU-05 y HU-06](../sprint-3-historias-5-y-6.md)
- [Product Backlog](../../BACKLOG.md)
- [DoD](../dod.md)
- [Revisión técnica del Sprint 2](../sprint-2-review.md)
- [Scrumban y CI](../scrumban.md)
- [Guía de staging](../staging-preproduccion.md)
