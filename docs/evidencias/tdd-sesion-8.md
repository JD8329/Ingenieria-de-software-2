# Evidencia TDD — rechazo de lotes duplicados

## Regla de negocio y cambios relacionados

Un agricultor no puede publicar un segundo lote activo con el mismo producto
(comparado sin distinguir mayúsculas y minúsculas) y la misma fecha de cosecha.
La regla no bloquea la publicación de ese producto en otra fecha. La API
responde `409 Conflict` y no persiste un segundo lote.

La implementación y sus pruebas están en:

- Regla de producción: `src/main/java/co/uniajc/agrovalle/agrovalleconnect/services/LoteCosechaService.java`,
  método `publicarLote`.
- Consulta de existencia: `src/main/java/co/uniajc/agrovalle/agrovalleconnect/repositories/LoteCosechaRepository.java`,
  método `existsByAgricultorIdAndTipoProductoIgnoreCaseAndFechaCosechaAndActivoTrue`.
- Prueba unitaria aislada con Mockito: `src/test/java/co/uniajc/agrovalle/agrovalleconnect/services/LoteCosechaServiceTest.java`,
  método `publicarLoteDuplicadoDelMismoAgricultorProductoYFecha_LanzaConflicto`.
- Prueba de integración REST con MockMvc: `src/test/java/co/uniajc/agrovalle/agrovalleconnect/Sprint1ApiIntegrationTest.java`,
  método `publicarLoteDuplicadoParaMismoAgricultorProductoYFecha_Retorna409`.

La prueba unitaria configura la consulta como duplicada, espera `ConflictoException`
y verifica que no se guarde el lote ni se publique el evento. La prueba MockMvc
comprueba el `409` HTTP y que el conteo de lotes permanezca en uno.

## Reproducción RED hecha para esta evidencia

El historial remoto no conserva una ejecución RED original. El PR
[#23](https://github.com/JD8329/Ingenieria-de-software-2/pull/23) fue integrado
en un único commit (`83662719c3c852ffac3315db72246869d2309ea1`); por lo tanto, no
hay una secuencia histórica de commits RED, GREEN y REFACTOR que pueda
presentarse como registro contemporáneo.

Para comprobar la regresión ahora, se creó un worktree temporal y aislado en el
commit padre de ese cambio (`d85cb589d1ce8b329e31ccce57d3d37785984d2f`), que es
antecesor directo del commit del PR. Allí se usó la prueba unitaria de PR #23 y
se añadió únicamente la firma de consulta del repositorio requerida para
compilar esa prueba. El servicio de producción permaneció en su versión
anterior, que guarda el lote sin comprobar duplicidad.

Comando ejecutado desde ese worktree:

```powershell
.\mvnw.cmd --batch-mode --no-transfer-progress '-Dtest=LoteCosechaServiceTest#publicarLoteDuplicadoDelMismoAgricultorProductoYFecha_LanzaConflicto' test
```

Resultado observado en la versión anterior del servicio:

```text
Tests run: 1, Failures: 1, Errors: 0, Skipped: 0
Expected ConflictoException to be thrown, but nothing was thrown.
BUILD FAILURE
```

Esta ejecución fallida es una reproducción actual en un entorno aislado, no
evidencia de que el equipo guardara esa salida durante el desarrollo original.

## GREEN comprobado en la implementación integrada

Con el código actual, en la raíz del proyecto:

```powershell
.\mvnw.cmd --batch-mode --no-transfer-progress '-Dtest=LoteCosechaServiceTest#publicarLoteDuplicadoDelMismoAgricultorProductoYFecha_LanzaConflicto' test
```

```text
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Y la prueba de extremo a extremo HTTP:

```powershell
.\mvnw.cmd --batch-mode --no-transfer-progress '-Dtest=Sprint1ApiIntegrationTest#publicarLoteDuplicadoParaMismoAgricultorProductoYFecha_Retorna409' test
```

```text
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Verificación completa del proyecto:

```powershell
.\mvnw.cmd --batch-mode --no-transfer-progress clean verify
```

```text
Tests run: 41, Failures: 0, Errors: 0, Skipped: 0
You have 0 Checkstyle violations.
All coverage checks have been met.
BUILD SUCCESS
```

El PR #23 registra la integración de las pruebas y la regla de producción. Su
[ejecución de GitHub Actions](https://github.com/JD8329/Ingenieria-de-software-2/actions/runs/39514340093)
terminó exitosamente.

## Cobertura: evidencia distinta

JaCoCo mide cobertura y verifica umbrales, pero no registra el orden temporal
de escritura de una prueba, fallo esperado, implementación y refactor. Por eso
sus porcentajes no se presentan como evidencia del ciclo TDD. La cobertura
general de la verificación completa se reporta por separado en
[`../sprint-2-review.md`](../sprint-2-review.md).
