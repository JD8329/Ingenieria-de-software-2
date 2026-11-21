# Sprint 8 — HU-15: Recuperación de contraseña

## Historia de usuario

Como usuario registrado, quiero recuperar mi contraseña por correo para no
perder el acceso a mi cuenta.

## Alcance y criterios de aceptación

- La solicitud se realiza en `POST /api/v1/auth/recuperar-password` con el
  correo registrado en una cuenta de agricultor o comerciante.
- La respuesta es `202 Accepted` y no revela si el correo está registrado.
- Si existe la cuenta, se genera un token aleatorio de un solo uso y se envía
  un enlace de restablecimiento por correo.
- El token vence a los 30 minutos. En la base de datos solo se conserva su hash
  SHA-256; una nueva solicitud invalida los enlaces anteriores de esa cuenta.
- `POST /api/v1/auth/restablecer-password` recibe el token y la nueva contraseña.
  Con un token válido actualiza la contraseña mediante BCrypt y responde
  `204 No Content`.
- Un token desconocido, vencido o ya utilizado responde `400 Bad Request` con
  un mensaje genérico. Las contraseñas nuevas deben tener al menos 8 caracteres.
- Los tokens expirados se eliminan durante la siguiente solicitud de recuperación
  o intento de restablecimiento.

## Contratos de API

### Solicitar recuperación

`POST /api/v1/auth/recuperar-password`

```json
{
  "correo": "usuario@example.com"
}
```

Respuesta `202 Accepted`, tanto para cuentas existentes como inexistentes:

```json
{
  "mensaje": "Si el correo está asociado a una cuenta, recibirás instrucciones para recuperar tu contraseña."
}
```

El envío se programa después de confirmar la transacción de persistencia y se
procesa de forma asíncrona para mantener una respuesta uniforme. Si el servidor
SMTP no puede entregar el mensaje, se registra el error sin escribir el correo
ni el token en los logs; el usuario puede solicitar un enlace nuevo.

### Restablecer contraseña

`POST /api/v1/auth/restablecer-password`

```json
{
  "token": "<token recibido en el enlace>",
  "nuevaContrasena": "nueva-clave-segura"
}
```

La URL del formulario receptor del enlace se configura con
`PASSWORD_RESET_URL`. El token se añade como parámetro `token`; la interfaz debe
enviarlo al endpoint de restablecimiento junto con la nueva contraseña.

## Configuración SMTP

No se almacenan credenciales de correo en el repositorio. La instancia debe
configurar estas variables de entorno:

| Variable | Uso | Valor local predeterminado |
|---|---|---|
| `MAIL_HOST` | Host SMTP | `localhost` |
| `MAIL_PORT` | Puerto SMTP | `25` |
| `MAIL_USERNAME` | Usuario SMTP, si el servidor requiere autenticación | Vacío |
| `MAIL_PASSWORD` | Contraseña SMTP, si el servidor requiere autenticación | Vacío |
| `MAIL_SMTP_AUTH` | Habilita autenticación SMTP | `false` |
| `MAIL_SMTP_STARTTLS` | Habilita STARTTLS | `false` |
| `MAIL_FROM` | Dirección remitente | `no-reply@agrovalle.local` |
| `PASSWORD_RESET_URL` | URL del formulario web que recibe el token | `http://localhost:3000/restablecer-password` |

Las credenciales deben gestionarse mediante el entorno de despliegue o un
gestor de secretos, nunca dentro de `application.properties` versionado.

## Verificación

Las pruebas de integración cubren el envío del enlace, respuesta no enumerable,
expiración, uso único y cambio de contraseña para agricultores y comerciantes.
La verificación completa se ejecutó con
`.\mvnw.cmd --batch-mode --no-transfer-progress clean install`: 88 pruebas
aprobadas, Checkstyle sin violaciones y umbrales JaCoCo satisfechos. En una
ejecución local con H2, la solicitud de recuperación respondió `202` y el uso de
un token desconocido respondió `400`.
