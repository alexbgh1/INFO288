
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.util.*;
import java.io.PrintStream;
import java.io.File;

import java.nio.charset.StandardCharsets;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

class ClienteLog {

    private static final String CONFIG_PATH = "/home/alex/Desktop/info288/INFO288/p3/INFO288/P3/Problema_1/centralized_log/appClient/config.txt";

    public static String apodo = "";
    public static String path = "";

    static public void main(String args[]) {

        if (args.length != 2) {
            System.err.println("Uso: java ClienteLog <apodo> <path>");
            System.err.println("Uso: java ClienteLog alex /home/alex/Desktop/info288/INFO288/p2/INFO288/P2/Problema_1/slave2/computacion.log");
            return;
        }

        apodo = args[0];
        path = args[1];

        // Leemos CONFIG_PATH
        File configFile = new File(CONFIG_PATH);
        // Valores por defecto
        String IP_S = "127.0.0.1";
        String PORT_S = "4000";
        String SECRETKEY_S = "a1b2c3d4";
        String SECRETKEY_S_2 = "0123456789tttddd";


        try {
            Scanner configReader = new Scanner(configFile);
            IP_S = configReader.nextLine().split("=")[1]; // "IP"="127.0.0.1"
            PORT_S = configReader.nextLine().split("=")[1]; // "PORT"="4000"
            SECRETKEY_S = configReader.nextLine().split("=")[1]; // "SECRETKEY"="20"
            SECRETKEY_S_2 = configReader.nextLine().split("=")[1]; // "SECRETKEY_2"="20"
            configReader.close();
        } catch (Exception e) {
            System.err.println("Exception:");
            e.printStackTrace();
            System.exit(1);
        }
        int PORT = Integer.parseInt(PORT_S);
        SecretKeySpec SECRETKEY_2 = new SecretKeySpec(SECRETKEY_S_2.getBytes(StandardCharsets.UTF_8), "AES");


        System.out.println("Apodo: " + apodo);
        System.out.println("Path: " + path);

        try {

            // Definimos el encoding de la consola
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
            
            // Registramos el cliente en el servidor para hacer uso de los métodos del servicio
            Registry registry = LocateRegistry.getRegistry(IP_S, PORT);
            ServicioLog service = (ServicioLog) registry.lookup("Log");

            // Creamos un objeto de la clase ClienteImpl
            ClienteImpl client = new ClienteImpl();

            // Agregamos el cliente al LinkedList de clientes del servicio
            service.alta(client);
            // Cargamos el secreto del servidor
            service.loadSecret();

            // Creamos un objeto de la clase Scanner para leer los mensajes del usuario


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


            // Validación del path del archivo, si existe continuamos, si no, terminamos la ejecución del programa
            try {
            Scanner file = new Scanner(new File(path));
            // Abrimos el archivo como Cliente
            // Verificamos que el archivo exista
                if (file.hasNextLine()) {
                    System.out.println("El archivo existe, se enviará al servidor."+"\n");
                } else {
                    System.out.println("El archivo no existe, por favor, escribe el Path del archivo que deseas enviar al servidor."+"\n");
                    // Termina la ejecución del programa
                    System.exit(1);
                }
            } catch (Exception e) {
                System.out.println("El archivo no existe, por favor, escribe el Path del archivo que deseas enviar al servidor."+"\n");
                System.exit(1);
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
                        // 1685145276;buscar por categorias;ini
                        // A line le agregamos ;timestamp;apodo
                        // 1685145276;buscar por categorias;ini;1625145276;alex
                        line =  line + "; " + System.currentTimeMillis()/1000 + "; " + apodo;
                        
                        // ENCRIPTAMOS
                        line = encriptar(line, SECRETKEY_S);
                        // System.out.println("Se enviará la línea: " + line); // ascii + secret
                        line = encriptar_2(line, SECRETKEY_2);
                        // System.out.println("Se enviará la línea: " + line); // (ascii + secret) cipher

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

                Thread.sleep(100);
            }

        // Cerramos el Scanner y el cliente
        // service.baja(client);
        // System.exit(0);

        } catch (RemoteException e) {
            System.err.println("Error de comunicacion: " + e.toString());
        } catch (Exception e) {
            System.err.println("Excepcion en ClienteLog:");
            e.printStackTrace();
        }
    }

    public static String encriptar_2(String s, SecretKeySpec key){
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] mensajeEncriptado = cipher.doFinal(s.getBytes(StandardCharsets.UTF_8));
            return Arrays.toString(mensajeEncriptado);

        }
        catch (Exception e) {
            System.err.println("Exception:");
            e.printStackTrace();
            System.exit(1);
        }
        return "something went wrong";

    }

    // Recibe una cadena de texto, la encripta y la retorna
    public static String encriptar(String s, String secret) {
        String encriptado = "";
        for (int i = 0; i < s.length(); i++) {
            // Si secret = 'a1b2c3d4': Tomamos el primer caracter de secret y lo sumamos al primer caracter de s
            // Seleccionamos el valor ascii en la posición i de secret
            int ascii = (int) secret.charAt(i % secret.length());
            int ascii2 = ((ascii * 3) + 7);

            if ((ascii >= 65 && ascii <= 90) || (ascii >= 97 && ascii <= 122)) {
                ascii = ascii2;
            }

            encriptado += (char) (s.charAt(i) + ascii + 3);
        }
        return encriptado;
    }

    
}
