# Taller de la Sesión 10 — Integración Continua con GitHub Actions

## Objetivo

Verificar automáticamente el estilo, las pruebas y la cobertura del proyecto
en GitHub Actions al actualizar una rama de funcionalidad o abrir un Pull
Request hacia `main` o `develop`.

## Pipeline

El workflow [`.github/workflows/ci.yml`](../.github/workflows/ci.yml) ejecuta
Java 17 en `ubuntu-latest` y define tres pasos de calidad:

1. `./mvnw --batch-mode --no-transfer-progress checkstyle:check`
2. `./mvnw --batch-mode --no-transfer-progress test`
3. `./mvnw --batch-mode --no-transfer-progress jacoco:check@check-coverage`

El sufijo `@check-coverage` selecciona la ejecución que contiene las reglas de
cobertura definidas en `pom.xml`; ejecutar `jacoco:check` sin ese identificador
invoca el goal sin esas reglas y no verifica el umbral del proyecto.

JaCoCo está configurado en `pom.xml` para exigir al menos 60 % en instrucciones,
ramas y líneas.

## Simulación de fallo y corrección

La simulación se realizó en la rama aislada `taller/sesion10-ci`. En
`LoteCosechaServiceTest`, el test `publicaLoteYEmiteEventoDeDominio` se cambió
temporalmente para esperar que el lote quedara inactivo, aunque la publicación
válida lo deja activo. JUnit falló por esa aserción deliberada; no era un fallo
de producción.

Después se restauró la expectativa correcta,
`assertTrue(resultado.getActivo())`, y se repitieron los checks. El PR #30 fue
aprobado y fusionado en `main` el 30/10/2026.

| Evidencia | Resultado |
|---|---|
| [CI rojo del push inicial](https://github.com/JD8329/Ingenieria-de-software-2/actions/runs/40921751574) | JUnit: 41 pruebas, 1 fallo deliberado; Checkstyle pasó; JaCoCo se omitió al fallar JUnit. |
| [CI rojo en el PR](https://github.com/JD8329/Ingenieria-de-software-2/actions/runs/41065445196) | Reproduce el fallo deliberado en la verificación del Pull Request. |
| [CI verde después de corregir](https://github.com/JD8329/Ingenieria-de-software-2/actions/runs/41068381092) | Los checks del Pull Request finalizaron exitosamente. |
| [PR #30](https://github.com/JD8329/Ingenieria-de-software-2/pull/30) | Aprobado y fusionado; la versión final conserva la aserción correcta. |

La práctica muestra el ciclo de detección y corrección dentro del CI; no debe
dejarse la aserción invertida en la versión integrada.

## Estado de la práctica

La simulación está completada y tiene evidencia del resultado fallido inicial,
del resultado exitoso tras la corrección y de la integración del PR #30. El
workflow no despliega a staging; la preparación de ese entorno se describe en
[Base de datos staging / preproducción](staging-preproduccion.md).
