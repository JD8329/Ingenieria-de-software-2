# Definition of Done (DoD) — AgroValle Connect

Una Historia de Usuario se considera **Terminada (Done)** cuando cumple TODOS los siguientes criterios:

- [ ] El código compila sin errores (`mvn clean install`)
- [ ] Checkstyle pasa sin violaciones (`mvn checkstyle:check`)
- [ ] 100% de las pruebas unitarias e integradas pasan (`mvn test`)
- [ ] Cobertura de líneas mínima del 60% (JaCoCo, verificada en `mvn verify`)
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

El DoD se evalúa por historia. Que la integración continua pase no sustituye la
revisión por pares ni la integración del Pull Request.

| Historia | Estado de integración | Verificación automatizada | Revisión por pares | Evaluación DoD |
|---|---|---|---|---|
| HU-01 | Integrada en `main`; cambios de Sprint 1 incluidos en PR #20 | CI del PR #20 aprobado | El PR inicial #1 fue aprobado por Steven-Velez. El PR #20, con cambios posteriores, no tiene aprobación formal registrada | Parcialmente verificada: hay implementación, CI exitoso y aprobación del PR inicial; confirmar revisión de los cambios posteriores y los criterios específicos antes de marcarla Done |
| HU-02 | Integrada en `main` mediante PR #21 el 29/09/2026 | CI aprobado: 12 pruebas, cero fallos, Checkstyle sin violaciones y cobertura JaCoCo sobre el umbral | El PR #21 no tiene aprobación formal registrada | Parcialmente verificada: implementación, pruebas integradas y demo técnica individual registrada en `docs/sprint-1-review.md`; falta revisión formal visible por un compañero y aceptación de la docente |
| HU-11 (soporte de HU-02) | Login JWT integrado en `main` mediante PR #20 | CI del PR #20 aprobado | El PR #20 no tiene aprobación formal registrada | Parcialmente verificada: login JWT incluido como soporte de HU-02; falta revisión formal visible por un compañero |

Evidencia más reciente de HU-02: [ejecución de CI del PR #21](https://github.com/JD8329/Ingenieria-de-software-2/actions/runs/36589475210).
