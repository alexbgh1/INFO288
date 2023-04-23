# Problema 2 (RMI)

Se deberá implementar un **log centralizado,** de manera secuencial (una vez activo el Servidor), un Cliente se conectará al Servidor, entrará en un bucle donde tendrá 2 opciones, **"EXIT"** para salir del bucle (sin acciones) o **"inicio de conexion;"+"apodo"+;+"fecha"+";"+"hora"**, donde el apodo es el nombre del cliente, que servirá como identificador dentro del log.

Los "logs" se consideran como mensajes con una estructura dada:

Ejemplo de envió de mensaje:

```bash
inicio de conexion;gggg;ff;hh;gggg;2023-04-23;09:38:45
1;2023-04-04;13:00:00;group by;gggg
```

Por simplicidad, la validación de datos se considera el conteo de ";" en el mensaje, si el mensaje tiene 3 ";" se considera válido, si no, se considera inválido.

**/data/data.txt**

```bash
1;2023-04-04;13:00:00;group by;gggg;2023-04-23;09:39:11
inicio de conexion;gggg;ff;hh;gggg;2023-04-23;09:38:45
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

Ya compilado, ejecutaremos el servidor de **RMI** y el **servidor de log.**

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
