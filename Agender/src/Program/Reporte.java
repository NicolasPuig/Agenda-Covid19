/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Program;

import Planificador.MLQ;
import Planificador.Solicitud;
import Util.ManejadorArchivos;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Seba Mazzey
 */
public class Reporte extends Thread {

    private int cantProd;
    private int cantArch;
    private int diasTotales;
    private boolean imprimirListaAgendados;
    private Semaphore semProductores;
    private static Semaphore semReportes = new Semaphore(0);
    private int agendadosTotales;

    public Reporte(int dTotales, int cantProd, int cantArch,
            boolean imprimirListaAgendados) {
        super("Reporte");
        this.cantProd = cantProd;
        this.cantArch = cantArch;
        this.diasTotales = dTotales;
        this.imprimirListaAgendados = imprimirListaAgendados;
        this.semProductores = Productor.getSemaforoProductores();
    }

    public static Semaphore getSemReportes() {
        return semReportes;
    }

    public void generarArchivoReporteTotal(int vacDisp, int personasEnEspera) {
        String texto
                = "--- REPORTE TOTAL ---"
                + "\nESTADISTICAS"
                + "\n - Cantidad Agendados:\t" + agendadosTotales
                + "\n - Cantidad Solicitudes en espera:\t" + personasEnEspera
                + "\n - Vacunas disponibles:\t" + vacDisp;
        ManejadorArchivos.escribirArchivo("src/Archivos/total.txt", texto, false);
    }

    public void generarArchivoReporteDiario(int dia, boolean imprimirListaAgendados,
            Solicitud[] solicitudes, int personasEnEspera, int vacDisp) {
        String texto
                = "--- REPORTE DIA " + dia + " ---"
                + "\nESTADISTICAS"
                + "\n - Cantidad Agendados:\t" + solicitudes.length
                + "\n - Cantidad Solicitudes en espera:\t" + personasEnEspera
                + "\n - Vacunas disponibles:\t" + vacDisp;
        if (imprimirListaAgendados) {
            texto += "\n\nLISTA DE AGENDADOS";
            for (int i = 0; i < solicitudes.length; i++) {
                texto += "\n" + i + ")\t" + solicitudes[i].toString();
            }
        }
        ManejadorArchivos.escribirArchivo(getPath(dia), texto, false);
    }

    private String getPath(int dia) {
        return "src/Archivos/dia_" + (dia < 10 ? "0" : "") + dia + ".txt";
    }

    @Override
    public void run() {
        int vacDisp = 0;
        int personasEnEspera = 0;
        for (int i = 0; i < diasTotales; i++) {
            // Espero que los productores terminen de producir
            semReportes.acquireUninterruptibly(cantProd);
            // Espero a que se queden sin vacunas o sin personas para agendar
            Agendador.acquireAll();
            // Recupero el Archivo de salida y proceso los datos
            Solicitud[] agendados = Agendador.getSolicitudesSalida();
            Agendador.limpiarBufferSalida();
            // Los productores y arch se pueden despertar aca
            vacDisp = MLQ.MLQ.getVacunasDisponibles();
            personasEnEspera = MLQ.MLQ.getLargoColaEspera();
            this.agendadosTotales += agendados.length;
            // Creo el reporte diario
            System.out.println("Comenzo escritura de reporte");
            this.generarArchivoReporteDiario(i+1, imprimirListaAgendados,
                    agendados, personasEnEspera, vacDisp);
            System.out.println("Termino reporte para " + agendados.length + " solicitudes");
            // Despierto a los productores y archivadores
            semProductores.release(cantProd);
            Agendador.releaseAll();
        }
        // Creo el reporte total
        System.out.println("Nada que reportar! se termina el programa");
        this.generarArchivoReporteTotal(vacDisp, personasEnEspera);
    }
}
