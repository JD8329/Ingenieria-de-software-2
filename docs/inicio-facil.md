# Cómo iniciar AgroValle Connect

Esta guía explica paso a paso cómo abrir AgroValle Connect en Windows. El
frontend y el backend están en el mismo proyecto: al iniciar el servidor, la
interfaz web también queda disponible. No necesitas instalar Node.js ni
ejecutar un programa adicional para el frontend.

## 1. Qué necesitas

Instala estos programas antes de empezar:

1. **Java JDK 17** o una versión compatible. Permite ejecutar el servidor.
2. **PostgreSQL** y **pgAdmin**. PostgreSQL guarda las cuentas y las cosechas;
   pgAdmin facilita crear la base de datos.
3. **Git**, si aún no tienes una copia del proyecto en tu computador.

No necesitas instalar Maven: el proyecto incluye `mvnw.cmd`, que descarga y
ejecuta Maven por ti. Tampoco necesitas instalar Node.js para usar la interfaz.

## 2. Preparar la base de datos (solo la primera vez)

1. Abre **pgAdmin**.
2. En la columna izquierda, abre tu servidor PostgreSQL. Si lo solicita,
   ingresa la contraseña que elegiste al instalar PostgreSQL.
3. Haz clic derecho en **Databases** (o **Bases de datos**) y selecciona
   **Create > Database...**.
4. Escribe `agrovalle_connect` como nombre y guarda.

La aplicación crea y actualiza las tablas al iniciar. No tienes que crear
tablas manualmente.

## 3. Abrir PowerShell en la carpeta del proyecto

1. Abre la carpeta del proyecto en el Explorador de archivos.
2. Haz clic en la barra donde aparece la dirección de la carpeta.
3. Escribe `powershell` y presiona **Enter**.

Se abrirá PowerShell en la carpeta correcta. Si aún no tienes el proyecto,
clónalo desde el repositorio antes de continuar.

## 4. Configurar la base de datos y la clave de seguridad

En PowerShell, copia y pega este bloque. Reemplaza
`ESCRIBE_AQUI_TU_CLAVE_DE_POSTGRES` por la contraseña elegida al instalar
PostgreSQL:

```powershell
$env:DB_NAME = "agrovalle_connect"
$env:DB_USER = "postgres"
$env:DB_PASSWORD = "ESCRIBE_AQUI_TU_CLAVE_DE_POSTGRES"

$bytes = New-Object byte[] 48
$generador = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$generador.GetBytes($bytes)
$env:JWT_SECRET = [Convert]::ToBase64String($bytes)
$generador.Dispose()
```

Si tu usuario de PostgreSQL no es `postgres`, cambia `DB_USER`. El bloque
genera automáticamente la clave JWT; no tienes que inventarla ni guardarla en
un archivo. Si cierras PowerShell, tendrás que volver a ejecutar este bloque la
próxima vez.

## 5. Iniciar el servidor y el frontend

En la misma ventana de PowerShell, ejecuta:

```powershell
.\mvnw.cmd spring-boot:run
```

La primera vez puede tardar unos minutos mientras Maven descarga los
componentes necesarios. Deja abierta la ventana. Cuando Spring Boot indique
que la aplicación inició, abre el navegador y visita:

**http://localhost:8080**

Esa dirección abre el frontend. El backend recibe las solicitudes del frontend
desde la misma dirección, así que no hace falta ejecutar dos programas ni
configurar la red.

## 6. Crear una cuenta y probar la aplicación

1. En la pantalla inicial, selecciona **Regístrate aquí**.
2. Completa los datos de agricultor. La cédula debe tener entre 6 y 10 dígitos
   y la contraseña debe tener al menos 8 caracteres.
3. Inicia sesión con el correo y la contraseña registrados.
4. En el panel, selecciona **Publicar cosecha** para agregar un producto, su
   categoría, cantidad, precio y fecha.
5. Abre **Ofertas** para ver las cosechas activas y probar los filtros.

No hay cuentas de ejemplo: registra una cuenta para entrar por primera vez.

## 7. Cerrar y volver a iniciar la aplicación

Para cerrar AgroValle Connect, vuelve a PowerShell y presiona **Ctrl + C**.
PostgreSQL puede permanecer iniciado; tus datos quedan guardados.

Para abrir el proyecto otro día, repite los pasos 3, 4 y 5. No necesitas crear
de nuevo la base de datos.

## Si algo no funciona

- **La página no abre:** confirma que PowerShell sigue abierto, que el servidor
  terminó de iniciar y que escribiste `http://localhost:8080`.
- **No se conecta a PostgreSQL:** confirma que PostgreSQL está iniciado, que
  existe la base `agrovalle_connect` y que `DB_USER` y `DB_PASSWORD` coinciden
  con tu instalación.
- **El puerto 5432 está ocupado:** detén la otra instancia de PostgreSQL o
  cambia el puerto de conexión en `src/main/resources/application.properties`.
- **El puerto 8080 está ocupado:** cierra el otro programa que lo utiliza o
  inicia Spring en otro puerto con
  `.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--server.port=8081"`.
  Luego visita <http://localhost:8081>.
- **No puedes crear la cuenta:** comprueba que la cédula tenga entre 6 y 10
  dígitos, que la contraseña tenga al menos 8 caracteres y que el correo no
  esté registrado.
- **La sesión dejó de funcionar:** inicia sesión otra vez. El token de acceso
  vence en una hora y se mantiene solo en la sesión actual del navegador.
- **La recuperación de contraseña no muestra un formulario:** el backend
  incluye los endpoints, pero esa pantalla aún no forma parte del frontend.

## Alternativa: usar Docker para PostgreSQL

Si ya tienes Docker Desktop, puedes omitir la instalación local de PostgreSQL.
Después de configurar `DB_NAME`, `DB_USER` y `DB_PASSWORD` en PowerShell,
ejecuta `docker compose up -d --wait` antes de iniciar Spring Boot. Para detener
la base de datos sin borrar la información, ejecuta `docker compose down`.

Más detalles del frontend y su correspondencia con la API:
[Interfaz web](diseno/front-end.md).
