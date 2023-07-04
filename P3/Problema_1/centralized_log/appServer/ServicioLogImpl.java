
import java.util.*;
import java.rmi.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

class ServicioLogImpl implements ServicioLog {
    List<Cliente> listaClientes;
    String archivo = "data/data.txt";

    // Leemos config_secret.txt
    String secret = "config_secret.txt";
    // Valores por defecto
    String SECRETKEY_S = "5";
    int SECRETKEY = Integer.parseInt(SECRETKEY_S);


    ServicioLogImpl() throws RemoteException {
        listaClientes = new LinkedList<Cliente>();
    }

    public String loadSecret() throws RemoteException{
        File secretFile = new File(secret);
        try {
            Scanner secretReader = new Scanner(secretFile);
            SECRETKEY_S = secretReader.nextLine().split("=")[1]; // "SECRETKEY"="20"
            SECRETKEY = Integer.parseInt(SECRETKEY_S);
            secretReader.close();
        } catch (Exception e) {
            System.err.println("Exception:");
            e.printStackTrace();
            return "Error al leer el archivo de configuración secreto.";
        }

        return "Llave secreta cargada correctamente.";
    }

    public void alta(Cliente client) throws RemoteException {
	    listaClientes.add(client);
    }

    public void baja(Cliente client) throws RemoteException {
        listaClientes.remove(listaClientes.indexOf(client));
    }

    public String registrarLog(Cliente c, String apodo, String log) throws RemoteException {


        try {
            // ------ Inserta en la última línea ------ 
            // Abre el archivo en modo de añadir al final, agrega el mensaje y cierra el archivo
            FileWriter archivoEscritura = new FileWriter(archivo, true);

            // DESENCRIPTA EL MENSAJE
            log = desencriptar(log, SECRETKEY);

            archivoEscritura.write(log  + "; " + System.currentTimeMillis()/1000  + "\n");
            archivoEscritura.close();
            return "--- ok ---";

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "--- Algo inesperado ocurrio. ---";
    }

    // Recibe una cadena de texto, la encripta y la retorna
    public static String desencriptar(String s, int key) {
        String desEncriptado = "";
        for (int i = 0; i < s.length(); i++) {
            desEncriptado += desEncriptarChar(s.charAt(i), key);
        }
        return desEncriptado;
    }

    public static char desEncriptarChar(char c, int key) {
        return (char) (c - key);
    }
}
