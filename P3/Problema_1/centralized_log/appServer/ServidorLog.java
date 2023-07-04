
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


// IMPORT CONFIG.json
import java.io.File;
import java.util.Scanner;

class ServidorLog  {

    // IMPORT CONFIG.json
    private static final String CONFIG_PATH = "config.txt";
    static public void main (String args[]) {
        
        // Leemos CONFIG_PATH
        File configFile = new File(CONFIG_PATH);
        // Valores por defecto
        String IP_S = "127.0.0.1";
        String PORT_S = "4000";

        try {
            Scanner configReader = new Scanner(configFile);
            IP_S = configReader.nextLine(); // "IP"="127.0.0.1"
            IP_S = IP_S.split("=")[1];
            PORT_S = configReader.nextLine(); // "PORT"="4000"
            PORT_S = PORT_S.split("=")[1];
            configReader.close();
        } catch (Exception e) {
            System.err.println("Exception:");
            e.printStackTrace();
            System.exit(1);
        }
        int PORT = Integer.parseInt(PORT_S);
        System.setProperty("java.rmi.server.hostname", IP_S);

        try {
            // Registramos el servicio en el servidor
            ServicioLogImpl srv = new ServicioLogImpl();
            ServicioLog stub =(ServicioLog) UnicastRemoteObject.exportObject(srv,0);

            // Creamos un objeto de la clase Registry
            Registry registry = LocateRegistry.getRegistry(IP_S,PORT);

            System.out.println("Servidor escuchando en el puerto " + String.valueOf(PORT));

            // Registramos el servicio en el servidor con el nombre "Log"
            registry.bind("Log", stub);
        }

        catch (Exception e) {
            System.err.println("Exception:");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
