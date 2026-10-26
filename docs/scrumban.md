# Scrumban y configuración de GitHub

## Límites de trabajo en curso

La guía del Segundo Corte establece estos límites para el tablero del equipo:

| Columna | WIP máximo |
|---|---:|
| In Progress | 3 |
| Code Review | 2 |

El repositorio no contiene un enlace al GitHub Project ni evidencia verificable
de que el tablero remoto esté configurado. El responsable del proyecto debe
crear o actualizar el tablero del equipo, usar estas columnas/límites, hacerlos
visibles en la vista y registrar aquí su URL:

**URL pública del tablero:** pendiente de proporcionar por el equipo.

## Protección de `main`

El workflow [`.github/workflows/ci.yml`](../.github/workflows/ci.yml) se ejecuta
en Pull Requests y en los pushes a `main` y `develop`. El job compila, ejecuta
pruebas, Checkstyle y JaCoCo mediante `./mvnw clean verify`.

La protección de ramas es una configuración del repositorio en GitHub y no se
puede activar mediante un archivo del worktree. Un administrador del repositorio
debe configurarla en **Settings → Rules → Rulesets** (o **Branches → Branch
protection rules**) para `main` con estos requisitos:

- Bloquear el push directo y exigir Pull Requests para integrar cambios.
- Exigir una aprobación de otro integrante antes de integrar.
- Exigir que pase el status check del job **Tests, Checkstyle y cobertura**.
- Descartar aprobaciones obsoletas cuando cambie el commit del Pull Request.

**Estado verificable desde el repositorio:** el workflow existe; la protección
remota y su estado no son verificables localmente y quedan pendientes de
confirmación por un administrador.

## Evidencia para la entrega

Antes de entregar, el equipo debe guardar capturas que muestren:

1. El tablero público con ambos límites WIP visibles.
2. El último workflow exitoso de la rama `main`.
3. El reporte HTML de JaCoCo o los logs del job con los umbrales aprobados.

Las capturas y la URL del tablero se deben agregar al PDF de entrega institucional;
su ausencia en el repositorio no se reemplaza por afirmaciones documentales.
