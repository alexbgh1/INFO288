# Problema 2 (RMI)

Se deberá implementar un **log centralizado,** de manera secuencial (una vez activo el Servidor), un Cliente se conectará al Servidor, entrará en un bucle donde tendrá 2 opciones, **"EXIT"** para salir del bucle (sin acciones) o **"inicio de conexion;"+"apodo"+;+"fecha"+";"+"hora"**, donde el apodo es el nombre del cliente, que servirá como identificador dentro del log.

Los "logs" se consideran como mensajes con una estructura dada:

La validación del mensaje parte por el **";"**, debe validar que el mensaje tenga la estructura correcta. Además hay una validación (una vez que el usuario ya está conectado), para que los campos tengan las variables de forma:

- **int**: Número del registro (único por usuario)
- **date YYYY-mm-DD**: Fecha de registro en formato YYYY-mm-DD
- **time HH:mm:ss**: Hora de registro en formato HH:mm:ss
- **string**: Mensaje de la instrucción

Ejemplo de envió de mensaje:

```bash
inicio de conexion;gggg;2023-04-04;13:00:00
1;2023-04-04;13:00:00;group by
```

La información se almacena en: **/data/data.txt**

```bash
inicio de conexion;gggg;2023-04-23;13:00:00;gggg;2023-04-23;13:06:27
1;2023-04-04;13:00:00;group by;gggg;2023-04-23;13:06:28
```

## Librerías

Java RMI

## Ejecución

Para ejecutar el código deberemos compilarlo.

**_/appServer - bash 1_**

```bash
javac ServidorLog.java
javac ClienteImpl.java
javac Cliente.java
```

Ya compilado, ejecutaremos el servidor de **RMI** y el **servidor de log.**. El puerto puede cambiar, de ser así verificar **ServidorLog.java y ClienteLog.java.**

**_/appServer - bash 1_**

```bash
rmiregistry 4003
```

**_/appServer - bash 2_**

```bash
java ServidorLog
```

Pegamos **Cliente.class**, **ClienteImpl.class** y **ServicioLog.class** en la carpeta **/appClient**.

Ahora ejecutaremos el cliente, que se conectará al servidor de **RMI** y al servidor de log.

**_/appServer - bash 3_**

```bash
javac ClienteLog.java
java ClienteLog
```

## Cómo funciona

Para entender un poco más el funcionamiento, y las relaciones, una secuencia de ejecución (interna) es más o menos la siguiente:

- **ServidorLog:** ServicioLogImpl, ServicioLog -> Cliente: ClienteImpl, Cliente

### ServidorLog

**ServidorLog.java** tiene 2 clases, **ServidorLog** y **ClienteImpl**.

**ServicioLog** es la interfaz que se implementa en **ServicioLogImpl** (contiene los métodos que se pueden llamar remotamente).

**ServicioLogImpl** es la clase que implementa la interfaz **ServicioLog**. Esta clase es la que se registra en el **RMI** (contiene la implementación de los métodos que se pueden llamar remotamente).

**Cliente** es la interfaz que se implementa en **ClienteImpl** (contiene los métodos que se pueden llamar remotamente).

**ClienteImpl** es la clase que implementa la interfaz **Cliente**. Esta clase es la que se registra en el **RMI** (contiene la implementación de los métodos que se pueden llamar remotamente).

En este caso se dejó declarado **Cliente** como una clase, pese a no tener funciones añadidas, la razón de esto es que se puede extender a un cliente más complejo, con más funciones, y por qué ya estaba implementado en la plantilla dada (RMI chat).

Cabe mencionar que toda la lógica de funciones que luego usará el usuario se encuentra en **ServicioLogImpl**.

### ClienteLog

En **ClientLog** se establece conexión con el **RMI** y se registra el cliente en el servidor de log. Luego se ejecuta un bucle que le permite al usuario ingresar un comando, que puede ser **"EXIT"** o **"inicio de conexion;"+"apodo"**.

Luego se hacen llamados a funciones declaradas en **ServicioLogImpl**, como por ejemplo **registrarLog**.
