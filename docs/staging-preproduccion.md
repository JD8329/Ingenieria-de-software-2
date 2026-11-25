# Base de datos de staging / preproducción

## Decisión y estado

La aplicación de staging se desplegará en Render y usará PostgreSQL en Neon,
con la base `agrovalle_staging`. El usuario confirmó que creó la base, guardó
`STAGING_DB_URL` como secreto de Actions y obtuvo `agrovalle_staging` al ejecutar
`SELECT current_database();`.

El repositorio ya contiene un Dockerfile, un Blueprint de Render y un job de
despliegue condicionado en GitHub Actions. Aún falta crear/sincronizar el
servicio externo en Render, configurar allí las variables de entorno y guardar
el deploy hook en GitHub. Por ello, todavía no se afirma que la aplicación esté
desplegada ni que el endpoint de salud responda.

## Esquema que usará la aplicación

Se eligió el esquema actual definido por las entidades JPA de la aplicación.
Hibernate lo crea/actualiza al iniciar mediante `ddl-auto=update`; no se debe
ejecutar `docs/diseno/schema.sql` ni el `ALTER TABLE` del modelo académico en
esta base. Ese DDL de cinco tablas (`municipio`, `agricultor`, `producto`,
`cosecha` y `oferta`) es un modelo de referencia separado, no una migración
compatible con las entidades actuales.

`ddl-auto=update` no sustituye un sistema de migraciones ni garantiza
migraciones reversibles. Usa staging para pruebas, no como almacenamiento de
datos permanentes; no ingreses datos personales o credenciales reales.

## Configuración en Render

Después de integrar estos cambios en `main`:

1. En Render, crea un **Blueprint** y conecta el repositorio
   `JD8329/Ingenieria-de-software-2`, usando el `render.yaml` de la rama `main`.
   Revisa el plan y sincroniza el servicio `agrovalle-staging`. El Blueprint
   configura Docker y deja desactivados los despliegues automáticos por commit.
2. En las variables del servicio, configura `SPRING_DATASOURCE_URL` con el URL
   JDBC de Neon para `agrovalle_staging`, por ejemplo
   `jdbc:postgresql://<host>:5432/agrovalle_staging?sslmode=require&channel_binding=require`.
   No incluyas `user` ni `password` en ese URL: configura `DB_USER` y
   `DB_PASSWORD` como variables separadas usando el rol y la contraseña de Neon.
   Usa la conexión pooled de Neon si está disponible y mantén TLS (`sslmode=require`).
3. El Blueprint genera `JWT_SECRET` en Render. No copies ese valor al repositorio
   ni a los logs. También configura Hibernate con `ddl-auto=update` y desactiva
   el SQL detallado para este entorno.
4. Si se van a probar recuperación de contraseña, configura en Render las
   variables SMTP (`MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`,
   `MAIL_SMTP_AUTH`, `MAIL_SMTP_STARTTLS`, `MAIL_FROM`) y `PASSWORD_RESET_URL`.
   Sin un SMTP válido, el envío de correos no está preparado para staging.
5. En la configuración del servicio Render, crea un **Deploy Hook**. En GitHub,
   abre **Settings → Secrets and variables → Actions → New repository secret**
   y guarda su URL como `RENDER_DEPLOY_HOOK_URL`.
6. Después de verificar que el servicio, las variables y el hook están listos,
   crea la variable de repositorio `STAGING_DEPLOY_ENABLED` con valor `true` en
   **Settings → Secrets and variables → Actions → Variables**. Mientras no esté
   habilitada, el job de despliegue queda omitido.
7. Tras un push a `main`, confirma que `build-and-test` finalice correctamente,
   que `deploy-staging` invoque el hook y que Render muestre el nuevo deploy
   como exitoso. Verifica el health check `GET /api/v1/productos` en la URL
   pública del servicio. Los Pull Requests no despliegan.

El valor de la URL JDBC no debe copiarse a documentos, capturas, commits o
conversaciones. No se imprimen secretos en Actions.

## Qué hace cada secreto

- `STAGING_DB_URL` es el secreto de Actions que el usuario ya creó para el
  ejercicio. GitHub no lo copia automáticamente a Render y el job de deploy no
  lo consume: el proceso de aplicación obtiene conexión de las variables
  configuradas directamente en Render.
- `RENDER_DEPLOY_HOOK_URL` es el secreto que usa Actions para pedirle a Render
  un despliegue. Su URL autoriza a disparar el deploy, así que no debe
  publicarse.
- `JWT_SECRET` lo genera y guarda Render para la aplicación. La variable
  `STAGING_DEPLOY_ENABLED` no es un secreto; es el interruptor de activación del
  job.

## Lista de verificación

- [x] Base `agrovalle_staging` creada en Neon y conexión confirmada por el usuario.
- [x] Esquema acordado: entidades JPA actuales; no ejecutar el DDL académico.
- [x] Secreto `STAGING_DB_URL` creado por el usuario en GitHub Actions.
- [x] Dockerfile, Blueprint y deploy job agregados al repositorio.
- [ ] Blueprint sincronizado y servicio `agrovalle-staging` creado en Render.
- [ ] Variables de conexión configuradas en Render.
- [ ] Secreto `RENDER_DEPLOY_HOOK_URL` y variable `STAGING_DEPLOY_ENABLED=true` creados.
- [ ] Primer deploy exitoso y endpoint de salud verificado.
