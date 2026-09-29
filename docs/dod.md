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

Las firmas confirman la aceptación del equipo y deben ser diligenciadas por sus
integrantes; no se completan sin su aprobación.

| Integrante | Firma / Nombre | Fecha |
|---|---|---|
| | | |
| | | |
| | | |
| | | |

## Estado verificable del Sprint 1

El DoD se evalúa por historia. Que la integración continua pase no sustituye la
revisión por pares ni la integración del Pull Request.

| Historia | Estado de integración | Verificación automatizada | Revisión por pares | Evaluación DoD |
|---|---|---|---|---|
| HU-01 | Integrada en `main` mediante PR #20 | CI aprobado: pruebas, Checkstyle y cobertura | No hay aprobación formal registrada en GitHub | Pendiente de confirmar revisión y firmas |
| HU-02 | PR #21 abierto hacia `main` | CI aprobado: 12 pruebas, cero fallos, Checkstyle sin violaciones y umbral JaCoCo cumplido | No hay aprobación formal registrada en GitHub | No terminada hasta revisión e integración |
| HU-11 | Implementación integrada en `main` como soporte de HU-02 mediante PR #20 | CI aprobado en PR #20 | No hay aprobación formal registrada en GitHub | Pendiente de confirmar revisión y firmas |

Evidencia más reciente de HU-02: [ejecución de CI del PR #21](https://github.com/JD8329/Ingenieria-de-software-2/actions/runs/36589475210).
