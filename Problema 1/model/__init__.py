import json

def get_connection_db(dbConfig):
    # Conectarse al .json /data/data1.json

    db = json.load(open(dbConfig["dbname"]))
    print("Conexión establecida: ",json.dumps(db))

    return db

def get_all_products(dbConfig):

    resp = []

    try:
        # Cargamos la data desde dbConfig["dbname"]
        data = get_connection_db(dbConfig)
        
        # Recorremos la data
        for product in data:
            resp.append(product)
        
        return resp
    
    except (Exception) as error:
        print("Error al cargar la data: ", error)
        return resp # []
