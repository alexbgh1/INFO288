import java.rmi.*;

// Definimos la interfaz del servicio
// Es decir, los métodos que el cliente puede usar
public interface ServicioLog extends Remote {
    void alta(Cliente client) throws RemoteException;
    void baja(Cliente client) throws RemoteException;
    String registrarLog(Cliente c,String apodo, String log) throws RemoteException;
    String loadSecret() throws RemoteException;
}
