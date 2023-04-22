from flask import Flask, request, jsonify
import json
import requests
import utils.utils  as utils

# --- Lee config.json ---
config = {}
with open("config.json", "r") as f:
    config = json.load(f)

# --- Categorias ---
categorias = config["categorias"]

# Crea la aplicación de Flask
app = Flask(__name__)

def search_slave_info(slave_id):
    """
    Busca la información del esclavo con el id ingresado
    :param slave_id: Id del esclavo a buscar
    :type slave_id: int
    :return: Diccionario con la información del esclavo
    :return type: dict
    """
    for slave in config["slaves"]:
        if slave["id"] == slave_id:
            return slave
    return None

def search_slaves(clave, parametros):
    """

    """

    # --- Si no parametros es None, se busca por productos ---
    results = []
    if parametros == None:
        clave = 'productos'
        for slave in config["slaves"]:
            try:
                response = requests.get(f"http://{slave['ip']}:{slave['port']}/search/query")
                results += response.json()
            except:
                print (f"Error al conectar con el esclavo {slave['id']}")
                pass
        return results
    

    # --- Si hay parametros, se busca por categorias o productos ---
    parametros = utils.validar_duplicados(parametros)
    # lista a string ["camisa", "pantalon"] -> "camisa+pantalon"
    parametros = "+".join(parametros)

    
    # Si es por productos, busca en todos los esclavos
    # http://127.0.0.1:5000/search/query?productos=camisa+teclado
    if clave == 'productos':
        for slave in config["slaves"]:
            try:
                response = requests.get(f"http://{slave['ip']}:{slave['port']}/search/query?{clave}={parametros}")
                results += response.json()
            except:
                print (f"Error al conectar con el esclavo {slave['id']}")
                pass


    # Si es por categorias, busca en el esclavo correspondiente
    # http://127.0.0.1:5000/search/query?categorias=ropa+computacion
    elif clave == 'categorias':
        for categoria in parametros.split("+"): # "ropa+computacion" -> ["ropa", "computacion"]
            if categoria in categorias:
                try:
                    slave_info = search_slave_info(categorias.index(categoria) + 1)  
                    response = requests.get(f"http://{slave_info['ip']}:{slave_info['port']}/search/query?{clave}={parametros}")
                    results += response.json()
                    print (f"Conectado con el esclavo {slave_info['id']}")
                except:
                    print (f"Error al conectar con el esclavo {slave_info['id']}")
                    pass
            
    # Eliminar duplicados del json
    # Recorremos por el json y filtramos por el id
    return results

# localhost:5000/search/query?productos=camisa+teclado
# localhost:5000/search/query?categorias=ropa+computacion
@app.route("/search/query", methods=["GET"])
def search_product():
    """
    Busca los productos que coincidan con el query ingresado
    Los query deben ser de la forma: /search/query?<nombre>=<valor> ; el nombre puede ser "productos" o "categorias"
    :return: Lista con los productos que coinciden con el query
    :return:type: json response (list)
    """
    # --- Si es solo /search/query, retorna todos los productos ---
    if len(request.args) == 0:
        return search_slaves()

    clave = list(request.args.keys())[0] # ['categorias'] -> convierte a lista
    # --- Obtiene el query ---
    articulos = request.args.get("productos") or request.args.get("categorias")
    if ((clave == "productos" or clave == "categorias") and articulos == None ):
        # retornamos todos los productos
        return search_slaves(clave, articulos)


    if (articulos == None):
        return jsonify({"error": "Debe buscar por 'productos' o 'categorías': query?<nombre>="}), 400 # Bad Request
    
    # --- Valida que el query no esté vacío ---
    if articulos == "":
        return jsonify({"error": "Debe ingresar un valor para buscar"}), 400
    
    # --- Realiza la búsqueda ---
    response = search_slaves(clave, articulos)

    return jsonify(response)


if __name__ == "__main__":
    app.run(port=5000)