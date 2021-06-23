package Hilos;

import static Planificador.MLQ.MLQ;
import Util.ManejadorArchivos;
import java.util.Collection;

/**
 *
 * @author PaoloMazza, SebaMazzey, NicoPuig
 */
public class DespachadorVacunas extends Thread {

    private final String archivoVacunas;

    public DespachadorVacunas(String archivoVacunas) {
        super("Despachador-vacunas");
        this.archivoVacunas = archivoVacunas;
    }

    @Override
    public void run() {
        Collection<String> lineas = ManejadorArchivos.leerArchivo(archivoVacunas, true);
        try {
            for (String linea : lineas) {
                String[] datos = linea.split(":");
                if (datos.length != 2) {
                    System.out.println("Error al cargar vacunas en linea: " + linea);
                    break;
                }
                int vacunasEntrantes = Integer.parseInt(datos[1].trim());
                MLQ.agregarVacunas(vacunasEntrantes);
                Reportador.release();
                Despachador.acquire();
            }
        } catch (InterruptedException | NumberFormatException ex) {
            System.out.println(ex);
        }
    }
}
