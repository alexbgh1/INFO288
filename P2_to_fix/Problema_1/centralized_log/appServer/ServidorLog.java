
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


class ServidorLog  {

    private static final int PORT = 4003;
    static public void main (String args[]) {

        System.setProperty("java.rmi.server.hostname","127.0.0.1");

        try {
            // Registramos el servicio en el servidor
            ServicioLogImpl srv = new ServicioLogImpl();
            ServicioLog stub =(ServicioLog) UnicastRemoteObject.exportObject(srv,0);

            // Creamos un objeto de la clase Registry
            Registry registry = LocateRegistry.getRegistry("127.0.0.1",PORT);

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
