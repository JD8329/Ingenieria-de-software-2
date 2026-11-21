# Planificación del Sprint 2 — AgroValle Connect

## Meta del sprint

Ampliar el catálogo y la información de mercado para que los usuarios puedan
consultar precios promedio regionales y los comerciantes encuentren lotes
activos por municipio y categoría. El sprint reúne **8 Story Points**:
HU-03 (3 SP) y HU-04 (5 SP), según el Product Backlog.

Esta planificación se documenta a partir del alcance y las estimaciones del
backlog, y se contrasta con las especificaciones técnicas existentes de HU-03 y
HU-04. No registra fechas, acuerdos de reunión ni compromisos individuales que
no estén confirmados.

## Historias seleccionadas

| Historia | Prioridad | Puntos | Resultado esperado |
|---|---:|---:|---|
| HU-03: Visualización y cálculo de precios promedio regionales | Should Have | 3 | Registrar reportes de precio autenticados y consultar públicamente el promedio de un producto en las últimas 24 horas cuando haya al menos 50 registros. |
| HU-04: Filtro de productos por municipio de origen y categoría | Must Have | 5 | Consultar lotes activos por municipio, categoría o ambos, conservando la compatibilidad con lotes sin categoría. |
| **Total** |  | **8** |  |

## Descomposición técnica e ISO/IEC 25010

### HU-03 — Precios promedio regionales

| ID | Tarea técnica | Característica ISO/IEC 25010 | Verificación |
|---|---|---|---|
| HU03-T1 | Persistir transacciones de precio con producto, municipio, precio, fecha asignada por el servidor y agricultor responsable. | Adecuación funcional — completitud funcional; fiabilidad — integridad de datos. | Guardar y consultar una transacción asociada al agricultor autenticado en pruebas de integración. |
| HU03-T2 | Validar los datos obligatorios y el precio positivo, limitado a dos decimales. | Fiabilidad — integridad y consistencia de datos. | Enviar valores válidos e inválidos y comprobar los estados y mensajes de respuesta. |
| HU03-T3 | Exponer el registro autenticado y la consulta pública, sin distinguir mayúsculas en el producto. | Seguridad — autenticidad y control de acceso; adecuación funcional — corrección funcional. | El registro sin JWT se rechaza; la consulta pública acepta el producto sin importar mayúsculas. |
| HU03-T4 | Calcular en la base de datos el promedio aritmético sobre las 24 horas anteriores y publicar el resultado solo con 50 o más registros. | Eficiencia de desempeño — utilización de recursos; adecuación funcional — corrección funcional. | Verificar el umbral, el conteo, el promedio, la ventana temporal y la exclusión de otros productos. |

### HU-04 — Filtro del catálogo por municipio y categoría

| ID | Tarea técnica | Característica ISO/IEC 25010 | Verificación |
|---|---|---|---|
| HU04-T1 | Añadir categoría opcional al lote sin romper el contrato existente de publicación de HU-02. | Compatibilidad — coexistencia; mantenibilidad — modularidad. | Publicar y consultar lotes con y sin categoría; comprobar que los campos anteriores siguen disponibles. |
| HU04-T2 | Implementar filtros opcionales por municipio y categoría, restringiendo los resultados a lotes activos. | Adecuación funcional — corrección funcional; eficiencia de desempeño — comportamiento temporal. | Probar cada filtro por separado y en conjunto, y comprobar que no aparecen lotes inactivos. |
| HU04-T3 | Validar que se informe al menos un filtro y que el municipio pertenezca a los valores admitidos por el modelo. | Fiabilidad — tolerancia a fallos; adecuación funcional — corrección funcional. | Omitir ambos filtros, enviar categoría vacía y enviar municipio no admitido; verificar `400 Bad Request`. |
| HU04-T4 | Comparar la categoría sin distinguir mayúsculas y exponer municipio, categoría, cantidad, precio y estado en el resultado. | Adecuación funcional — completitud funcional; usabilidad — reconocibilidad de la información. | Comparar categorías con distintas combinaciones de mayúsculas y validar la representación de la respuesta. |

> **Decisión de alcance para HU-03:** el flujo implementado registra reportes de
> precio proporcionados por agricultores autenticados; no representa compras
> cerradas ni descuenta inventario. La fecha del reporte la asigna el servidor.
> Esta precisión evita atribuir al sprint el flujo de pedidos, que corresponde a
> historias pendientes.

## Tarea transversal: traducción BDD a pruebas automatizadas JUnit 5

**ID:** T-TEST-02
**Historias:** HU-03 y HU-04
**Calidad:** fiabilidad, adecuación funcional y capacidad de ser probado.

### Escenarios BDD del backlog

- **HU-03**
  - **Given** al menos 50 transacciones de un producto en las últimas 24 horas.
  - **When** un usuario solicita el promedio de ese producto.
  - **Then** la API retorna `200 OK` con la media aritmética en COP y el número de transacciones utilizadas.
- **HU-04**
  - **Given** un comerciante consulta el catálogo con municipio y categoría.
  - **When** solicita los lotes que coinciden con esos filtros.
  - **Then** la API retorna únicamente lotes activos coincidentes con disponibilidad y precio.

### Cobertura de pruebas

- HU-03: umbral de 50 registros, promedio, ventana temporal, aislamiento por
  producto, registro autenticado, validación de datos y rechazo de solicitudes
  sin JWT. La consulta con menos de 50 registros responde `422 Unprocessable
  Entity`.
- HU-04: filtros combinados y por separado, comparación de categoría sin
  distinguir mayúsculas, exclusión de lotes inactivos, compatibilidad con lotes
  sin categoría y rechazo de solicitudes sin filtros.
- Las pruebas de integración específicas se mantienen en
  `Hu03PreciosPromedioIntegrationTest` y
  `Hu04FiltroProductosIntegrationTest`.

## Criterios de terminación del sprint

- Los escenarios de aceptación de HU-03 y HU-04 están cubiertos por pruebas
  automatizadas y todas las pruebas pasan.
- La compilación, Checkstyle y el flujo de verificación del proyecto pasan
  mediante `./mvnw clean verify`.
- La cobertura cumple el mínimo del DoD: 60% en instrucciones, ramas y líneas,
  verificada con JaCoCo.
- Los cambios se integran mediante Pull Request con revisión por pares y
  commits bajo Conventional Commits, conforme al DoD.
- Se actualizan el Backlog y la documentación relacionada cuando el alcance o
  el comportamiento implementado lo requiere.

## Referencias de alcance

- [Product Backlog](../BACKLOG.md), que define prioridad, estimación y criterios
  BDD de HU-03 y HU-04.
- [HU-03: precios promedio regionales](sprint-2-hu03.md), que detalla contrato
  REST, ventana temporal, umbral y persistencia.
- [HU-04: filtro por municipio y categoría](sprint-2-hu04.md), que detalla
  filtros, compatibilidad y validaciones.
- [Definition of Done](dod.md), que define los criterios generales de calidad
  del equipo.
