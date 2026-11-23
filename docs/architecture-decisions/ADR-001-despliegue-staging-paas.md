# ADR-001: Despliegue de AgroValle Connect en staging

- **Estado:** Propuesta pendiente de validación por el equipo y la docente
- **Fecha:** 2026-11-22
- **Alcance:** topología de despliegue para preproducción

## Contexto

La guía de la Sesión 13 solicita representar una topología de staging con una
aplicación Java y PostgreSQL administrado. El repositorio configura una
aplicación Spring Boot para ejecución local y un workflow de CI, pero no
contiene un proveedor PaaS elegido, un workflow de despliegue, un perfil
`application-staging.yml` ni una conexión de CI a `STAGING_DB_URL`.

Además, `docs/diseno/schema.sql` es un esquema académico de referencia y no
debe asumirse como migración compatible con el esquema JPA actual.

## Decisión propuesta

No se declara seleccionado ni desplegado ningún proveedor de staging. Hasta
que el equipo y la docente confirmen proveedor, esquema y responsable, se
mantendrá el diagrama de despliegue como objetivo propuesto y no se conectará CI
ni la aplicación a una base remota.

## Alternativas por evaluar

- PaaS de aplicación y PostgreSQL administrado en un solo proveedor.
- Aplicación y base de datos en proveedores distintos, con conexión TLS.
- Mantener solo ejecución local y CI hasta que exista una necesidad y
  autorización explícitas para staging.

No se elige entre Render, Railway, Neon, Supabase u otro proveedor en este ADR:
esa selección requiere una decisión real del equipo y revisar los límites,
disponibilidad y credenciales del entorno.

## Consecuencias

- El CI sigue limitado a compilación, Checkstyle, pruebas y JaCoCo.
- Crear el secreto `STAGING_DB_URL` no activa por sí mismo conexión ni
  despliegue.
- Antes de desplegar, se debe acordar el esquema de base de datos compatible,
  las migraciones, la gestión de secretos, TLS y el flujo de aprobación.
- El estado de este ADR debe cambiarse solo cuando el equipo registre una
  decisión aprobada y exista evidencia de la configuración implementada.

## Referencias

- [Sesión 13: diagramas UML](../architecture/diagramas-uml.md)
- [Guía de staging / preproducción](../staging-preproduccion.md)
- [Esquema académico de referencia](../diseno/schema.sql)
