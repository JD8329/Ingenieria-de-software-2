# Revisión del Sprint 2 — AgroValle Connect

## Estado de este documento

Este registro es una revisión técnica de los artefactos del repositorio frente
a la guía del Segundo Corte. No es un acta de una Sprint Review del equipo y no
afirma presentación, aceptación de la docente/Product Owner ni acuerdos que no
estén documentados. La retrospectiva del equipo se mantiene en
[`sprint-2-retrospective.md`](sprint-2-retrospective.md) para completarla con el
equipo.

| Campo de la reunión | Estado |
|---|---|
| Fecha, hora y duración | Pendiente de registrar por el equipo |
| Asistentes y presentador | Pendiente de confirmar |
| Historias presentadas y aceptadas | Pendiente de registrar |
| Comentarios de la docente/Product Owner | No registrados |
| Evidencia de demostración | Pendiente de adjuntar |

## Verificación técnica del repositorio

El workflow de GitHub Actions está definido en
[`.github/workflows/ci.yml`](../.github/workflows/ci.yml). El 28 de octubre de
2026 se ejecutó `.\mvnw.cmd --batch-mode --no-transfer-progress clean verify`
localmente sobre `09885244d334d93ddd1eeb190799ac78766b208a`, el mismo commit de
`main` para el que se registra la
[ejecución exitosa más reciente de CI](https://github.com/JD8329/Ingenieria-de-software-2/actions/runs/40323762565).

| Verificación | Resultado |
|---|---|
| Compilación y pruebas JUnit 5 | `BUILD SUCCESS`; 41 pruebas aprobadas, 0 fallos y 0 errores |
| Pruebas REST con MockMvc | Integración para los flujos de registro, autenticación, publicación, HU-03 y HU-04 |
| Pruebas unitarias aisladas con Mockito | 10 pruebas en `AgricultorServiceTest` y `LoteCosechaServiceTest` |
| Evidencia TDD para publicación duplicada | [Pruebas, reproducción RED aislada y GREEN verificado](evidencias/tdd-sesion-8.md); [PR #23](https://github.com/JD8329/Ingenieria-de-software-2/pull/23) y [su CI exitosa](https://github.com/JD8329/Ingenieria-de-software-2/actions/runs/39514340093) |
| Checkstyle | 0 violaciones |
| JaCoCo — instrucciones | 95,4% |
| JaCoCo — ramas | 80,6% |
| JaCoCo — líneas | 95,8% |
| Reporte JaCoCo local | `target/site/jacoco/index.html`, generado por `clean verify` |
| GitHub Actions en `main` | Éxito para el commit `09885244d334d93ddd1eeb190799ac78766b208a`; [run 40323762565](https://github.com/JD8329/Ingenieria-de-software-2/actions/runs/40323762565) |
| Protección de `main` | No acreditada: GitHub respondió 404 al consultar la regla. Requiere configuración y confirmación de un administrador; ver [`scrumban.md`](scrumban.md) |
| Tablero Scrumban | [GitHub Project público](https://github.com/users/JD8329/projects/1); sus opciones incluyen `In progress (wip:3)` y `Code Review / Testing (wip:2)` |

El CI remoto prueba que el workflow terminó correctamente para `main`; las
cifras JaCoCo de esta tabla provienen del reporte local sobre ese mismo commit.
La salida local no sustituye las capturas requeridas en el PDF institucional.

## Alcance y pendientes antes de presentar

- HU-03 y HU-04 tienen documentación y pruebas de integración; los criterios,
  artefactos y estado del código se relacionan en
  [`entrega-segundo-corte.md`](entrega-segundo-corte.md).
- El tablero público contiene 22 elementos; HU-03 y HU-04 aparecen como
  `Done`. Los límites aparecen en los nombres de las opciones de estado; no se
  presenta como evidencia de un bloqueo automático al excederlos.
- La evidencia TDD de lotes duplicados incluye una reproducción RED hecha
  ahora sobre el servicio previo y pruebas GREEN actuales. PR #23 se integró en
  un solo commit, por lo que no se afirma que exista evidencia histórica de las
  fases separadas.
- La fecha, participantes, historias aceptadas, comentarios de la docente y
  evidencia de una reunión real siguen pendientes de confirmar por el equipo.
- La protección de `main` requiere que un administrador aplique las opciones
  descritas en [`scrumban.md`](scrumban.md).
- La retrospectiva se difiere explícitamente; no se debe inferir ni completar
  información de la reunión a partir de este registro técnico.
