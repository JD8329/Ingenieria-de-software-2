# Base de datos de staging / preproducción

## Propósito

Staging es una base de datos separada para ensayar una configuración antes de
producción. No se deben copiar datos personales o credenciales reales a este
entorno. Usa credenciales propias y mantenlas fuera del repositorio.

## Preparación solicitada para el ejercicio

1. En el proveedor PostgreSQL del equipo, crea una base nueva llamada
   `agrovalle_staging` (por ejemplo, desde el panel de Neon, Render o Supabase).
   En servicios administrados, crea la base desde el panel si el proveedor no
   permite `CREATE DATABASE` en su consola SQL.
2. Copia la cadena JDBC de esa base. En general tiene esta forma:
   `jdbc:postgresql://<host>:<puerto>/agrovalle_staging?sslmode=require`.
   Usa exactamente el host, puerto, usuario, contraseña y parámetros que indique
   el proveedor. La cadena puede contener credenciales: no la publiques ni la
   agregues a `application.properties`.
3. Antes de ejecutar `docs/diseno/schema.sql`, revisa la advertencia siguiente
   y confirma que ese es el esquema que solicita el ejercicio. Ejecuta el DDL
   únicamente contra la base staging vacía, nunca contra producción.
4. En GitHub, abre **Settings → Secrets and variables → Actions → New
   repository secret** y crea el secreto `STAGING_DB_URL` con la URL JDBC. No
   compartas su valor en capturas, commits, tickets públicos ni conversaciones.

## Importante: modelo de referencia frente al backend

`docs/diseno/schema.sql` crea el modelo relacional normalizado de referencia
de cinco tablas (`municipio`, `agricultor`, `producto`, `cosecha` y `oferta`).
La aplicación actual evolucionó y persiste otras entidades mediante JPA, por
lo que ese DDL no es una migración automática ni necesariamente coincide con el
esquema que espera ejecutar el backend.

Antes de cargar datos o conectar la aplicación, acuerda con la profesora si el
objetivo es demostrar el esquema académico de referencia o desplegar el
esquema requerido por la aplicación. No combines ambos modelos sin diseñar y
probar una migración explícita.

## Alcance actual de GitHub Actions

El workflow [`../.github/workflows/ci.yml`](../.github/workflows/ci.yml)
ejecuta Checkstyle, JUnit y JaCoCo. En su configuración actual no referencia
`STAGING_DB_URL`, no despliega la aplicación y no ejecuta `schema.sql`.
Guardar el secreto es solo preparar la credencial; no configura por sí mismo
una conexión, despliegue o prueba contra staging.

Para usarlo en un paso futuro, una persona autorizada debe modificar el workflow
para inyectar el secreto al paso estrictamente necesario, sin imprimirlo en los
logs, y configurar también el resto de parámetros de conexión que requiera la
aplicación. No se debe ejecutar DDL contra producción desde CI.

## Lista de verificación del equipo

- [ ] Base separada `agrovalle_staging` creada en el proveedor acordado.
- [ ] Modelo que se desplegará confirmado (DDL académico o esquema de la app).
- [ ] DDL revisado y ejecutado solo en la base staging, si corresponde.
- [ ] `STAGING_DB_URL` agregado como secreto de Actions sin exponer su valor.
- [ ] Workflow actualizado y revisado antes de afirmar que CI usa staging.
- [ ] Conexión probada sin mostrar credenciales en logs ni evidencias.
