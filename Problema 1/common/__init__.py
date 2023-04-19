from flask import Flask, json
import time,sys

###############################################################################
current_milli_time = lambda: int(round(time.time() * 1000))

###############################################################################


def read_config():
    print("#################################################################")
    print("Cantidad de argumentos:{}".format(len(sys.argv)))
    print("Lista de argumentos:{}".format(sys.argv))
    print("#################################################################")


    if len(sys.argv) < 3:
        print("Error, debe ingresar el nombre del archivo de configuración")
        sys.exit(1)

    # Read the config file
    configFile = sys.argv[1]
    dataFile = sys.argv[2]

    configData = ""
    with open(configFile) as json_file:
        configData = json.load(json_file)


    return configData

###############################################################################

#CONSTANTE DEL SISTEMA
app = Flask(__name__)
sysConfig = read_config()