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
1685751512; buscar por categorias; ini; 1685751521; compu; 1685751521
1685751512; buscar por categorias; fin; 1685751522; compu; 1685751522
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

```bash
java ClienteLog <apodo> <path_log>
java ClienteLog compu /home/alex/Desktop/info288/INFO288/p2/INFO288/P2/Problema_1/slave2/computacion.log
java ClienteLog alex /home/alex/Desktop/info288/INFO288/p2/INFO288/P2/Problema_1/slave1/ropa.log
```
