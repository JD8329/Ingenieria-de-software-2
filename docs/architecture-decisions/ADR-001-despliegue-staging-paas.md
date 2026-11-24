# ADR-001: Despliegue de AgroValle Connect en staging

- **Estado:** Aceptado; configuración externa pendiente
- **Fecha:** 2026-11-23
- **Alcance:** topología de despliegue para preproducción

## Contexto

El proyecto requiere un entorno de staging para la aplicación Spring Boot y una
base PostgreSQL independiente. El responsable eligió Render para la aplicación,
Neon para PostgreSQL y el esquema que ya definen las entidades JPA. La base
`agrovalle_staging` fue creada y su nombre se confirmó desde la consola de Neon.

El DDL en `docs/diseno/schema.sql` pertenece al modelo académico normalizado de
referencia y no representa una migración compatible con las entidades actuales.

## Decisión

- Empaquetar la aplicación Java 17 como imagen Docker y desplegarla en un
  servicio web de Render definido en `render.yaml`.
- Conectar el servicio a Neon mediante TLS, guardando el URL JDBC, usuario y
  contraseña en las variables privadas del servicio Render.
- Usar el esquema JPA existente con `ddl-auto=update` en staging. No ejecutar
  `schema.sql` ni combinar ambos modelos.
- Desactivar el auto-deploy de Render por commit. GitHub Actions puede invocar
  el deploy hook únicamente después de que `build-and-test` pase en un push a
  `main` y cuando `STAGING_DEPLOY_ENABLED=true`.
- Mantener `STAGING_DB_URL` y `RENDER_DEPLOY_HOOK_URL` como secretos de Actions;
  la aplicación recibe sus variables de runtime directamente en Render. El JWT
  se genera en Render.

## Alternativas consideradas

- Hospedar aplicación y base PostgreSQL en un único proveedor.
- Usar proveedores separados sin un deploy hook condicionado por el CI.
- Mantener solo ejecución local y CI, sin staging.

## Consecuencias y estado

- La configuración de aplicación y CI está versionada; el servicio Render,
  sus variables y el deploy hook todavía deben configurarse en los paneles.
- No se afirma un despliegue exitoso hasta verificar un run real y el health
  check `GET /api/v1/productos`.
- `ddl-auto=update` facilita inicializar staging, pero no es un sistema de
  migraciones; no debe considerarse adecuado para migraciones de producción.
- La integración de correo requiere configurar por separado un SMTP válido.
- El deploy job queda omitido hasta habilitar `STAGING_DEPLOY_ENABLED` en GitHub.

## Referencias

- [Sesión 13: diagramas UML](../architecture/diagramas-uml.md)
- [Guía de staging / preproducción](../staging-preproduccion.md)
- [Esquema académico de referencia](../diseno/schema.sql)
