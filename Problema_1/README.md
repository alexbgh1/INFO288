# Problema 1

El problema a resolver consiste en tener **3 nodos esclavos** y **1 nodo maestro**. El nodo maestro debe ser capaz de recibir una petición de búsqueda y según la categoría (predefinida) hacer llamada al nodo esclavo correspondiente.

En caso de hacer una llamada `/search/product` o `/search/category` al nodo maestro, este debe retornar el resultado de la búsqueda en todos los nodos esclavos.

## Librerías

```python
pip install flask
pip install requests
```

## Ejecución

Desde la carpeta **Problema_1** ejecutar los siguientes comandos:

**bash slave 1**

```bash
python3 ./slave.py 1
```

**bash slave 2**

```bash
python3 ./slave.py 2
```

**bash slave 3**

```bash
python3 ./slave.py 3
```

**bash master**

```bash
python3 ./main.py
```

## Testear

```
# Master
http://127.0.0.1:5000/search/category?query=ropa
http://127.0.0.1:5000/search/category?query=ropa+hogar
http://127.0.0.1:5000/search/product?query=camisa

# Slaves
http://127.0.0.1:5001/search/product?query=camisa
http://127.0.0.1:5002/search/product
http://127.0.0.1:5003/search/product
```

## config

Donde el **id** es el parámetro que va en la ejecución de los nodos esclavos (./slave.py **id**)

```json
{
  "slaves": [
    {
      "id": 1,
      "ip": "127.0.0.1",
      "port": 5001,
      "categoria": "Ropa",
      "data_location": "data/ropa.json"
    },
    {
      "id": 2,
      "ip": "127.0.0.1",
      "port": 5002,
      "categoria": "Computacion",
      "data_location": "data/computacion.json"
    },
    {
      "id": 3,
      "ip": "127.0.0.1",
      "port": 5003,
      "categoria": "Hogar",
      "data_location": "data/hogar.json"
    }
  ],
  "categorias": ["ropa", "computacion", "hogar"]
}
```
