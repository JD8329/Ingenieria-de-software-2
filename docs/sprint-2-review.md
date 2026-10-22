# Revisión del Sprint 2 — AgroValle Connect

## Estado de este documento

Este registro es una revisión técnica de los artefactos del repositorio frente
a la guía del Segundo Corte. No es un acta de una Sprint Review del equipo y no
afirma presentación, aceptación de la docente/Product Owner ni acuerdos que no
estén documentados.

| Campo de la reunión | Estado |
|---|---|
| Fecha, hora y duración | Pendiente de registrar por el equipo |
| Asistentes y presentador | Pendiente de confirmar |
| Historias presentadas y aceptadas | Pendiente de registrar |
| Comentarios de la docente/Product Owner | No registrados |
| Evidencia de demostración | Pendiente de adjuntar |

## Verificación técnica del repositorio

El workflow de GitHub Actions está definido en
[`.github/workflows/ci.yml`](../.github/workflows/ci.yml). El 22 de octubre de
2026 se ejecutó `.\mvnw.cmd --batch-mode --no-transfer-progress clean verify`
localmente:

| Verificación | Resultado |
|---|---|
| Compilación y pruebas JUnit 5 | `BUILD SUCCESS`; 27 pruebas aprobadas, 0 fallos y 0 errores |
| Pruebas REST con MockMvc | Implementadas para los flujos documentados en Sprint 1 |
| Pruebas unitarias aisladas con Mockito | 8 pruebas aprobadas para `AgricultorService` y `LoteCosechaService` |
| Evidencia del ciclo TDD Red-Green-Refactor | No registrada en el repositorio; debe documentarse durante la ejecución del equipo |
| Checkstyle | 0 violaciones |
| JaCoCo — instrucciones | 95,6% |
| JaCoCo — ramas | 83,3% |
| JaCoCo — líneas | 95,5% |
| Reporte JaCoCo local | `target/site/jacoco/index.html` |
| Protección de `main` | No verificable en el worktree; requiere confirmación de un administrador |
| Tablero Scrumban y límites WIP | Sin URL ni evidencia remota en el repositorio; ver [`scrumban.md`](scrumban.md) |

Este resultado prueba la ejecución local; no demuestra que GitHub Actions haya
ejecutado el commit actual ni reemplaza las capturas del CI remoto y de JaCoCo
requeridas para la entrega.

## Alcance y pendientes antes de presentar

- Registrar la demostración de las historias que el equipo realmente complete;
  no atribuir al Sprint 2 historias futuras ni las historias no implementadas.
- Adjuntar evidencias reales del tablero, del último CI exitoso de `main` y de
  JaCoCo en el PDF institucional.
- Completar la fecha, los participantes, las historias presentadas, los
  resultados y los comentarios de la reunión real.
- Confirmar la configuración remota de protección de `main`.
