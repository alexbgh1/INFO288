
import java.util.*;

import javax.crypto.Cipher;

import java.rmi.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

class ServicioLogImpl implements ServicioLog {
    List<Cliente> listaClientes;
    String archivo = "data/data.txt";

    // Leemos config_secret.txt
    String secret = "config_secret.txt";
    // Valores por defecto
    String SECRETKEY_S = "5";
    int SECRETKEY = Integer.parseInt(SECRETKEY_S);
    String SECRETKEY_2 = "0123456789tttddd";
    SecretKeySpec secretKey;


    ServicioLogImpl() throws RemoteException {
        listaClientes = new LinkedList<Cliente>();
    }

    public String loadSecret() throws RemoteException{
        File secretFile = new File(secret);
        try {
            Scanner secretReader = new Scanner(secretFile);
            SECRETKEY_S = secretReader.nextLine().split("=")[1]; // "SECRETKEY"="20"
            SECRETKEY = Integer.parseInt(SECRETKEY_S);
            SECRETKEY_2 = secretReader.nextLine().split("=")[1]; // "SECRETKEY_2"="0123456789tttddd"
            secretReader.close();
        } catch (Exception e) {
            System.err.println("Exception:");
            e.printStackTrace();
            return "Error al leer el archivo de configuración secreto.";
        }

        return "Llave secreta cargada correctamente.";
    }

    public void alta(Cliente client) throws RemoteException {
	    listaClientes.add(client);
    }

    public void baja(Cliente client) throws RemoteException {
        listaClientes.remove(listaClientes.indexOf(client));
    }

    public String registrarLog(Cliente c, String apodo, String log) throws RemoteException {


        try {
            // ------ Inserta en la última línea ------ 
            // Abre el archivo en modo de añadir al final, agrega el mensaje y cierra el archivo
            FileWriter archivoEscritura = new FileWriter(archivo, true);

            // DESENCRIPTA EL MENSAJE
            byte[] claveBytes = SECRETKEY_2.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(claveBytes, "AES");

            log = desencriptar_2(log, secretKey);
            if (log == "Error al desencriptar")
            {
                archivoEscritura.write("Error al desencriptar ; "+ apodo + "; " + System.currentTimeMillis()/1000  + "\n");
                archivoEscritura.close();
                return "--- Error al desencriptar ---";
            }
            log = desencriptar(log, SECRETKEY);

            archivoEscritura.write(log  + "; " + System.currentTimeMillis()/1000  + "\n");
            archivoEscritura.close();
            return "--- ok ---";

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "--- Algo inesperado ocurrio. ---";
    }

    public static String desencriptar_2(String s, SecretKeySpec key) {
        String mensajeEncriptadoString = s.substring(1, s.length() - 1); // Remover los corchetes al inicio y al final
        String[] valoresString = mensajeEncriptadoString.split(", "); // Dividir los valores por coma y espacio

        byte[] mensajeEncriptado = new byte[valoresString.length];
        for (int i = 0; i < valoresString.length; i++) {
            mensajeEncriptado[i] = Byte.parseByte(valoresString[i]);
        }
        try{
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] mensajeDesencriptado = cipher.doFinal(mensajeEncriptado);
            String mensajeOriginal = new String(mensajeDesencriptado, StandardCharsets.UTF_8);
            return mensajeOriginal;

        }catch(Exception e){
            e.printStackTrace();
        }

        return "Error al desencriptar";
    }

    // Recibe una cadena de texto, la encripta y la retorna
    public static String desencriptar(String s, int key) {
        String desEncriptado = "";
        for (int i = 0; i < s.length(); i++) {
            desEncriptado += desEncriptarChar(s.charAt(i), key);
        }
        return desEncriptado;
    }

    public static char desEncriptarChar(char c, int key) {
        return (char) (c - key);
    }
}
