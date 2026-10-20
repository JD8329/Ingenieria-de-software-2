# Sprint 8 — HU-14 y HU-15

## Objetivo

Implementar el perfil público del agricultor y la recuperación segura de
contraseña para cuentas de agricultores y comerciantes.

**Alcance:** HU-14 (3 puntos) y HU-15 (5 puntos), 8 puntos de historia.

## HU-14 — Perfil público del agricultor

`GET /api/v1/agricultores/{id}/perfil` está disponible sin autenticación y
retorna el nombre, ubicación de la finca, municipio, calificación promedio y
lotes activos del agricultor. No expone cédula, correo ni teléfono. La
calificación se calcula a partir de los pedidos calificados; si aún no hay
calificaciones, retorna `0.0`. Los lotes se presentan ordenados por
identificador. Un agricultor inexistente responde `404 Not Found`.

## HU-15 — Recuperación de contraseña

`POST /api/v1/auth/recuperar-password` recibe `{ "correo": "..." }`. Retorna
`202 Accepted` tanto si el correo está registrado como si no, evitando revelar
la existencia de cuentas. Para una cuenta registrada, genera un token aleatorio
de 256 bits, guarda únicamente su hash SHA-256 y envía el enlace al correo.
Solicitar otro enlace invalida el token anterior de esa cuenta.

El enlace agrega el parámetro `token` a la URL configurada en
`PASSWORD_RESET_BASE_URL`. El token expira en 30 minutos y solo puede usarse
una vez. `POST /api/v1/auth/restablecer-password` recibe
`{ "token": "...", "nuevaContrasena": "..." }`; una contraseña de al menos
ocho caracteres se cifra con el codificador de contraseñas de la aplicación y
el endpoint responde `204 No Content`. Un token vencido, inválido o ya usado
responde `400 Bad Request`.

### Configuración de correo

Definir estas variables de entorno en el despliegue; no guardar credenciales
SMTP en el repositorio:

| Variable | Uso |
|---|---|
| `SMTP_HOST` | Servidor SMTP |
| `SMTP_PORT` | Puerto SMTP (por defecto `1025`) |
| `SMTP_USERNAME` | Usuario SMTP, si el servidor requiere autenticación |
| `SMTP_PASSWORD` | Contraseña SMTP, si el servidor requiere autenticación |
| `SMTP_AUTH` | Habilita autenticación SMTP (`false` por defecto) |
| `SMTP_STARTTLS` | Habilita STARTTLS (`false` por defecto) |
| `PASSWORD_RESET_BASE_URL` | URL de la pantalla que recibe el token (por defecto `http://localhost:3000/restablecer-password`) |
| `PASSWORD_RESET_SENDER` | Dirección remitente (por defecto `no-reply@agrovalleconnect.local`) |

Las solicitudes de cuentas válidas requieren que el servidor SMTP esté
disponible. Si el envío falla, la solicitud falla explícitamente y no se
confirma como aceptada.

## Verificación

La prueba de integración `Sprint8ApiIntegrationTest` verifica el contenido no
sensible del perfil, calificación y filtro de lotes activos; la respuesta para
agricultores inexistentes; recuperación de cuentas de ambos tipos; respuesta
genérica para correos desconocidos; almacenamiento no reversible del token;
actualización de la contraseña; y rechazo de tokens vencidos o reutilizados.

```text
./mvnw "-Dtest=Sprint8ApiIntegrationTest" test
```

La verificación técnica automatizada no representa aprobación formal por
pares; esta debe registrarse mediante el proceso de Pull Request del equipo.
