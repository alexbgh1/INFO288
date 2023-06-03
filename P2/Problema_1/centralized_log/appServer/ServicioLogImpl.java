
import java.util.*;
import java.rmi.*;
import java.io.FileWriter;
import java.io.IOException;

class ServicioLogImpl implements ServicioLog {
    List<Cliente> listaClientes;
    String archivo = "data/data.txt";
    String pointer = "data/pointer.txt";

    ServicioLogImpl() throws RemoteException {
        listaClientes = new LinkedList<Cliente>();
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
            archivoEscritura.write(log  + "; " + System.currentTimeMillis()/1000  + "\n");
            archivoEscritura.close();
            return "--- ok ---";

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "--- Algo inesperado ocurrio. ---";
    }
}
