
import requests
import random
import json
import time
import subprocess
import utils.utils as utils
import pandas as pd
import matplotlib.pyplot as plt

def simulate_requests(ip, master, endpoint, amount, products_names):
    """
    Simula requests a un servidor
    """

    for consulta in range(amount):
        # random choice products
        to_search = random.choice(products_names).lower()
        # localhost: +  5001 + /api/query?productos= + producto1
        url_get = ip+str(master['port'])+endpoint + to_search
        requests.get(url_get)
        print(f"Consulta {url_get} realizada")
        time.sleep(0.05)
    

def generate_data():
    """
    1. Carga la configuracion
    2. Crea los datos 100, 1000, 10000
    3. Levanta los esclavos para cada iteracion
    4. Simula las requests
    5. Apaga los esclavos

    6. Levanta el cliente de Java para centralizar la informacion
    7. Procesa la informacion

    """

    # 1. Carga la configuracion
    c = utils.load_config()
    test_data = c['test_data']; path_data = c['path_data']; slaves = c['slaves']; path_slave = c['path_slave'];
    ip = c['ip']; master = c['master'] 
    categories = [c['category'] for c in c['test_data']['test']]; requests_amount = c['requests_amount'];
    paths_java = c['paths_java']; java_client_path = c['java_client_path']
    path_out_data = c['path_out_data']; path_java_centralized_data = c['path_java_centralized_data']

    product_name = 'abcdefghijklmnopqrstuvwxyz'
    product_max_length = 5
    products_names = [ ''.join(random.choice(product_name) for i in range(random.randint(1, product_max_length))) for i in range(1000)]
    prices = [ i * 10+100 for i in range(90)]

    # 2. Crea los datos 100, 1000, 10000
    for length_data in test_data['test_amount']: # 100, 1000, 10000

        # Crea los archivos
        for current_test in test_data['test']: # [{},{},{}]

            # Creamos el archivo con los datos

            path = path_data + '/' + current_test['category'].lower() + '.json'
            with open(path, 'w') as file:
                file.write('[')
                for i in range(current_test['id_from'], current_test['id_from']+length_data):
                    product = {
                        "id": i,
                        "categoria": current_test['category'],
                        "nombre": random.choice(products_names),
                        "precio": random.choice(prices)
                    }
                    if (i != current_test['id_from']+length_data-1):
                        file.write(json.dumps(product) + ',\n')
                    else:
                        file.write(json.dumps(product))
                    
                file.write(']')
                file.close()

        # 3. Levanta los esclavos para cada iteracion
        # Una vez creado los archivos, levanta los esclavos
        for slave in slaves:
            subprocess.Popen(["python3", path_slave, str(slave['id'])])
            time.sleep(0.5)

        # 4. Simula las requests (productos)
        simulate_requests(
            ip,
            master,
            '/search/query?productos=',
            requests_amount,
            products_names
        )

        # 4. Simula las requests (categorias)
        simulate_requests(
            ip,
            master,
            '/search/query?categorias=',
            requests_amount,
            categories
        )
    
        # 5. Apaga los esclavos
        # Una vez terminado, apaga los esclavos
        for slave in slaves:
            requests.get(c['ip']+str(slave['port'])+'/stopServer')
            time.sleep(1)
        print(f"Datos generados: {length_data} datos")

    # 6. Levanta el cliente de Java para centralizar la informacion
    # java ClienteLog <apodo> 
    # java ClienteLog ropa /home/alex/Desktop/info288/INFO288/p3/INFO288/P3/Problema_1/slave1/ropa.log
    # java ClienteLog compu /home/alex/Desktop/info288/INFO288/p3/INFO288/P3/Problema_1/slave2/computacion.log
    # java ClienteLog hogar /home/alex/Desktop/info288/INFO288/p3/INFO288/P3/Problema_1/slave3/hogar.log

    java_processes = []
    for java_client in paths_java:
        process = subprocess.Popen(["java", "-cp", java_client_path, "ClienteLog", java_client['apodo'], java_client['path']])
        java_processes.append(process)
        time.sleep(30)

    # 7. Cierra procesos de java
    # Para detener los procesos
    for process in java_processes:
        process.terminate()
        print("Proceso de java terminado")
        time.sleep(1)

    # 8. Generamos analisis de los datos
    # import ../data/data.txt
    data = pd.read_csv(path_java_centralized_data, sep=";", names = ['id', 'time_py', 'type_search', 'state', 'time_java_c', 'apodo', 'time_java_s'])
    # Trim
    data['state'] = data['state'].str.strip()
    data['type_search'] = data['type_search'].str.strip()

    # Filtramos los datos
    data = data.sort_values(by=['id', 'state'])

    # Creamos un arreglo con type_search='buscar por productos'
    # Y otro con type_search='buscar por categorias'
    data_products = data.loc[data['type_search'] == 'buscar por productos']
    data_categories = data.loc[data['type_search'] == 'buscar por categorias']
    
    # Creamos un arreglo con los datos de python
    plot_and_save(path_out_data, data_products, 'productos')
    plot_and_save(path_out_data, data_categories, 'categorias')

def plot_and_save(path_out_data, data, type):
    data_to_plot_products = data['time_py'].tolist()
    temp_prod = []
    for i in range(0, len(data_to_plot_products), 2):
        temp_prod.append(data_to_plot_products[i] - data_to_plot_products[i+1] )


    fig, ax = plt.subplots(figsize=(10, 10))
    ax.set_title(f'Tiempo de respuesta de las consultas {type}', fontsize=20)
    ax.set_xlabel('Cantidad de datos', fontsize=18)
    ax.set_ylabel('Tiempo de respuesta (ms)', fontsize=18)
    ax.grid(True)
    ax.plot(temp_prod, 'o-', label='Python')

    # save plot
    plt.savefig(f'{path_out_data}/{type}.png')

# __main__
if __name__ == "__main__":
    # -- config: c --
    generate_data()
    