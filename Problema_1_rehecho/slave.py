from flask import Flask, request, jsonify, json
import sys
import utils.utils  as utils

# --- Ejecución ---
# http://127.0.0.1:5001/search/product?query=camisa+Pantalón

# Basado en los argumentos de ejecución, se determina la información del esclavo
if len(sys.argv) != 2:
    print("Uso: python3 slave.py <slaveID>") # python3 slave.py 1
    sys.exit(1)

if not sys.argv[1].isdigit():
    print("El id del esclavo debe ser un número")
    sys.exit(1)

# --- Carga la configuración del esclavo y los datos que almacenará ---
slave_config, data_slave = utils.load_data(config_name='config.json', slave_id=sys.argv[1])

# --- Crea la aplicación de Flask ---
app = Flask(__name__)

# Ruta para la búsqueda por producto
# http://127.0.0.1:5001/search/query?productos=camisa+pantalón
# http://127.0.0.1:5001/search/query?categoria=ropa

def search_articulo(clave, parametro):
    """
    Busca los articulos que coincidan con el parametro ingresado
    :clave: Clave del diccionario en el que se buscará el parametro
    :clave:type: str

    :parametro: String con los parametros a buscar, ej: "camisa pantalón"
    :parametro:type: str

    :return: Lista con los articulos que coinciden con el parametro
    :return:type: list
    """
    parametro = utils.validar_duplicados(parametro)

    key = ''
    if clave == 'productos':
        key = 'nombre'
    elif clave == 'categorias':
        key = 'categoria'

    results = []
    for param in parametro:
        for product in data_slave:
            if param.lower() in product[key].lower() in product[key].lower():
                results.append(product)

    results = [dict(t) for t in {tuple(d.items()) for d in results}]
    return results

@app.route("/search/query", methods=["GET"])
def search_product():
    """
    Busca los productos que coincidan con el query ingresado
    :return: Lista con los productos que coinciden con el query
    :return:type: json response (list)
    """

    # --- Si es solo /search/query, retorna todos los productos ---
    if len(request.args) == 0:
        return jsonify(data_slave)

    # Para mantenerlo simple asumiremos que solo se puede buscar por un query a la vez
    # Por lo tanto, solo se puede buscar por productos o categorías, no ambos
    clave = list(request.args.keys())[0] # ['categorias'] -> convierte a lista

    # --- Obtiene el query ---
    # Iteramos claves para obtener el valor de cada una
    articulo = request.args.get("productos") or request.args.get("categorias")

    if (articulo == None):
        return jsonify({"error": "Debe buscar por 'productos' o 'categorías': query?<nombre>="}), 400 # Bad Request
    
    # --- Valida que el query no esté vacío ---
    if articulo == "":
        return jsonify({"error": "Debe ingresar un valor para buscar"}), 400
    
    # --- Realiza la búsqueda ---
    response = search_articulo(clave, articulo)

    return jsonify(response)


# --- Inicia la aplicación ---
app.run(host=slave_config["ip"], port=slave_config["port"], debug=True)
