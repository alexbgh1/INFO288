from flask import Flask, json
import time,sys

###############################################################################
current_milli_time = lambda: int(round(time.time() * 1000))

###############################################################################


def read_config():
    if len(sys.argv) < 2:
        print("Error, debe ingresar el nombre del archivo de configuración")
        sys.exit(1)

    # Read the config file
    configFile = sys.argv[1]

    configData = ""
    with open(configFile) as json_file:
        configData = json.load(json_file)


    return configData

###############################################################################

#CONSTANTE DEL SISTEMA
app = Flask(__name__)
sysConfig = read_config()