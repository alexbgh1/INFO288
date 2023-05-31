# Problema 1 (RMI)

Para el problema 1 en la parte de **RMI** se modificaron algunas cosas, como la entrada

Ejemplo de envió de mensaje:

```bash
inicio de conexion;alex;ffff;hh
/home/alex/Desktop/info288/INFO288/p2/INFO288/P2/Problema_1/slave1/ropa.log
/home/alex/Desktop/info288/INFO288/p2/INFO288/P2/Problema_1/slave2/computacion.log
EXIT
```

Al **iniciar la conexion** se crea un archivo **pointer\_<apodo>** el cual guarda la posicion del archivo que se esta leyendo, para luego poder leer desde esa posicion.

La información centralizada se almacena en: **/data/data.txt**

```bash
inicio de conexion;alex;ffff;hh;1685216796499
1;INFO:root:1685145276;buscar_categorias;word_['ropa', 'computacion'];ini;1685216807323;alex;1685216807347
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
