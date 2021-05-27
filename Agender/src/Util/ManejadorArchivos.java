package Util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

/**
 * TODO: Cheaquear que funcione
 * @author NicoPuig
 */
public class ManejadorArchivos {

    public static Collection<String> leerArchivo(String path) {
        Collection<String> lineas = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String linea = "";
            while (linea != null) {
                linea = br.readLine();
                lineas.add(linea);
            }
        } catch (IOException ex) {
            System.out.println("Error al leer el archivo " + path + "\n" + ex);
        }
        return lineas;
    }

    public static void escribirArchivo(String nombreCompletoArchivo, String[] listaLineasArchivo, boolean append) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nombreCompletoArchivo, append))) {
            for (String lineaActual : listaLineasArchivo) {
                bw.write(lineaActual);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error al escribir el archivo " + nombreCompletoArchivo);
        }
    }
}
