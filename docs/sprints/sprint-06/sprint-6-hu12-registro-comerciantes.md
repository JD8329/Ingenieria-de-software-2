# Sprint 6 — HU-12: Registro de comerciantes

## Alcance

Se incorpora el registro de comerciantes urbanos con cuenta y datos de negocio
propios, sin reutilizar los campos agrícolas. La entidad `Comerciante` guarda
razón social, NIT, correo, contraseña codificada con BCrypt, teléfono, dirección
y municipio. El municipio `CALI` queda disponible para cuentas comerciales.

El correo se valida entre agricultores y comerciantes para evitar cuentas
ambiguas. El NIT es único entre comerciantes. Las respuestas nunca exponen la
contraseña.

## Criterios de aceptación

- `POST /api/v1/auth/register` acepta los datos obligatorios y responde
  `201 Created` con el rol `COMERCIANTE`.
- Campos ausentes o inválidos responden `400 Bad Request`.
- Un correo ya usado por agricultor o comerciante, o un NIT existente, responde
  `409 Conflict`.
- La contraseña se persiste codificada y no aparece en la respuesta.
- Las credenciales de un comerciante permiten iniciar sesión en
  `POST /api/v1/auth/login`; el JWT identifica el rol `COMERCIANTE`.
- Un token de comerciante no autoriza las operaciones reservadas a
  `PRODUCCION`, como publicar lotes y registrar precios.

## Contrato de registro

`POST /api/v1/auth/register`

```json
{
  "razonSocial": "Mercado del Valle S.A.S.",
  "nit": "900123456-7",
  "correo": "compras@mercado.example",
  "contrasena": "clave-segura",
  "telefono": "3001234567",
  "direccion": "Calle 10 # 20-30",
  "municipio": "CALI"
}
```

La respuesta `201 Created` incluye `id`, razón social, NIT, correo, teléfono,
dirección, municipio y rol. No incluye contraseña.

## Verificación

Las pruebas de integración cubren persistencia, codificación BCrypt, respuesta
sin contraseña, validación de datos obligatorios, NIT duplicado, correo
compartido con una cuenta agricultora, inicio de sesión, rol del JWT,
autorización de lotes y rechazo de contraseñas incorrectas.

```powershell
.\mvnw.cmd -Dtest=Sprint6ComercianteIntegrationTest test
```

Las siete pruebas de HU-12 aprobaron. La verificación completa del incremento
se ejecutó con `.\mvnw.cmd --batch-mode --no-transfer-progress clean install`:
71 pruebas aprobadas, Checkstyle sin violaciones y umbrales JaCoCo satisfechos.
La prueba en ejecución real de la API confirmó registro `201`, login `200` y
rechazo de publicación por cuenta comerciante `403`.
