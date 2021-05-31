package Modelado;

import Planificador.MLQ;
import java.util.concurrent.Semaphore;
import Util.ManejadorArchivos;
import java.util.Collection;

/**
 *
 * @author PaoloMazza, SebaMazzey, NicoPuig
 */
public class DespachadorVacunas extends Thread {

    private String archivoVacunas;
    private Semaphore semDespachador;

    public DespachadorVacunas(String archivoVacunas) {
        super("Despachador-vacunas");
        this.archivoVacunas = archivoVacunas;
        this.semDespachador = Despachador.getSemaforoDespachadores();
    }

    @Override
    public void run() {
        Collection<String> lineas = ManejadorArchivos.leerArchivo(archivoVacunas, true);
        for (String linea : lineas) {
            String[] datos = linea.split(":");
            if (datos.length != 2) {
                System.out.println("Error al cargar vacunas en linea: " + linea);
                break;
            }
            int vacunasEntrantes = Integer.parseInt(datos[1].trim());
            MLQ.MLQ.agregarVacunas(vacunasEntrantes);
            Reporte.getSemReportes().release();
            semDespachador.acquireUninterruptibly();
        }
    }
}
