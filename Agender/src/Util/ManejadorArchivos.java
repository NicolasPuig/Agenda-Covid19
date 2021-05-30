package Util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * @author NicoPuig
 */
public class ManejadorArchivos {

    public static void generarArchivoEntradaConMomentos(String path, int cantidadMomentos, int id) {
        String separator = ";";
        int ci = 100000 * id;
        LinkedList<String> lineas = new LinkedList<>();
        lineas.add("Momento;CI;edad;riesgo");
        for (int momento = 1; momento <= cantidadMomentos; momento++) {
            int cantidadPorMomento = (int) (Math.random() * (250 - 100)) + 100;
            for (int i = 0; i < cantidadPorMomento; i++) {
                int edad = (int) (Math.floor(Math.random() * (90 - 18)) + 18);
                int riesgo = (edad > 65 || Math.random() > 0.9) ? (int) (Math.ceil(Math.random() * 5)) : 0;
                String linea = String.join(separator, String.valueOf(momento), String.valueOf(ci++), String.valueOf(edad), String.valueOf(riesgo));
                lineas.add(linea);
            }
        }
        escribirArchivo(path, lineas, false);
    }

    public static Collection<String> leerArchivo(String path, boolean ignoreHeader) {
        Collection<String> lineas = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String linea =  br.readLine();
            if(ignoreHeader){
                linea = br.readLine();
            }
            while (linea != null) {
                lineas.add(linea);
                linea = br.readLine();
            }
        } catch (IOException ex) {
            System.out.println("Error al leer el archivo " + path + "\n" + ex);
        }
        return lineas;
    }

    public static void escribirArchivo(String path, String texto, boolean append) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, append))) {
            bw.write(texto);
        } catch (IOException e) {
            System.out.println("Error al escribir el archivo " + path);
        }
    }

    public static void escribirArchivo(String path, Object[] lines, boolean append) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, append))) {
            for (Object lineaActual : lines) {
                bw.write(lineaActual.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error al escribir el archivo " + path);
        }
    }

    public static void escribirArchivo(String path, LinkedList lines, boolean append) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, append))) {
            for (Object lineaActual : lines) {
                bw.write(lineaActual.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error al escribir el archivo " + path);
        }
    }

    public static void limpiarArchivo(String path) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, false))) {
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error al escribir el archivo " + path);
        }
    }

    public static void borrarArchivos() {
        int i = 1;
        while (new File("src/Archivos/dia_" + (i < 10 ? "0" : "") + i + ".txt").delete()) {
            i++;
        }
    }
}
