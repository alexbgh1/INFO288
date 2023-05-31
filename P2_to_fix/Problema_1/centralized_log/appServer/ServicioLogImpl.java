
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

            // ------- EXIT -------
            // Si el mensaje es no tiene 3 <;>, no es válido
            // Pero si el mensaje es 'EXIT' o comienza con 'cerrar sesion;', es válido
            if (log.equals("EXIT")) {
                // Si el mensaje es 'EXIT', registra su salida
        
                // ------ Inserta en la última línea ------
                // Abre el archivo en modo de añadir al final, agrega el mensaje y cierra el archivo
                FileWriter archivoEscritura = new FileWriter(archivo, true);
                archivoEscritura.write(log  +  ";" + apodo + ";" + System.currentTimeMillis()  + "\n"); // EXIT;;hora_sv
                archivoEscritura.close();
                return "--- ok ---";
            }
            
            // Si el mensaje contiene 'inicio de sesion;' lo registra
            if (log.startsWith("inicio de conexion;")) {
                // Si el mensaje es 'inicio de sesion;', registra su entrada
        
                // ------ Inserta en la última línea ------
                // Abre el archivo en modo de añadir al final, agrega el mensaje y cierra el archivo
                FileWriter archivoEscritura = new FileWriter(archivo, true);
                archivoEscritura.write(log  + ";" + System.currentTimeMillis()  + "\n"); // inicio de sesion;apodo;fecha_sv;hora_sv
                archivoEscritura.close();
                return "--- ok ---";
            }

            // ------- Si el mensaje es válido, registra su entrada -------
            

            // ------ Inserta en la última línea ------ 
            // Abre el archivo en modo de añadir al final, agrega el mensaje y cierra el archivo
            FileWriter archivoEscritura = new FileWriter(archivo, true);
            archivoEscritura.write(log  + ";" + System.currentTimeMillis()  + "\n");
            archivoEscritura.close();
            return "--- ok ---";

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "--- Algo inesperado ocurrio. ---";
    }
}
