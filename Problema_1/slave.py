from flask import Flask, request, jsonify, json
import sys

# --- Ejecución ---
# http://127.0.0.1:5001/search/product?query=camisa+Pantalón

# Basado en los argumentos de ejecución, se determina la información del esclavo
if len(sys.argv) != 2:
    print("Uso: python3 slave.py <slaveID>") # python3 slave.py 1
    sys.exit(1)

# --- Lee config.json ---
config = {}
with open("config.json", "r") as f:
    config = json.load(f)

# --- Encontrar el id del esclavo dentro de config ---
slave_id = int(sys.argv[1])
slave = None
for s in config["slaves"]:
    if s["id"] == slave_id:
        slave = s
        break

if slave is None:
    print("No se encontró el esclavo con id {}".format(slave_id))
    sys.exit(1)

# --- Localizar su archivo de productos ---
data_location = slave["data_location"]
data = {}
try:
    with open(data_location, "r") as f:
        data = json.load(f) # [{...}, {...}, {...}]

except FileNotFoundError:
    print("No se encontró el archivo de productos {}".format(data_location))
    sys.exit(1)


# --- Crea la aplicación de Flask ---
app = Flask(__name__)

# Ruta para la búsqueda por producto
@app.route("/search/product", methods=["GET"])
def search_product():
    query = request.args.get("query", default="", type=str)
    # Split por espacios
    print(query)
    query = query.split(" ") # -> ["camisa", "pantalon"]
    # Set para eliminar duplicados
    query = list(set(query)) # -> ["camisa", "pantalon"]
    
    results = search(query)
    return jsonify(results)

# Ruta para la búsqueda por categoría
@app.route("/search/category", methods=["GET"])
def search_category():
    category = request.args.get("query", default="", type=str)
    category = category.split(" ") 
    category = list(set(category)) 

    results = search([], category)
    return jsonify(results)

def search(query, category=[]):
    # query = ["camisa", "pantalon"] 

    results = []
    for q in query:
        for product in data:
            if q.lower() in product["nombre"].lower() in product["nombre"].lower():
                results.append(product)
    
    for c in category:
        for product in data:
            if c.lower() in product["categoria"].lower() in product["categoria"].lower():
                results.append(product)

    return results

# --- Inicia la aplicación ---
app.run(host=slave["ip"], port=slave["port"], debug=True)
