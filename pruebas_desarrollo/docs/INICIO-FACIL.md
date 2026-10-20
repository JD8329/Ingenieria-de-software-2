# Cómo iniciar AgroValle Connect

Esta guía explica, paso a paso y sin asumir experiencia en programación, cómo
abrir AgroValle Connect en un computador con Windows. La interfaz (front) y el
servidor (back) están dentro del mismo proyecto: **al iniciar el servidor, la
interfaz también queda disponible**. No es necesario instalar Node.js ni
ejecutar un segundo programa para el front.

## 1. Qué se necesita

Antes de empezar, instala estos programas:

1. **Java JDK 17** o una versión compatible posterior. Es el programa que
   permite ejecutar el servidor.
2. **PostgreSQL**. Es la base de datos donde se guardan las cuentas y las
   cosechas. Durante su instalación, recuerda el nombre de usuario y la
   contraseña que elegiste.
3. **Git**, si todavía no tienes una copia de este proyecto en tu computador.

No necesitas instalar Maven: el proyecto incluye un archivo que lo descarga y
lo ejecuta por ti. Tampoco necesitas instalar un programa adicional para el
front.

## 2. Preparar la base de datos (solo la primera vez)

1. Abre **pgAdmin**, que se instala junto con PostgreSQL.
2. En el menú de la izquierda, abre tu servidor PostgreSQL. Si lo solicita,
   escribe la contraseña que elegiste al instalar PostgreSQL.
3. Haz clic derecho en **Databases** (o **Bases de datos**) y selecciona
   **Create > Database...**.
4. Escribe `agrovalle_connect` como nombre y guarda.

La aplicación crea y actualiza las tablas necesarias cuando se inicia. No
crees tablas manualmente.

## 3. Abrir una ventana de comandos en la carpeta del proyecto

1. Abre la carpeta del proyecto en el Explorador de archivos.
2. Haz clic en la barra donde aparece la dirección de la carpeta.
3. Escribe `powershell` y presiona **Enter**. Se abrirá una ventana azul o negra
   ubicada en esa carpeta.

Si el proyecto todavía no está descargado, puedes pedir a una persona de apoyo
que lo clone desde el repositorio y continúe desde este paso.

## 4. Configurar la conexión y la clave de seguridad

En la ventana de PowerShell, copia y pega este bloque. Sustituye
`ESCRIBE_AQUI_TU_CLAVE_DE_POSTGRES` por la contraseña que configuraste en
PostgreSQL. No compartas esa contraseña ni la guardes en archivos del proyecto.

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

Si en PostgreSQL elegiste un usuario distinto de `postgres`, cambia el valor de
`DB_USER`. La clave `JWT_SECRET` se genera de forma aleatoria; no tienes que
inventar una. Estas instrucciones configuran únicamente la ventana actual, por
lo que se repiten si cierras PowerShell y vuelves a iniciar la aplicación otro
día.

## 5. Iniciar el servidor (back) y el front

En la misma ventana de PowerShell, ejecuta:

```powershell
.\mvnw.cmd spring-boot:run
```

La primera vez puede tardar unos minutos mientras se descargan componentes.
Deja abierta la ventana. Cuando aparezca un mensaje que indique que la
aplicación inició, abre el navegador y visita:

**http://localhost:8080**

Esa dirección abre el front. El back recibe las solicitudes del front en esa
misma dirección. Por eso no hace falta iniciar dos programas ni cambiar ajustes
de red.

## 6. Crear una cuenta y probar la aplicación

1. En la pantalla de inicio, selecciona **Regístrate aquí**.
2. Completa los datos de agricultor y guarda la cuenta. La cédula debe tener
   entre 6 y 10 números y la contraseña debe tener al menos 8 caracteres.
3. Inicia sesión con el correo y la contraseña que acabas de registrar.
4. En el panel, elige **Publicar una cosecha** para agregar un producto, su
   categoría, cantidad, precio y fecha de cosecha.
5. Abre **Ofertas** para consultar las cosechas publicadas y filtrarlas.

El proyecto no crea cuentas de ejemplo. Debes registrar una cuenta para entrar
por primera vez.

## 7. Cerrar la aplicación

Para cerrar AgroValle Connect, vuelve a la ventana de PowerShell y presiona
**Ctrl + C**. Cuando quieras volver a utilizarlo, repite los pasos 3, 4 y 5.

## Si algo no funciona

- **La página no abre:** confirma que la ventana de PowerShell sigue abierta y
  que terminó de iniciar. Comprueba que escribiste `http://localhost:8080`.
- **No se puede conectar a PostgreSQL:** confirma que PostgreSQL está iniciado,
  que existe la base `agrovalle_connect` y que `DB_USER` y `DB_PASSWORD`
  corresponden a tu instalación.
- **La contraseña de la base de datos es incorrecta:** vuelve al paso 4 y
  corrige `DB_PASSWORD` en la misma ventana antes de iniciar la aplicación.
- **No se puede crear la cuenta:** revisa que el correo no se haya usado antes,
  que la cédula tenga entre 6 y 10 números y que la contraseña tenga al menos
  8 caracteres.
- **La sesión ya no es válida:** inicia sesión otra vez. Por seguridad, la
  sesión del agricultor dura una hora y se guarda solo mientras la pestaña del
  navegador esté abierta.

Para detener la aplicación, no cierres la ventana mientras guarda información;
usa **Ctrl + C** y espera a que vuelva el indicador de PowerShell.
