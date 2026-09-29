# Revisión y demo del Sprint 1 — AgroValle Connect

## Estado de la revisión

La bitácora registra sesiones de clase y trabajo los días 17, 22, 24 y 28 de
septiembre de 2026, pero no registra una sesión de Sprint Review ni una demo. Por
esa razón, no se presentan como realizadas la demostración funcional, la
aceptación de historias ni la retroalimentación de la docente. Esta revisión
queda pendiente de completar después de la demo del equipo.

| Campo | Registro disponible |
|---|---|
| Fecha y hora de la demo | No registrada en la bitácora |
| Participantes de la demo | No registrados. La asistencia a otras sesiones no se asume como asistencia a la demo. |
| Historias demostradas | No hay demo registrada. El Sprint Goal planificado cubre HU-01 y HU-02 (10 SP). |
| Evidencia de demo (captura, video o enlace) | Pendiente de generar durante la demo |
| Aceptación de la docente/Product Owner | Pendiente de confirmar |

## Evidencia técnica disponible

El check más reciente de GitHub Actions del [PR #21](https://github.com/JD8329/Ingenieria-de-software-2/pull/21)
ejecutó `clean verify` y finalizó con éxito el 29 de septiembre de 2026. El log
registra:

- 12 pruebas aprobadas, sin fallos ni errores.
- Cero violaciones de Checkstyle.
- Umbral de cobertura JaCoCo cumplido.
- `BUILD SUCCESS`.

[Ver ejecución de CI](https://github.com/JD8329/Ingenieria-de-software-2/actions/runs/36589475210).
Esta evidencia acredita la verificación automatizada de la rama del PR; no
sustituye una demo ante la docente ni acredita aceptación funcional por parte de
los interesados.

Al momento de actualizar este documento, el PR #21 continúa abierto y no tiene
aprobaciones de revisión registradas. La integración y la aprobación de HU-02
siguen pendientes.

## Guion propuesto para la demo

1. Registrar un agricultor válido y mostrar la respuesta `201 Created`.
2. Verificar que la contraseña no aparece en la respuesta y que queda cifrada al
   persistir.
3. Iniciar sesión con credenciales válidas y obtener un token JWT.
4. Intentar publicar un lote sin token o con token inválido y comprobar `401`.
5. Publicar un lote válido con el token y mostrar el identificador devuelto.
6. Probar datos inválidos, fecha pasada y cantidades/precios no positivos.
7. Consultar los lotes y comprobar que las respuestas no exponen el objeto
   agricultor, su cédula, correo ni contraseña cifrada.
8. Mostrar la ejecución de CI enlazada arriba.

## Resultados de la demo y retroalimentación

Completar esta tabla durante o inmediatamente después de una demo real:

| Escenario | Resultado observado en la demo | Evidencia | Comentarios de interesados |
|---|---|---|---|
| Registro de agricultor | Pendiente de demo | Pendiente | Pendiente |
| Login y autorización JWT | Pendiente de demo | Pendiente | Pendiente |
| Publicación y consulta de lotes | Pendiente de demo | Pendiente | Pendiente |
| Validaciones y protección de datos | Pendiente de demo | Pendiente | Pendiente |

## A completar al cierre de la revisión

- Fecha, hora y asistentes reales.
- Evidencia de la presentación (capturas, grabación o enlace autorizado).
- Historias aceptadas o cambios solicitados por la docente/Product Owner.
- Acciones acordadas para el siguiente sprint.
