
import java.util.*;
import java.rmi.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

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

            // ------- Validación de formato && Exit -------
            // Si el mensaje es no tiene 3 <;>, no es válido
            // Pero si el mensaje es 'EXIT' o comienza con 'cerrar sesion;', es válido
            if (contador != 3) { 
                if (log.equals("EXIT")) {
                    // Si el mensaje es 'EXIT', registra su salida
                    // Junta el apodo, fecha y hora en un solo string
                    LocalDateTime tiempoAhora = LocalDateTime.now();
                    DateTimeFormatter fecha = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    DateTimeFormatter hora = DateTimeFormatter.ofPattern("HH:mm:ss");
                    String clienteFechaHora = apodo + ";" + tiempoAhora.format(fecha) + ";" + tiempoAhora.format(hora);
            
                    // ------ Inserta en la última línea ------
                    // Abre el archivo en modo de añadir al final, agrega el mensaje y cierra el archivo
                    FileWriter archivoEscritura = new FileWriter(archivo, true);
                    archivoEscritura.write(log  + ";" + clienteFechaHora + "\n"); // EXIT;apodo;fecha_sv;hora_sv
                    archivoEscritura.close();
                    return "--- ok ---";
                }
                return "--- Formato incorrecto. ---";
            }
            
            // Si el mensaje contiene 'inicio de sesion;' lo registra
            if (log.startsWith("inicio de conexion;")) {
                // Si el mensaje es 'inicio de sesion;', registra su entrada
                // Junta el apodo, fecha y hora en un solo string
                LocalDateTime tiempoAhora = LocalDateTime.now();
                DateTimeFormatter fecha = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter hora = DateTimeFormatter.ofPattern("HH:mm:ss");
                String clienteFechaHora = apodo + ";" + tiempoAhora.format(fecha) + ";" + tiempoAhora.format(hora);
        
                // ------ Inserta en la última línea ------
                // Abre el archivo en modo de añadir al final, agrega el mensaje y cierra el archivo
                FileWriter archivoEscritura = new FileWriter(archivo, true);
                archivoEscritura.write(log  + ";" + clienteFechaHora + "\n"); // inicio de sesion;apodo;fecha_sv;hora_sv
                archivoEscritura.close();
                return "--- ok ---";
            }

            // ------- Validación de log, si existe -------
            // Verificamos si el mensaje 'log' ya existe en el archivo
            // Si existe, no lo agregamos
            Scanner archivoLectura = new Scanner(new File(archivo));
            while (archivoLectura.hasNextLine()) {
                String linea = archivoLectura.nextLine();
                // Si la linea de un cliente1: ej: 1;__;__;__ se repite 1;__;__;__;cliente1
                // Contiene el mismo identificador correlativo, y el mismo usuario (apodo), entonces ya existe
                String[] linea_split = linea.split(";");
                if (linea_split[0].equals(log.split(";")[0]) && linea_split[4].equals(apodo)) {
                    return "--- La instrucción ya existe. (nº correlativo) ---";
                }
            }
            
            // ------- Validación de log (formatos) -------
            String[] log_split = log.split(";");
            // Verifica si el primer elemento es un número
            try {
                Integer.parseInt(log_split[0]);
            } catch (NumberFormatException e) {
                return "--- El primer elemento debe ser un número. ---";
            }
            // Verifica si el segundo elemento es una fecha
            try {
                DateTimeFormatter formatYear = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate f = LocalDate.parse(log_split[1], formatYear);
            } catch (Exception e) {
                return "--- El segundo elemento debe ser una fecha yyyy-MM-dd. ---";
            }
            // Verifica si el tercer elemento es una hora
            try {
                DateTimeFormatter formatHour = DateTimeFormatter.ofPattern("HH:mm:ss");
                LocalTime f = LocalTime.parse(log_split[2], formatHour);
            } catch (Exception e) {
                return "--- El tercer elemento debe ser una hora HH:mm:ss. ---";
            }
            // Verifica si el cuarto elemento es un string
            try {
                String.valueOf(log_split[3]);
            } catch (Exception e) {
                return "--- El cuarto elemento debe ser un string, instrucción. ---";
            }

            // ------- Si el mensaje es válido, registra su entrada -------
            // Junta el apodo, fecha y hora en un solo string
            LocalDateTime tiempoAhora = LocalDateTime.now();
            DateTimeFormatter fecha = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter hora = DateTimeFormatter.ofPattern("HH:mm:ss");
            String clienteFechaHora = apodo + ";" + tiempoAhora.format(fecha) + ";" + tiempoAhora.format(hora);

            // ------ Inserta en la última línea ------ 
            // Abre el archivo en modo de añadir al final, agrega el mensaje y cierra el archivo
            FileWriter archivoEscritura = new FileWriter(archivo, true);
            archivoEscritura.write(log  + ";" + clienteFechaHora + "\n");
            archivoEscritura.close();
            return "--- ok ---";

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "--- Algo inesperado ocurrio. ---";
    }
}
