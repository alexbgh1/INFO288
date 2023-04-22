import java.rmi.*;

public interface ServicioLog extends Remote {
    void alta(Cliente client) throws RemoteException;
    void baja(Cliente client) throws RemoteException;
    String registrarLog(Cliente c,String apodo, String log) throws RemoteException;
}
