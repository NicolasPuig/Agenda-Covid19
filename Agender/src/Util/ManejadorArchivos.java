package Util;

import Planificador.FCFSQueue;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

/**
 * TODO: Cheaquear que funcione
 *
 * @author NicoPuig
 */
public class ManejadorArchivos {

    public static void generarArchivoEntrada(String path, int cantidad) {
        String separator = ";";
        int ci = 100000;
        LinkedList<String> lineas = new LinkedList<>();
        lineas.add("CI;edad;riesgo");
        for (int i = ci; i < ci + cantidad; i++) {
            int edad = (int) (Math.floor(Math.random() * (90 - 18)) + 18);
            int riesgo = (edad > 65 || Math.random() > 0.8) ? (int) (Math.ceil(Math.random() * 5)) : 0;
            String linea = String.join(separator, String.valueOf(i), String.valueOf(edad), String.valueOf(riesgo));
            lineas.add(linea);
        }
        escribirArchivo(path, lineas, false);
    }

    public static Collection<String> leerArchivo(String path, boolean ignoreHeader) {
        Collection<String> lineas = new LinkedList<>();
        try ( BufferedReader br = new BufferedReader(new FileReader(path))) {
            String linea = ignoreHeader ? br.readLine() : "";
            while (linea != null) {
                lineas.add(linea);
                linea = br.readLine();
            }
        } catch (IOException ex) {
            System.out.println("Error al leer el archivo " + path + "\n" + ex);
        }
        return lineas;
    }

    public static void escribirArchivo(String path, String[] lines, boolean append) {
        try ( BufferedWriter bw = new BufferedWriter(new FileWriter(path, append))) {
            for (String lineaActual : lines) {
                bw.write(lineaActual);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error al escribir el archivo " + path);
        }
    }

    public static void escribirArchivo(String path, LinkedList lines, boolean append) {
        try ( BufferedWriter bw = new BufferedWriter(new FileWriter(path, append))) {
            for (Object lineaActual : lines) {
                bw.write(lineaActual.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error al escribir el archivo " + path);
        }
    }

    public static void escribirArchivo(String path, FCFSQueue lines, boolean append) {
        try ( BufferedWriter bw = new BufferedWriter(new FileWriter(path, append))) {
            while (!lines.isEmpty()) {
                bw.write(lines.pop().toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error al escribir el archivo " + path);
        }
    }
}
