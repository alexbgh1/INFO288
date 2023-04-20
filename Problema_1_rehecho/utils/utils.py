from flask import json

def load_data(config_name='config.json',slave_id=1):
    """
    Carga la configuración: {id, ip, port, categoria, data_location}
    Y los datos que almacenará el esclavo: [id, categoria, nombre, precio]
   
    :config_name: Nombre del archivo de configuración
    :config_name:type: str
    :slave_id: Id del esclavo a cargar
    :slave_id:type: sys.argv[1] (int)

    :return: Tupla con la configuración y los datos del esclavo
    """

    # --- Lee 'config.json' ---
    try:
        with open(config_name, "r") as f:
            config_name = json.load(f)
    except FileNotFoundError:
        print("No se encontró el archivo de configuración {}".format(config_name))
        return None, None

    # --- Encontrar el id del esclavo dentro de config ---
    slave_id = int(slave_id)
    slave_config = None
    for current_slave in config_name["slaves"]:
        if current_slave["id"] == slave_id:
            slave_config = current_slave
            break

    if slave_config is None:
        print("No se encontró el esclavo con id {}".format(slave_id))
        return None, None
    
    # --- Localizar su archivo de productos ---
    data_location = slave_config["data_location"]
    data_slave = {}
    try:
        with open(data_location, "r") as f:
            data_slave = json.load(f) # [{id,categoria,nombre,precio}, {...}, {...}]

    except FileNotFoundError:
        print("No se encontró el archivo de productos {}".format(data_location))
        return None, None

    return slave_config, data_slave

def validar_duplicados(parametro):
    """
    Elimina los duplicados de la lista
    :parametro: String con los parametros a buscar, ej: "camisa camisa pantalón"
    :parametro:type: str

    :return: Lista con los parametros sin duplicados, ej: ["camisa", "pantalón"]
    :return:type: list
    """
    parametro = parametro.split(" ")
    # remover '' de la lista
    parametro = list(filter(None, parametro))
    parametro = list(set(parametro))

    return parametro