/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Program;

import Planificador.MLQ;
import java.util.concurrent.Semaphore;
import Util.ManejadorArchivos;
import java.util.Collection;

/**
 *
 * @author Seba Mazzey
 */
public class ProductorVacunas extends Thread {

    private String archivoVacunas;
    private Semaphore semProductores;

    public ProductorVacunas(String archivoVacunas) {
        super("P-vacunas");
        this.archivoVacunas = archivoVacunas;
        this.semProductores = Productor.getSemaforoProductores();
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
            semProductores.acquireUninterruptibly();
        }
    }
}
