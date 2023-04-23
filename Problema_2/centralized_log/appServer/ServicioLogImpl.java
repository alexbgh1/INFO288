
import java.util.*;
import java.rmi.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

class ServicioLogImpl implements ServicioLog {
    List<Cliente> listaClientes;
    String archivo = "data/data.txt";

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

            // Validamos que el 'log' contenga solo los valores permitidos
            // Para simplificar contaremos el número de <;> que contenga el 'log'
            // Ej: 
            //     1;2023-01-04;13:10:10;id=3 eliminado tabla4

            int contador = 0;
            for (int i = 0; i < log.length(); i++) {
                if (log.charAt(i) == ';') {
                    contador++;
                }
            }

            // Si el contador es diferente de 3, el log no es válido
            if (contador != 3) {

                if (log.equals("EXIT")) {
                    // Si el mensaje es 'EXIT', registra su salida
                    // Junta el apodo, fecha y hora en un solo string
                    LocalDateTime tiempoAhora = LocalDateTime.now();
                    DateTimeFormatter fecha = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    DateTimeFormatter hora = DateTimeFormatter.ofPattern("HH:mm:ss");
                    String clienteFechaHora = apodo + ";" + tiempoAhora.format(fecha) + ";" + tiempoAhora.format(hora);
            
                    // ------ Insertar al inicio ------
                    // Abre el archivo en modo de lectura y escritura
                    RandomAccessFile archivo_txt = new RandomAccessFile(archivo, "rw");
                    // Lee el contenido actual del archivo y guárdalo en una variable
                    // Para conservar el orden de los mensajes
                    String contenidoActual = "";
                    String linea;
                    while ((linea = archivo_txt.readLine()) != null) {
                        contenidoActual += linea + "\n";
                    }
                    
                    // Mueve el puntero del archivo al inicio
                    archivo_txt.seek(0);

                    // Escribe el nuevo contenido en el archivo, seguido del contenido anterior
                    archivo_txt.writeBytes(log  + ";" + clienteFechaHora + "\n" + contenidoActual);
                    archivo_txt.close();
                    
                    // ------ Inserta en la última línea ------
                    // Abre el archivo en modo de añadir al final, agrega el mensaje y cierra el archivo
                    // FileWriter archivoEscritura = new FileWriter(archivo, true);
                    // archivoEscritura.write(log  + ";" + clienteFechaHora + "\n");
                    // archivoEscritura.close();
                    return "--- ok ---";
                }

                return "--- Formato incorrecto. ---";
            }

            // Verificamos si el mensaje 'log' ya existe en el archivo
            // Si existe, no lo agregamos
            Scanner archivoLectura = new Scanner(new File(archivo));
            while (archivoLectura.hasNextLine()) {
                String linea = archivoLectura.nextLine();
                if (linea.contains(log)) {
                    return "--- El mensaje ya existe. ---";
                }
            }
            
            // Si no existe, lo agregamos
            // Junta el apodo, fecha y hora en un solo string
            LocalDateTime tiempoAhora = LocalDateTime.now();
            DateTimeFormatter fecha = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter hora = DateTimeFormatter.ofPattern("HH:mm:ss");
            String clienteFechaHora = apodo + ";" + tiempoAhora.format(fecha) + ";" + tiempoAhora.format(hora);
    

            // ------ Insertar al inicio ------
            // Abre el archivo en modo de lectura y escritura
            RandomAccessFile archivo_txt = new RandomAccessFile(archivo, "rw");
            // Lee el contenido actual del archivo y guárdalo en una variable
            // Para conservar el orden de los mensajes
            String contenidoActual = "";
            String linea;
            while ((linea = archivo_txt.readLine()) != null) {
                contenidoActual += linea + "\n";
            }
            // Mueve el puntero del archivo al inicio
            archivo_txt.seek(0);

            // Escribe el nuevo contenido en el archivo, seguido del contenido anterior
            archivo_txt.writeBytes(log  + ";" + clienteFechaHora + "\n" + contenidoActual);
            archivo_txt.close();



            // ------ Inserta en la última línea ------ 
            // Abre el archivo en modo de añadir al final, agrega el mensaje y cierra el archivo
            // FileWriter archivoEscritura = new FileWriter(archivo, true);
            // archivoEscritura.write(log  + ";" + clienteFechaHora + "\n");
            // archivoEscritura.close();
            return "--- ok ---";

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "--- Algo inesperado ocurrio. ---";
    }
}
