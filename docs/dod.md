# Definition of Done (DoD) — AgroValle Connect

Una Historia de Usuario se considera **Terminada (Done)** cuando cumple TODOS los siguientes criterios:

- [ ] El código compila sin errores (`mvn clean install`)
- [ ] Checkstyle pasa sin violaciones (`mvn checkstyle:check`)
- [ ] 100% de las pruebas unitarias e integradas pasan (`mvn test`)
- [ ] Cobertura mínima del 60% en instrucciones, ramas y líneas (JaCoCo, verificada en `mvn verify`)
- [ ] Código revisado y aprobado por al menos un compañero (Pull Request)
- [ ] Commits bajo el estándar Conventional Commits
- [ ] README.md y /docs actualizados si el cambio lo requiere
- [ ] Husky (`.husky/pre-commit`) ejecuta `./mvnw verify` y bloquea commits si falla
- [ ] Criterios de aceptación BDD (Given-When-Then) de la HU cumplidos y verificados

## Firmas del equipo

Firmas registradas por el equipo el 29/09/2026.

| Integrante | Firma / Nombre | Fecha |
|---|---|---|
| Juan David Gonzalez | Juan | 29/09/2026 |
| Néstor Javier Hernández Vallecilla | Néstor | 29/09/2026 |
| Steven Andrés Guachetá Veles | Steven | 29/09/2026 |
| Andrés Felipe Lopez Murillo | Andrés | 29/09/2026 |

## Estado verificable del Sprint 1

El DoD general del equipo conserva la revisión por pares como criterio. Para
este cierre técnico, solicitado sin reabrir la aprobación formal, la tabla
registra únicamente implementación, pruebas y evidencia; no afirma una
aprobación no registrada.

| Historia | Estado de integración | Verificación automatizada | Estado técnico para el cierre solicitado |
|---|---|---|---|
| HU-01 | Integrada en `main`; cambios de Sprint 1 incluidos en PR #20 | `./mvnw clean verify`: suite completa con 19 pruebas aprobadas; cobertura sobre 60 %, Checkstyle limpio | Funcionalidad, validaciones de registro, duplicidad de datos en servicio y persistencia, y protección de contraseña verificadas. |
| HU-02 | Integrada en `main` mediante PR #21 el 29/09/2026 | CI histórico aprobado: 12 pruebas, Checkstyle sin violaciones y cobertura JaCoCo sobre el umbral | Publicación autenticada, validaciones, consultas y demo técnica individual documentadas. |
| HU-11 (soporte de HU-02) | Login JWT integrado en `main` mediante PR #20 | CI histórico aprobado | Login JWT y autorización de publicación probados como soporte de HU-02. |

Evidencia de CI de HU-02: [ejecución del PR #21](https://github.com/JD8329/Ingenieria-de-software-2/actions/runs/36589475210).

## Estado verificable del Sprint 6

La verificación completa se ejecutó en la rama HU-12, que contiene el commit de
HU-11 como dependencia. La revisión por pares todavía requiere Pull Request. El
hook Husky tampoco está activado en este clon (`core.hooksPath` no está
configurado); los mismos controles automatizados se ejecutaron manualmente.

| Historia | Rama de trabajo | Build, pruebas, Checkstyle y cobertura | BDD y documentación | Revisión por pares | Husky local |
|---|---|---|---|---|---|
| HU-11 | `feature/sprint6-hu11-login-jwt` | `clean install`: 71 pruebas aprobadas; Checkstyle limpio; umbrales JaCoCo satisfechos en la rama dependiente HU-12. | Login correcto/incorrecto, claim `rol` y autorización de `PRODUCCION` verificados; documentación en español. | Pendiente de Pull Request y aprobación. | Pendiente de activar el hook en este clon. |
| HU-12 | `feature/sprint6-hu12-registro-comerciantes` | `clean install`: 71 pruebas aprobadas; Checkstyle limpio; umbrales JaCoCo satisfechos. | Registro, persistencia, BCrypt, unicidad de correo/NIT, login y rol verificados; endpoint en ejecución respondió 201/200/403 según el caso. Documentación en español. | Pendiente de Pull Request y aprobación. | Pendiente de activar el hook en este clon. |

El DoD completo queda sujeto a la revisión formal por Pull Request y a la
activación local de Husky; no se marcan esos criterios como terminados.
