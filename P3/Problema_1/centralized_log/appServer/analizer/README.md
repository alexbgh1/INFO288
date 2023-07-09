<h1>Analizer</h1>

<p><b>"analizer.py"</b> es un script que requiere de algunos servicios funcionales antes de su ejecución:</p>

```bash
# JAVA path: centralized_log/appServer
rmiregistry 4003
java ServidorLog
```

```bash
# PYTHON path: ../main.py
python3 ./main.py
```

<p>Es importante verificar los archivos <b>.env</b> para el manejo de rutas de la aplicación</p>

<p>El script está encargado de seguir los siguientes pasos:</p>
<ol>
    <li>Carga la configuración (.env)</li>
    <li>Crea los datos 100, 1000, 10000</li>
    <li>Levanta los esclavos (endopints) para cada iteración</li>
    <li>Simula las requests por cada iteración</li>
    <li>Apaga los esclavos por cada iteración</li>
    <li>Levanta el cliente de Java para centralizar la informacion</li>
    <li>Cierra procesos de java</li>
    <li>Procesa la informacion</li>
</ol>

<p>El script genera un archivo <b>products.png</b>, <b>categories.png</b> con gráficas que utilizan la información centralizada, en donde debería apreciar una subida exponencial acorde a la cantidad de datos elegidas [100, 1000, 1000]</p>
