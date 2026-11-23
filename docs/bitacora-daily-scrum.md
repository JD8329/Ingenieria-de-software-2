# Bitácora de reuniones de seguimiento — Sprints 1 y 2

## Cadencia y equipo

El equipo informa que se reunía semanalmente durante aproximadamente una hora.
En el Sprint 1 también hay sesiones de clase y sesiones extra con fechas
concretas; se conservan como fueron registradas y no se extrapolan a semanas
sin evidencia. Una reunión semanal de una hora es una sesión de seguimiento,
no necesariamente un Daily Scrum formal; se usa este archivo como bitácora del
equipo sin reclasificar las sesiones como ceremonias que no fueron confirmadas.

El equipo confirma que no se reunió todos los días para realizar un Daily Scrum.
Por tanto, esta bitácora no afirma que hubiera reuniones diarias: registra las
sesiones reales que se recuerdan y deja sin completar las fechas, horas y
detalles que no tienen respaldo. Las horas aproximadas se mantienen solo cuando
ya fueron informadas; no se asignan horas exactas retrospectivamente.

| Integrante | Rol |
|---|---|
| Juan David Gonzalez | Scrum Master |
| Steven Andres Guacheta | Developer |
| Andres Felipe Lopez | Developer |
| Nestor Javier Hernandez | Developer |

## Registro de sesiones del Sprint 1

Completar una fila después de cada reunión real. No registrar acuerdos, bloqueos, asistencia ni duración exacta sin confirmarlos con el equipo. La columna "Referencia técnica" enlaza la sesión con la rama, PR o commit en el que se trabajó ese día (dejar en blanco si no aplica).

| Fecha | Tipo de sesión | Duración | Asistentes | Avance desde la sesión anterior | Plan hasta la siguiente sesión | Impedimentos / acciones | Referencia técnica |
|---|---|---|---|---|---|---|---|
| 17 de septiembre | Clase | Aproximadamente 2 horas | Todo el equipo | Pendiente de diligenciar | Pendiente de diligenciar | Pendiente de diligenciar | Pendiente de diligenciar |
| 22 de septiembre | Sesión extra | Aproximadamente 1 hora | Todo el equipo | Definición del Sprint Goal del Sprint 1 | Iniciar la implementación de las historias de usuario priorizadas | Dudas iniciales sobre el alcance técnico del proyecto (sin detalle adicional registrado) | — |
| 24 de septiembre | Clase | Aproximadamente 2 horas | Todo el equipo | Pendiente de diligenciar | Pendiente de diligenciar | Pendiente de diligenciar | Pendiente de diligenciar |
| 28 de septiembre | Sesión extra | Aproximadamente 1 hora | Todo el equipo | Revisión conjunta de los requisitos de la Sesión 4 para identificar el estado del Sprint 1 frente a la rúbrica de la profesora | Documentar el Sprint Goal y el desglose técnico; comenzar la implementación de HU-01 (registro de agricultores) y HU-02 (publicación de lotes de cosecha) con entidad, controlador, repositorio, servicio y pruebas unitarias en JUnit 5; continuar la configuración del tablero Kanban (columnas y límites WIP) a cargo de un integrante del equipo | Pendiente de confirmar con el equipo | Commits de configuración inicial del repositorio (`.gitignore`, `README.md`) |

*Esta bitácora se inicia con la cadencia y los roles informados por el equipo. Las fechas y resultados de sesiones anteriores deben ser completados por sus participantes a partir de sus registros; no se han inventado actas ni acuerdos no confirmados.*

## Registro del Sprint 2

El equipo recuerda que, durante las dos semanas previas al 5 de noviembre de
2026, se reunió semanalmente durante aproximadamente una hora para trabajar en
los puntos del Sprint 2 y los artefactos de diseño. Las entradas siguientes son
una reconstrucción posterior basada en ese recuerdo, no actas contemporáneas.
Las fechas exactas, la asistencia, los avances específicos de cada encuentro y
los impedimentos quedan pendientes de confirmar con los participantes.

| Fecha | Tipo de sesión | Duración | Asistentes | Avance desde la sesión anterior | Plan hasta la siguiente sesión | Impedimentos / acciones | Referencia técnica |
|---|---|---|---|---|---|---|---|
| Semana del 22 al 28 de octubre de 2026 (fecha exacta por confirmar) | Reunión semanal | Aproximadamente 1 hora | Pendiente de confirmar | Trabajo en los puntos del Sprint 2 y de diseño (detalle por confirmar) | Continuar con los puntos pendientes del Sprint 2 y diseño | Pendiente de confirmar | Artefactos de Sprint 2 y `docs/diseno/` |
| Semana del 29 de octubre al 4 de noviembre de 2026 (fecha exacta por confirmar) | Reunión semanal | Aproximadamente 1 hora | Pendiente de confirmar | Continuación del trabajo en los puntos del Sprint 2 y de diseño (detalle por confirmar) | Pendiente de confirmar | Pendiente de confirmar | Artefactos de Sprint 2 y `docs/diseno/` |

## Seguimiento de historias — Sprints 3 a 8

Para completar el ejercicio académico, se incluye una sesión representativa por sprint. Los avances y referencias técnicas se contrastaron con el historial de GitHub; la duración de 15 minutos y la asistencia por roles son valores de la simulación, no actas de reuniones ocurridas. Las fechas indican actividad técnica visible en commits o Pull Requests, no la fecha de una reunión.

| Fecha de actividad en GitHub | Tipo de sesión | Duración | Asistentes | Avance desde la sesión anterior | Plan hasta la siguiente sesión | Impedimentos / acciones | Referencia técnica |
|---|---|---|---|---|---|---|---|
| 11 de noviembre de 2026 | Daily Scrum — seguimiento de Sprint 3 | 15 minutos | Scrum Master y Developers (por rol) | HU-05: envío e historial de mensajes por lote. HU-06: checkout y reserva de pedidos con validación de stock y persistencia transaccional. | Continuar con despacho y calificación de pedidos (HU-07 y HU-08). | HU-05/HU-06 aún reciben `comercianteId` desde la solicitud; dejar registrada la limitación y derivar la identidad del usuario autenticado en una iteración de seguridad. | [HU-05/HU-06](sprint-3-historias-5-y-6.md) · [PR #35](https://github.com/JD8329/Ingenieria-de-software-2/pull/35) |
| 11 de noviembre de 2026 | Daily Scrum — seguimiento de Sprint 4 | 15 minutos | Scrum Master y Developers (por rol) | HU-07: programación de despacho, ruta y estado del pedido. HU-08: calificación y promedio del agricultor. | Continuar con notificaciones de cambios de estado y reportes por municipio (HU-09 y HU-10). | Revisar autorización del agricultor: el controlador recibe `agricultorId` como parámetro opcional, no como identidad derivada del token. | [HU-07/HU-08](sprint-4-historias-7-y-8.md) · [PR #35](https://github.com/JD8329/Ingenieria-de-software-2/pull/35) |
| 21 de noviembre de 2026 | Daily Scrum — seguimiento de Sprint 5 | 15 minutos | Scrum Master y Developers (por rol) | HU-09: eventos de cambio de estado y notificaciones persistidas. HU-10: reporte de ventas agrupadas por municipio. | Revisar integración de autenticación por rol y registro de comerciantes (HU-11 y HU-12). | Las notificaciones se guardan en la base de datos; no representan envío a un canal externo. Confirmar con el equipo si ese alcance satisface la historia. | [HU-09/HU-10](sprint-5-historias-9-y-10.md) · [PR #35](https://github.com/JD8329/Ingenieria-de-software-2/pull/35) |
| 21 de noviembre de 2026 | Daily Scrum — seguimiento de Sprint 6 | 15 minutos | Scrum Master y Developers (por rol) | HU-11: claim de rol en JWT y reglas de autorización. HU-12: registro de cuentas de comerciantes. Las historias se integraron mediante PR separados. | Continuar con edición/baja de lotes y perfil público del agricultor (HU-13 y HU-14). | Verificar que la autorización de cada endpoint use el rol y la identidad esperados; el PR acredita integración técnica, no aceptación académica. | [HU-11](sprint-6-hu11-login-jwt.md) · [HU-12](sprint-6-hu12-registro-comerciantes.md) · [PR #36](https://github.com/JD8329/Ingenieria-de-software-2/pull/36) · [PR #38](https://github.com/JD8329/Ingenieria-de-software-2/pull/38) |
| 21 de noviembre de 2026 | Daily Scrum — seguimiento de Sprint 7 | 15 minutos | Scrum Master y Developers (por rol) | HU-13: actualización y desactivación de lotes. HU-14: perfil público con lotes activos y calificación. | Continuar con recuperación de contraseña (HU-15) y validar el flujo completo de autenticación. | Confirmar en la revisión funcional los permisos y los datos expuestos por el perfil; no consta aceptación de la docente en los PR consultados. | [HU-13](sprint-7-hu13-edicion-lotes.md) · [HU-14](sprint-7-hu14-perfil-agricultor.md) · [PR #39](https://github.com/JD8329/Ingenieria-de-software-2/pull/39) · [PR #40](https://github.com/JD8329/Ingenieria-de-software-2/pull/40) |
| 21 de noviembre de 2026 | Daily Scrum — seguimiento de Sprint 8 | 15 minutos | Scrum Master y Developers (por rol) | HU-15: solicitud de recuperación no enumerable, token de un solo uso con vigencia limitada y cambio de contraseña; PR integrado en `main`. | Revisar configuración SMTP del entorno, ejecutar el flujo con el proveedor de correo acordado y completar la evidencia de aceptación. | La entrega real de correo requiere credenciales SMTP del entorno; las pruebas con remitente simulado no demuestran entrega externa. | [HU-15](sprint-8-hu15-recuperacion-password.md) · [PR #41](https://github.com/JD8329/Ingenieria-de-software-2/pull/41) |
