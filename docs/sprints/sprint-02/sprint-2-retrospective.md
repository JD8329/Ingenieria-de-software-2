# Retrospectiva del Sprint 2 — AgroValle Connect

## Estado del registro

Este documento organiza la retrospectiva a partir de evidencia técnica del
repositorio. No hay un acta confirmada de la reunión; por eso, los puntos de
análisis y las acciones de abajo son propuestas para discutir, no acuerdos
atribuidos al equipo.

| Campo | Registro |
|---|---|
| Fecha y hora de la retrospectiva | Pendiente de confirmar |
| Participantes y facilitador | Pendiente de confirmar |
| Evidencia o acta de la reunión | Pendiente de adjuntar |
| Acuerdos aprobados por el equipo | Pendiente de registrar |

## Contexto verificable del sprint

- HU-03 incorpora el registro autenticado de precios y la consulta del promedio
  regional para las últimas 24 horas, con un mínimo de 50 transacciones.
- HU-04 incorpora filtros del catálogo por municipio y categoría, de forma
  individual o combinada, manteniendo compatibilidad con lotes sin categoría.
- Las dos historias cuentan con pruebas de integración documentadas en sus
  respectivos archivos: [`sprint-2-hu03.md`](sprint-2-hu03.md) y
  [`sprint-2-hu04.md`](sprint-2-hu04.md).
- El repositorio también documenta la arquitectura, los artefactos de diseño y
  la implementación del patrón Observer. La revisión técnica del sprint registra
  resultados locales de pruebas, Checkstyle y JaCoCo; véase
  [`sprint-2-review.md`](sprint-2-review.md).
- La documentación aclara que la revisión técnica no reemplaza el registro de
  la reunión, la demo del equipo ni la aceptación de la docente/Product Owner.

## Temas para la conversación del equipo

### Qué conviene conservar

- Separar responsabilidades entre controladores, servicios y repositorios, y
  respaldar los flujos principales con pruebas automatizadas.
- Validar las historias mediante criterios observables: periodo y umbral de
  HU-03, y filtros y compatibilidad de HU-04.
- Mantener documentación que distinga lo implementado de los modelos y
  funcionalidades que siguen siendo de referencia o futuras.

### Qué conviene revisar

- Confirmar si el trabajo comprometido para HU-03 y HU-04 fue realista para la
  capacidad del equipo y qué dependencias generaron esperas.
- Revisar si las pruebas cubren también casos límite y errores de entrada, y si
  sus resultados son fáciles de entender cuando algo falla.
- Verificar que la documentación, el tablero y el estado de los Pull Requests
  reflejen lo que efectivamente se terminó y se demostró.
- Identificar qué hizo falta para registrar la demo, los comentarios y la
  aceptación de las historias, sin completar esos datos retrospectivamente.

## Acciones propuestas para validar

Las siguientes acciones no se consideran comprometidas hasta que el equipo las
apruebe y asigne responsables y fechas en la reunión.

| Acción propuesta | Resultado esperado | Responsable | Fecha objetivo | Estado |
|---|---|---|---|---|
| Registrar la demo y los resultados de aceptación de cada historia. | Evidencia verificable de lo presentado y de los comentarios recibidos. | Por asignar | Por acordar | Propuesta |
| Mantener criterios de aceptación y casos de prueba vinculados a cada historia. | Menos ambigüedad al implementar y revisar HU-03/HU-04. | Por asignar | Por acordar | Propuesta |
| Actualizar el tablero y la documentación al cerrar cada historia. | Estado coherente entre backlog, Pull Request, pruebas y documentación. | Por asignar | Por acordar | Propuesta |
| Revisar con un administrador los requisitos remotos de protección de `main` y dejar evidencia de su configuración. | Confirmar qué controles se exigen antes de integrar cambios. | Por asignar | Por acordar | Propuesta |

## Cierre de la retrospectiva

Completar esta sección después de la reunión real:

- **Acuerdos aceptados:** pendiente.
- **Acciones priorizadas y responsables:** pendiente.
- **Fecha de seguimiento:** pendiente.
- **Observaciones adicionales del equipo:** pendiente.
