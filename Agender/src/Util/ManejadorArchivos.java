package Util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Stream;

/**
 *
 * @author PaoloMazza, SebaMazzey, NicoPuig
 */
public class ManejadorArchivos {

    public static void generarArchivoEntradaVacunas(String path, int cantidadMomentos, int minVacunas, int maxVacunas) {
        String texto = "Vacunas Entrantes";
        for (int momento = 1; momento <= cantidadMomentos; momento++) {
            texto += "\ndia " + momento + ": " + (int) (Math.random() * (maxVacunas - minVacunas) + minVacunas);
        }
        escribirArchivo(path, texto, false);
    }

    public static void generarArchivosEntradaSolicitudes(String path, int cantidadMomentos, int id, int minPersonasPorMomento, int maxPersonasPorMomento, float probabilidadRiesgo) {
        String separator = ";";
        int ci = 100000 * id;
        LinkedList<String> lineas = new LinkedList<>();
        String[] departamentos = {"Tacuarembó", "Treinta y Tres", "Soriano", "San José", "Salto", "Rocha", "Rivera", "Río Negro", "Paysandú", "Montevideo", "Maldonado", "Lavalleja", "Florida", "Durazno", "Colonia", "Cerro Largo", "Canelones", "Artigas", "Flores"};
        lineas.add("Momento;CI;edad;riesgo;departamento");
        for (int momento = 1; momento <= cantidadMomentos; momento++) {
            int cantidadPorMomento = (int) (Math.random() * (maxPersonasPorMomento - minPersonasPorMomento)) + minPersonasPorMomento;
            for (int i = 0; i < cantidadPorMomento; i++) {
                int edad = (int) (Math.floor(Math.random() * (80 - 18)) + 18);
                int riesgo = (edad > 65 || Math.random() > 1 - probabilidadRiesgo) ? (int) (Math.ceil(Math.random() * 5)) : 0;
                String departamento = departamentos[(int) (Math.random() * (departamentos.length))];
                String linea = String.join(separator, String.valueOf(momento), String.valueOf(ci++),
                        String.valueOf(edad), String.valueOf(riesgo), departamento);
                lineas.add(linea);
            }
        }
        escribirArchivo(path, lineas, false);
    }

    public static Collection<String> leerArchivo(String path, boolean ignoreHeader) {
        Collection<String> lineas = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String linea = br.readLine();
            if (ignoreHeader) {
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

    public static void borrarArchivosSalida() {
        int i = 1;
        File[] carpetasSalida = new File("src/Archivos/").listFiles(file -> file.getName().startsWith("Salida"));
        for (File carpeta : carpetasSalida) {
            for (File archivo : carpeta.listFiles()) {
                archivo.delete();
            }
        }
    }
}
