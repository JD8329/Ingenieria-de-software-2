# Definition of Done (DoD) — AgroValle Connect

Una historia de usuario se considera **Done** cuando cumple los criterios que
aplican a su alcance. Este checklist define la regla del equipo; no sustituye
la evidencia de pruebas, revisión o aceptación que debe enlazarse en el Pull
Request.

## Criterios de terminado

- [ ] Se cumplen los criterios de aceptación de la historia y sus escenarios
  BDD están verificados.
- [ ] El proyecto compila y la suite completa de pruebas automatizadas pasa con
  el Maven Wrapper: `./mvnw --batch-mode --no-transfer-progress clean verify`
  (Windows PowerShell: `.\mvnw.cmd --batch-mode --no-transfer-progress clean verify`).
- [ ] Checkstyle no reporta violaciones.
- [ ] JaCoCo aprueba el mínimo configurado de **60 % en instrucciones, ramas y
  líneas**. El reporte HTML se genera en `target/site/jacoco/index.html`.
- [ ] El Pull Request tiene al menos una aprobación de otro integrante y todos
  los checks requeridos están verdes antes de integrarlo.
- [ ] Los commits siguen Conventional Commits y la documentación relacionada se
  actualiza cuando cambian contratos, reglas, configuración o decisiones.
- [ ] La configuración sensible se obtiene de variables de entorno o secretos;
  no se agregan contraseñas, tokens ni URLs de conexión con credenciales al
  repositorio.
- [ ] Si el cambio modifica el esquema de datos, incluye y documenta la
  migración o actualización necesaria para los entornos afectados.

La verificación local no sustituye al CI remoto. El workflow actual ejecuta
Checkstyle, JUnit y JaCoCo como pasos separados; una ejecución local de
`clean verify` sirve como verificación integral antes de abrir o actualizar el
Pull Request.

## Herramientas locales

El hook de Husky en `.husky/pre-commit` ejecuta `./mvnw verify` cuando Husky
está instalado y habilitado en el clon. Es una ayuda local; el CI del Pull
Request es la evidencia compartida y verificable. Si el hook no está activo, se
deben ejecutar manualmente las verificaciones del Maven Wrapper.

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

## Estado verificable del Sprint 7

HU-13 y HU-14 se implementaron en ramas independientes creadas desde
`origin/main` (`49d4581`). En cada rama se ejecutó `mvnw clean install`; las
pruebas, Checkstyle y umbrales de cobertura JaCoCo finalizaron correctamente.
La revisión por pares y la integración mediante Pull Request siguen pendientes.
Husky continúa sin activarse en este clon, por lo que las validaciones se
ejecutaron manualmente.

| Historia | Rama de trabajo | Build, pruebas, Checkstyle y cobertura | Revisión por pares | Husky local |
|---|---|---|---|---|
| HU-13 | `feature/sprint7-hu13-edicion-lotes` | `clean install`: 80 pruebas aprobadas; Checkstyle sin violaciones; JaCoCo aprobó los umbrales. | Pendiente de Pull Request y aprobación. | Pendiente de activar el hook en este clon. |
| HU-14 | `feature/sprint7-hu14-perfil-agricultor` | `clean install`: 74 pruebas aprobadas; Checkstyle sin violaciones; JaCoCo aprobó los umbrales. | Pendiente de Pull Request y aprobación. | Pendiente de activar el hook en este clon. |

El checklist general no se marca como terminado hasta completar la revisión
formal y la integración de ambas ramas.

## Estado verificable del Sprint 8

HU-15 se implementó en una rama independiente creada desde `origin/main`.
La verificación automatizada pasó con `clean install`; los endpoints también
se comprobaron en ejecución local con H2. El envío del mensaje se prueba con un
remitente SMTP simulado; la entrega real requiere configurar el servidor SMTP
del entorno. El PR #41 está abierto y quedan pendientes la aprobación y la
integración. Husky continúa sin activarse en este clon.

| Historia | Rama de trabajo | Build, pruebas, Checkstyle y cobertura | BDD y documentación | Revisión por pares | Husky local |
|---|---|---|---|---|---|
| HU-15 | `feature/sprint8-hu15-recuperacion-password` | `clean install`: 88 pruebas aprobadas; Checkstyle sin violaciones; umbrales JaCoCo satisfechos. | Solicitud no enumerable, token de un solo uso y vigencia de 30 minutos, correo SMTP y cambio BCrypt para agricultores y comerciantes verificados. Documentación en español. | PR #41 abierto; pendiente de aprobación. | Pendiente de activar el hook en este clon. |
