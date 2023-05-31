
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.util.*;
import java.io.PrintStream;
import java.io.File;


class ClienteLog {

    private static final int PORT = 4003;

    static public void main(String args[]) {
        if (args.length != 0) {
            System.err.println("Uso: /ClienteLog");
            return;
        }

        try {

            // Definimos el encoding de la consola
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
            System.out.println("Si deseas comenzar a enviar mensajes al servidor, escribe 'inicio de conexion;tu_apodo;fecha;hora'");
            System.out.println("O Si deseas salir del chat, escribe 'EXIT'"+"\n");

            // Si el usuario escribe 'inicio de conexión;tu_apodo;fecha;hora', se guarda el apodo en la variable 'apodo'
            while (ent.hasNextLine()){
                msg = ent.nextLine();

                // Si contiene "inicio de conexión;" y el mensaje tiene 3 ";"
                // Se espera un formato: 
                if (msg.startsWith(prefix) && msg.split(";").length == 4) {
                    apodo = msg.split(";")[1];
                    System.out.println("El apodo asignado es: " + apodo +"."+"\n");
                    // Enviamos la conexión al servidor
                    String response = service.registrarLog(client, apodo, msg);
                    System.out.println(response);
                    break;
                } else if (msg.equals(msgExit)) {
                    System.out.println("Saliendo del chat..."+"\n");
                    break;
                } else {
                    System.out.println("Por favor, escribe 'inicio de conexión;tu_apodo;fecha;hora' para comenzar a enviar mensajes al servidor o 'EXIT' para salir del chat."+"\n");
                }
            }

            // Comienza el "chat"
            System.out.println("A continuación deberás escribir el Path del archivo que deseas enviar al servidor."+"\n");
            System.out.println("Ej: '/home/alex/Desktop/info288/INFO288/p2/INFO288/P2/Problema_1/slave1/ropa.log'"+"\n");
            
            Thread.sleep(2000);



            if (apodo.equals("")) {
                System.out.println("No se ha asignado un apodo, por lo que no se podrán enviar mensajes al servidor.");
            }
            else {
  


                // Una vez ingresado el usuario, creamos un pointer_<apodo>.txt el cual solo tendrá la última línea del archivo
                // Este archivo se actualizará cuando leamos el archivo original
                
                // Si ya existe un archivo con el nombre del apodo, lo leemos en vez de crearlo

                String pointerPath = "pointer_" + apodo + ".txt";
                File pointer = new File(pointerPath);
                int pointerPosition = 0;

                if (pointer.exists()) {
                    Scanner pointerReader = new Scanner(pointer);
                    pointerPosition = Integer.parseInt(pointerReader.nextLine().split(";")[0]);
                    pointerReader.close();
                    
                } else {
                    pointer.createNewFile();
                    // agregamos la primera línea al archivo pointer_<apodo>.txt
                    // Es decir: 0
                    PrintStream pointerWriter = new PrintStream(pointer);
                    pointerWriter.println(pointerPosition);
                    pointerWriter.close();
                }


                String path = "";
                // El usuario define el path del archivo que desea enviar al servidor
                while (ent.hasNextLine()){
                    msg = ent.nextLine();

                    try {
                    Scanner file = new Scanner(new File(msg));
                    // Abrimos el archivo como Cliente
                    // Verificamos que el archivo exista
                        if (file.hasNextLine()) {
                            path = msg;
                            break;
                        } else {
                            System.out.println("El archivo no existe, por favor, escribe el Path del archivo que deseas enviar al servidor."+"\n");
                        }
                    } catch (Exception e) {
                        System.out.println("El archivo no existe, por favor, escribe el Path del archivo que deseas enviar al servidor."+"\n");
                    }
                }
                Scanner file = new Scanner(new File(path));
                // Nos movemos a la linea <pointerPosition> del archivo
                for (int i = 0; i < pointerPosition; i++) {
                    file.nextLine();
                }

                System.out.println("El puntero está en la línea: " + pointerPosition + "\n");
                while (true) {
                    // Revisamos el Archivo cada 10 segundos
                    // Si el archivo tiene una nueva línea, se envía al servidor
                    if (file.hasNextLine()) {
                        try{
                            pointerPosition++;
                            String line = file.nextLine();
                            // INFO:root:1685145276;buscar_categorias;word_['ropa', 'computacion'];ini
                            // A line le agregamos ;timestamp;apodo
                            line = pointerPosition +";"+ line + ";" + System.currentTimeMillis() + ";" + apodo;
                            String response = service.registrarLog(client, apodo, line);
                            System.out.println(response);
                            
                            // Actualizamos el puntero
                            PrintStream pointerWriter = new PrintStream(pointer);
                            pointerWriter.println(pointerPosition);
                            pointerWriter.close();
                        }
                        catch (Exception e) {
                            System.out.println("Error al enviar el mensaje al servidor.");
                        }
                    }
                    else {
                        // Si no hay nuevas lineas, duerme por 10 segundos y revisa el archivo file
                        Thread.sleep(10000);
                        System.out.println("No hay nuevas líneas en el archivo, se revisará nuevamente en 10 segundos.");
                        try{
                            file = new Scanner(new File(path));
                            for (int i = 0; i < pointerPosition; i++) {
                                file.nextLine();
                            }                   
                        }
                        catch (Exception e) {
                            System.out.println("Error al leer el archivo.");
                        }     
                    }
                    // if (ent.hasNextLine() && ent.nextLine().equals(msgExit)) {
                    //     System.out.println("Saliendo del chat..."+"\n");
                    //     String response = service.registrarLog(client, apodo, msgExit);
                    //     System.out.println(response);
                    //     break;
                    // }


                    Thread.sleep(500);

                }
            }


            // Cerramos el Scanner y el cliente
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
