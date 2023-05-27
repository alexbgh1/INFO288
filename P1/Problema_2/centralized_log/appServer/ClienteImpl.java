
import java.rmi.*;
import java.rmi.server.*;

// Aquí podríamos tener funciones que el cliente pueda usar
class ClienteImpl extends UnicastRemoteObject implements Cliente {
    ClienteImpl() throws RemoteException {
    }
    // public void notificacion(String apodo, String m) throws RemoteException {
	// System.out.println("\n" + apodo + " dice > " + m);
    // }
}
