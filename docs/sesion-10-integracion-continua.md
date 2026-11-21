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

La simulación se hace en la rama aislada `taller/sesion10-ci`, nunca en `main`
ni en la rama del PR #27. En `LoteCosechaServiceTest`, el test
`publicaLoteYEmiteEventoDeDominio` contiene temporalmente una aserción invertida:
espera que el lote quede inactivo, aunque la publicación válida lo deja activo.
El paso JUnit debe fallar y GitHub Actions debe marcar el check del PR en rojo.

Después de observar el fallo, se corrige la aserción a
`assertTrue(resultado.getActivo())`, se vuelven a ejecutar los controles y se
actualiza la misma rama. Los checks deben quedar verdes antes de solicitar la
aprobación por pares.

La protección de `main` no se puede demostrar mientras no exista una regla
remota configurada. Sin ella, GitHub puede permitir fusionar un PR con checks
fallidos; por seguridad, el equipo no debe fusionar el PR de la simulación
mientras esté rojo. Un administrador debe exigir Pull Request, aprobación de
pares y el status check `build-and-test` en la rama `main`.

## Estado de la práctica

La simulación se considera completada cuando se observan y guardan evidencias
de ambos resultados en el PR: una ejecución fallida por la prueba deliberada y
una ejecución exitosa después de corregirla. La rama solo se fusiona cuando la
versión final contiene la aserción correcta y todos los checks están verdes. Si
se conserva el historial de commits, este puede mostrar tanto el fallo
controlado como su corrección.
