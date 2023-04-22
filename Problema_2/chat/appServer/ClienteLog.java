
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.util.*;
import java.io.PrintStream;

class ClienteLog {

    private static final int PORT = 4002;

    static public void main(String args[]) {
        if (args.length != 0) {
            System.err.println("Uso: /ClienteLog");
            return;
        }

        try {

            System.setOut(new PrintStream(System.out, true, "UTF-8"));
            // Registramos el cliente en el servidor para hacer uso de los métodos del servicio
            Registry registry = LocateRegistry.getRegistry("127.0.0.1", PORT);
            ServicioLog service = (ServicioLog) registry.lookup("Log");

            // Creamos un objeto de la clase ClienteImpl
            ClienteImpl client = new ClienteImpl();

            // Agregamos el cliente al LinkedList de clientes del servicio
            service.alta(client);

            // Creamos un objeto de la clase Scanner para leer los mensajes del usuario
            Scanner ent = new Scanner(System.in);

            // Definimos variables de validación
            // 'msg': mensaje que el usuario escribe en la consola
            // 'msgExit = EXIT': mensaje que el usuario escribe para salir del chat
            // 'prefix = inicio de conexión;': prefijo que el usuario debe escribir para asignar un apodo
            // 'apodo': apodo que el usuario asigna a su sesión
            String msg; String msgExit = "EXIT"; String prefix = "inicio de conexion;";
            String apodo = "";

            System.out.println("Hola!, has establecido una conexión correcta con el servidor."+"\n");
            System.out.println("Si deseas comenzar a enviar mensajes al servidor, escribe 'inicio de conexion;tu_apodo'");
            System.out.println("O Si deseas salir del chat, escribe 'EXIT'"+"\n");

            // Si el usuario escribe 'inicio de conexión;tu_apodo', se guarda el apodo en la variable 'apodo'
            while (ent.hasNextLine()){
                msg = ent.nextLine();

                if (msg.startsWith(prefix)) {
                    apodo = (msg.substring(prefix.length())).trim();
                    System.out.println("Apodo asignado: " + apodo +"."+"\n");
                    break;
                } else if (msg.equals(msgExit)) {
                    System.out.println("Saliendo del chat..."+"\n");
                    break;
                } else {
                    System.out.println("Por favor, escribe 'inicio de conexión;tu_apodo' para comenzar a enviar mensajes al servidor o 'EXIT' para salir del chat."+"\n");
                }
            }

            System.out.println("Los mensajes enviados deberán tener el siguiente formato:");
            System.out.println("Número correlativo; fecha; hora; mensaje de acción"+"\n");
            System.out.println("Ej: '1;2023-01-04;13:10:10;id=3 eliminado tabla4'"+"\n");
            
            Thread.sleep(2000);
            
            System.out.println("Desde este punto, puedes comenzar a enviar mensajes al servidor."+"\n");

            String response;
            if (apodo.equals("")) {
                System.out.println("No se ha asignado un apodo, por lo que no se podrán enviar mensajes al servidor.");
            }
            else {
                while (ent.hasNextLine()) {

                    msg = ent.nextLine();
                    if (msg.equals(msgExit)) {
                        service.registrarLog(client, apodo, msgExit);
                        System.out.println("Saliendo del chat...");
                        break;
                    } else {
                        response = service.registrarLog(client, apodo, msg);
                        System.out.println(response);
                    }
    
    
                }
            }


            ent.close();
            service.baja(client);
            System.exit(0);

        } catch (RemoteException e) {
            System.err.println("Error de comunicacion: " + e.toString());
        } catch (Exception e) {
            System.err.println("Excepcion en ClienteLog:");
            e.printStackTrace();
        }
    }
}
