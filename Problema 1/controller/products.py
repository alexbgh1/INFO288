from flask import Flask, request, jsonify,json
from common import app, sysConfig
from controller import *

import model

@app.route('/api', methods=['GET'])

# http://localhost:5001/api/query?productos=pantalon+camisa
@app.route('/api/query', methods=['GET'])
def get_products_query():
    
    # Obtenemos los parametros de la URL
    query_parameters = request.args

    # Obtenemos el parametro productos
    productos = query_parameters.get('productos')
    productos = productos.split(" ") # split por el caracter +

    # Obtenemos la data de los productos
    res = []
    productsDB = model.get_all_products(sysConfig)

    # Si el producto esta en la data, lo agregamos a la respuesta
    for product in productsDB:
        print(product["nombre"])
        print
        if product["nombre"] in productos:
            res.append(product)
            print("Producto encontrado: ", product["nombre"])

    return jsonify(res)

# http://localhost:5001/api/query?categoria=Ropa+Hogar

