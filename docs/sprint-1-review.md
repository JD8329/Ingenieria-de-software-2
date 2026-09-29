# Revisión y demo del Sprint 1 — AgroValle Connect

**Estado:** plantilla pendiente de completar durante la demo del Sprint Review.

Este documento no afirma que la demo ya se haya realizado. El equipo debe completar
la fecha, asistentes, resultado observado y enlaces a evidencias después de ejecutar
la presentación.

## Datos de la revisión

| Campo | Registro |
|---|---|
| Fecha y hora | Pendiente de diligenciar |
| Participantes | Pendiente de confirmar |
| URL de la demo o evidencia | Pendiente de adjuntar |
| Historias demostradas | Pendiente de confirmar |

## Evidencia técnica automática previa a la demo

En la rama de trabajo, `./mvnw --batch-mode --no-transfer-progress clean verify`
finalizó correctamente: 12 pruebas pasan, Checkstyle reporta cero violaciones y
JaCoCo mide 90,48% de cobertura de líneas (171/189). Esta verificación local no
reemplaza la demo, la aceptación de historias ni la ejecución de GitHub Actions
después de publicar la rama.

## Guion de demostración

1. Registrar un agricultor válido y mostrar la respuesta HTTP `201 Created`.
2. Iniciar sesión con las credenciales del agricultor y obtener un token JWT.
3. Intentar un registro duplicado y mostrar el rechazo esperado.
4. Publicar un lote válido con el token y mostrar su ID.
5. Intentar publicar sin token y con un token inválido; mostrar `401 Unauthorized`.
6. Intentar publicar un lote con datos inválidos o fecha anterior a hoy y mostrar
   el rechazo esperado.
7. Mostrar el resultado de las pruebas JUnit 5, Checkstyle y cobertura de JaCoCo.

## Resultados observados y retroalimentación

| Escenario | Resultado observado | Evidencia (captura, URL, log o PR) | Comentarios de interesados |
|---|---|---|---|
| Registro válido de agricultor | Pendiente de demo | Pendiente | Pendiente |
| Validación de registro duplicado | Pendiente de demo | Pendiente | Pendiente |
| Login y autorización JWT | Pendiente de demo | Pendiente | Pendiente |
| Publicación de lote válido | Pendiente de demo | Pendiente | Pendiente |
| Validaciones de publicación | Pendiente de demo | Pendiente | Pendiente |
| Pruebas y controles de calidad | Pendiente de demo | Pendiente | Pendiente |

## Aceptación y acciones

- Historias aceptadas por la docente/Product Owner: pendiente de confirmar.
- Hallazgos o cambios solicitados: pendiente de registrar.
- Acciones para el siguiente sprint: pendiente de acordar.
