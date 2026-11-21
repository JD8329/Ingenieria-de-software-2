# Scrumban y configuración de GitHub

## Límites de trabajo en curso

La guía del Segundo Corte establece estos límites para el tablero del equipo:

| Columna | WIP máximo |
|---|---:|
| In Progress | 3 |
| Code Review | 2 |

El tablero público del equipo está disponible en
[Agrovalle-connet](https://github.com/users/JD8329/projects/1). Sus opciones
visibles de estado incluyen `In progress (wip:3)` y
`Code Review / Testing (wip:2)`. El listado consultado contiene 22 elementos;
HU-03 y HU-04 aparecen como `Done`.

Los nombres acreditan los límites declarados en la configuración visible del
tablero; esta revisión no confirma que GitHub bloquee automáticamente el
movimiento de tarjetas cuando se exceden. El equipo debe respetar y vigilar los
conteos.

## Protección de `main`

El workflow [`.github/workflows/ci.yml`](../.github/workflows/ci.yml) se ejecuta
en Pull Requests y en los pushes a `main` y `develop`. El job compila, ejecuta
pruebas, Checkstyle y JaCoCo mediante `./mvnw clean verify`.

La protección de ramas es una configuración remota y no se puede activar
mediante un archivo del worktree. La consulta de la regla de protección de
`main` respondió `404 Not Found`; por ello, esta entrega no la presenta como
configurada. El equipo actual no cuenta con permisos de administrador. Una
persona administradora debe abrir **JD8329/Ingenieria-de-software-2 → Settings
→ Rules → Rulesets → New branch ruleset** (o **Branches → Add branch
protection rule**) y aplicar a `main` estos requisitos:

- Elegir `main` como patrón de rama objetivo y exigir Pull Requests antes de
  integrar, para impedir pushes directos fuera de los bypass autorizados.
- Exigir al menos una aprobación de otro integrante y descartar aprobaciones
  obsoletas al cambiar el commit del Pull Request.
- Exigir el status check **Tests, Checkstyle y cobertura** antes de integrar.
- Guardar la regla y confirmar que esté activa para `main`.

**Estado verificable:** el workflow existe y su ejecución en `main` está
documentada en [la revisión del Sprint 2](sprint-2-review.md). La regla remota
queda pendiente de acción y verificación por una persona administradora.

## Evidencia para la entrega

Antes de entregar, el equipo debe guardar capturas que muestren:

1. El tablero público con los nombres de estado y los conteos de trabajo.
2. El workflow exitoso de `main` correspondiente al commit entregado.
3. El reporte HTML de JaCoCo o una captura con los porcentajes de instrucciones
   y ramas del mismo commit.

Las capturas y la URL del tablero se deben agregar al PDF de entrega institucional;
su ausencia en el repositorio no se reemplaza por afirmaciones documentales.
