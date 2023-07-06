import json
import time

def load_config():
    """
    Carga la configuración del archivo config.json
    :return: Diccionario con la configuración
    :return type: dict
    """

    with open("config.json", "r") as f:
        config = json.load(f)

    return config
