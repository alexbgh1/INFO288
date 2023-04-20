from flask import Flask, request, jsonify
import json
import requests

# --- Lee config.json ---
config = {}
with open("config.json", "r") as f:
    config = json.load(f)

# --- Categorias ---
categorias = config["categorias"]

# Crea la aplicación de Flask
app = Flask(__name__)

def search_slaves(query=[''], category=['']):
    # Como ya sabemos que en categorias están las categorias de los productos
    # ['ropa', 'computacion', 'hogar']

    # Si query es vacío, entonces se buscan en todos los nodos
    res = []

    # Si category está en categorias, entonces se busca en el nodo que tenga esa categoria
    if category != ['']:
        for cat in category:
            if cat in categorias:
                for slave in config["slaves"]:
                    if cat in slave["categoria"].lower():
                        try:
                            url = "http://{}:{}/search/product?query=".format(slave["ip"], slave["port"])
                            r = requests.get(url)
                            res += r.json()
                            print('LLamando nodo {} con categoria {}'.format(slave["id"], cat))
                        except:
                            print('Es posible que el servidor {} no esté disponible'.format(slave["id"]))
                            pass

    if query == [''] and category == ['']: # localhost:5000/search/product
        for slave in config["slaves"]:
            # Por cada slave, se hace una petición GET a la ruta /search/product del slave
            # y se obtiene un json con los resultados, que se agregan a res
            try:

                url = "http://{}:{}/search/product?query=".format(slave["ip"], slave["port"])
                r = requests.get(url)
                res += r.json()
                print ('LLamando nodo {}'.format(slave["id"]))

            except:
                print('Es posible que el servidor {} no esté disponible'.format(slave["id"]))
                pass
    return res


# localhost:5000/search/product?query=producto
@app.route("/search/product", methods=["GET"])
def search_product():
    query = request.args.get("query", default="", type=str)
    query = query.split(" ")
    query = list(set(query))

    results = search_slaves(query=query)
    return jsonify(results)

# Ruta para la búsqueda por categoría
@app.route("/search/category", methods=["GET"])
def search_categoria():
    categoria = request.args.get("query", default="", type=str)
    categoria = categoria.split(" ")
    categoria = list(set(categoria))

    results = search_slaves(category=categoria)
    return jsonify(results)

if __name__ == "__main__":
    app.run(port=5000)