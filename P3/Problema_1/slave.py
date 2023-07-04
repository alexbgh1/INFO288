from flask import Flask, request, jsonify, json
import sys
import utils.utils  as utils
import logging
import time 
import uuid

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
if slave_config == None:
    print("Error al cargar la configuración del esclavo")
    sys.exit(1)

# --- INIT LOGGING --- https://stackoverflow.com/questions/8353594/can-python-log-output-without-inforoot
# Crear un objeto de registro
logger = logging.getLogger()
logger.setLevel(logging.INFO)

# Crear un formateador personalizado
formatter = logging.Formatter('%(message)s')

# Crear un controlador para el archivo de registro
file_handler = logging.FileHandler(slave_config["log_location"])
file_handler.setLevel(logging.INFO)
file_handler.setFormatter(formatter)

# Agregar el controlador de archivo al objeto de registro
logger.addHandler(file_handler)

# Desactivar el log de Flask solo por fines de tener el log final solicitado
fastapi_logger = logging.getLogger('werkzeug')
fastapi_logger.disabled = True

# --- Crea la aplicación de Flask ---
app = Flask(__name__)

# Ruta para la búsqueda por producto
# http://127.0.0.1:5001/search/query?productos=camisa+pantalón
# http://127.0.0.1:5001/search/query?categoria=ropa

def logg(uuidVal, clave, parametro,state):
    """
    Genera un log, en el log_path de cada slave
    """
    logging.info( uuidVal+'; '+ str(int(time.time())) + '; buscar por '+clave+'; '+state)
    # logging.info( str(int(time.time())) + ';buscar_'+clave+';word_'+str(parametro)+';ini')

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

    # print("timestamp: ", str(int(time.time())))
    # print("clave: ", clave)
    # print("parametro: ", parametro)

    parametro = utils.validar_duplicados(parametro)

    # --- Si se realiza una consulta, agrega al log INI---
    # timestamp ; buscar_categoria; ini
    uuidVal = str(uuid.uuid1())
    logg(uuidVal, clave, parametro, 'ini')

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



    # Elimina duplicados, esto se hace porque si se busca por producto y es: "camisa ca"
    results = [dict(t) for t in {tuple(d.items()) for d in results}]


    # --- Si se realiza una consulta, agrega al log FIN---
    # timestamp ; buscar_categoria; ini
    logg(uuidVal, clave, parametro, 'fin')
    return results

@app.route("/search/query", methods=["GET"])
def search_product():
    """
    Busca los productos que coincidan con el query ingresado
    :return: Lista con los productos que coinciden con el query
    :return:type: json response (list)
    """

    # --- Si es solo /search/query?productos, retorna todos los productos ---
    if len(request.args) == 0:
        # // log aca
        # logg('todos', None, 'ini')
        res = jsonify(data_slave)
        # logg('todos', None, 'ini')

        return res

    # Para mantenerlo simple asumiremos que solo se puede buscar por un query a la vez
    # Por lo tanto, solo se puede buscar por productos o categorías, no ambos
    clave = list(request.args.keys())[0] # ['categorias'] -> convierte a lista

    # --- Obtiene el query ---
    # Iteramos claves para obtener el valor de cada una
    articulo = request.args.get("productos") or request.args.get("categorias")

    if (articulo == None):
        return jsonify({"error": "Debe buscar por 'productos' o 'categorías': query?productos=<nombre>"}), 400 # Bad Request
    
    # --- Valida que el query no esté vacío ---
    if articulo == "":
        return jsonify({"error": "Debe ingresar un valor para buscar"}), 400
    
    # --- Realiza la búsqueda ---
    response = search_articulo(clave, articulo)

    return jsonify(response)


# --- Inicia la aplicación ---
app.run(host=slave_config["ip"], port=slave_config["port"], debug=True)
