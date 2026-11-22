# Documentación — AgroValle Connect

Índice de planificación, diseño, implementación y evidencias del proyecto.
Los documentos distinguen entre funcionalidades implementadas, propuestas de
diseño y datos de reuniones que deben confirmarse con el equipo.

## Producto y calidad

- [Product Backlog](../BACKLOG.md): historias, prioridades, criterios BDD y estado de integración.
- [Definition of Done](dod.md): criterios comunes para considerar una historia terminada.
- [Scrumban y configuración de GitHub](scrumban.md): tablero, límites WIP, CI y estado de protección de `main`.
- [Bitácora de reuniones y Daily Scrum](bitacora-daily-scrum.md): sesiones registradas y reconstrucciones declaradas.

## Sprints

| Sprint | Alcance | Plan o especificaciones | Revisión / retrospectiva |
|---|---|---|---|
| 1 | Registro de agricultor y publicación de lote (HU-01, HU-02) | [Planificación](sprint-1-planning.md) | [Review y demo](sprint-1-review.md) · [Retrospectiva](sprint-1-retrospective.md) |
| 2 | Precios promedio y filtros del catálogo (HU-03, HU-04) | [Planificación](sprint-2-planning.md) · [HU-03](sprint-2-hu03.md) · [HU-04](sprint-2-hu04.md) | [Review técnica](sprint-2-review.md) · [Retrospectiva](sprint-2-retrospective.md) |
| 3 | Contacto directo y reserva de pedidos (HU-05, HU-06) | [Especificación](sprint-3-historias-5-y-6.md) | No hay acta de review/retrospectiva enlazada |
| 4 | Despacho y calificación (HU-07, HU-08) | [Especificación](sprint-4-historias-7-y-8.md) | No hay acta de review/retrospectiva enlazada |
| 5 | Notificaciones y reportes (HU-09, HU-10) | [Especificación](sprint-5-historias-9-y-10.md) | No hay acta de review/retrospectiva enlazada |
| 6 | Autenticación por rol y registro de comerciantes (HU-11, HU-12) | [HU-11](sprint-6-hu11-login-jwt.md) · [HU-12](sprint-6-hu12-registro-comerciantes.md) | No hay acta de review/retrospectiva enlazada |
| 7 | Edición de lotes y perfil público (HU-13, HU-14) | [HU-13](sprint-7-hu13-edicion-lotes.md) · [HU-14](sprint-7-hu14-perfil-agricultor.md) | No hay acta de review/retrospectiva enlazada |
| 8 | Recuperación de contraseña (HU-15) | [Especificación](sprint-8-hu15-recuperacion-password.md) | No hay acta de review/retrospectiva enlazada |

Las referencias de estado del backlog y los Pull Requests deben leerse junto con
el estado actual de GitHub; los registros históricos conservan el estado que
tenían cuando se redactaron.

## Diseño

- [Índice de artefactos de diseño](diseno/README.md)
- [Resumen y cambios del Sprint de Diseño](resumen-y-cambios-del-sprint-diseno.md)
- [Documentación integral: normalización, prototipos y arquitectura](documentacion-integral-agrovalle.md)
- [Requisito HU-01: IEEE 29148 y BDD](ieee29148-req01.md)

## Pruebas, TDD y CI

- [Evidencia TDD de sesión 8](evidencias/tdd-sesion-8.md)
- [Taller de integración continua de la Sesión 10](sesion-10-integracion-continua.md)
- [Verificación técnica del Segundo Corte](entrega-segundo-corte.md): snapshot documental del corte; consultar el backlog y los PR para el estado posterior.

## Entornos

- [Base de datos staging / preproducción](staging-preproduccion.md)
- [Ejecución local y variables de entorno](../README.md#calidad)

## Evidencia pendiente de completar por el equipo

- Validar las fechas, asistentes, avances y bloqueos de las reuniones reconstruidas.
- Adjuntar actas, capturas, comentarios de la docente/Product Owner y aceptación
  únicamente cuando existan y el equipo los confirme.
- Confirmar qué historias de cada sprint se presentaron en una demo real; que una
  funcionalidad esté fusionada no demuestra por sí sola su aceptación académica.
- Completar la portada y los datos institucionales del PDF sin incluir
  identificaciones personales en el repositorio público.
