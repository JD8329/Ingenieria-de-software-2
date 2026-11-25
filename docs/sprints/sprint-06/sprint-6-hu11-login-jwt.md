# Sprint 6 — HU-11: Inicio de sesión con JWT

## Alcance

Se conserva el inicio de sesión existente y se incluye el rol de la cuenta en el
JWT. Los endpoints de publicación de lotes y registro de transacciones de precio
requieren el rol `PRODUCCION`; estar autenticado ya no es suficiente para
ejecutarlos.

El registro de comerciantes y su acceso al inicio de sesión se documentan en la
entrega de HU-12.

## Criterios de aceptación

- Credenciales válidas generan un JWT firmado con tipo `Bearer` y vigencia de
  una hora.
- El token incluye el rol persistido de la cuenta en la propiedad `rol`.
- Credenciales incorrectas responden `401 Unauthorized` y no generan token.
- Publicar lotes y registrar transacciones requiere un token con rol
  `PRODUCCION`.
- Los endpoints públicos conservan su acceso sin autenticación.

## Contrato de inicio de sesión

`POST /api/v1/auth/login`

```json
{
  "correo": "productor@example.com",
  "contrasena": "clave-segura"
}
```

Respuesta `200 OK`:

```json
{
  "accessToken": "<JWT>",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

El JWT mantiene `iss` y `sub`, y ahora incluye `rol`. La contraseña continúa
verificándose contra el hash BCrypt almacenado; el token no contiene
credenciales.

## Verificación

La prueba de integración valida que las credenciales correctas producen un
token cuyo rol es `PRODUCCION`, y que las incorrectas responden 401. Las pruebas
de publicación verifican además que el rol se interpreta desde el token al
autorizar la operación.

```powershell
.\mvnw.cmd "-Dtest=Sprint1ApiIntegrationTest,LoteCosechaControllerTest" test
```

Resultado focalizado: 21 pruebas aprobadas, sin fallos ni errores. La verificación
completa del incremento HU-11/HU-12 también pasó con `clean install`: 71 pruebas,
Checkstyle sin violaciones y umbrales JaCoCo satisfechos.
