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
public class Reporte2 extends Thread{
    private int cantProd;
    private int cantArch;
    private int diasTotales;
    private boolean imprimirListaAgendados;
    private Semaphore semProductores;
    private static Semaphore semReportes = new Semaphore(0);
    private int agendadosTotales;
    
    public Reporte2(int dTotales, int cantProd, int cantArch,
            boolean imprimirListaAgendados, Semaphore semProductores) {
        this.cantProd = cantProd;
        this.cantArch = cantArch;
        this.diasTotales = dTotales;
        this.imprimirListaAgendados = imprimirListaAgendados;
        this.semProductores = semProductores;
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
            Archivador.getMlqMutex().acquireUninterruptibly(cantArch);
            // Recupero el Archivo de salida y proceso los datos
            Solicitud[] agendados = Archivador.getSolicitudesSalida();
            Archivador.limpiarBufferSalida();
            // Los productores y arch se pueden despertar aca
            vacDisp = MLQ.MLQ.getVacunasDisponibles();
            personasEnEspera = MLQ.MLQ.getLargoColaEspera();
            this.agendadosTotales += agendados.length;
            // Creo el reporte diario
            System.out.println("Comenzo escritura de reporte");
            this.generarArchivoReporteDiario(i, imprimirListaAgendados,
                    agendados, personasEnEspera, vacDisp);
            System.out.println("Termino reporte para " + agendados.length + " solicitudes");
            // Despierto a los productores y archivadores
            semProductores.release(cantProd);
            Archivador.getMlqMutex().release(cantArch);
        }
        // Creo el reporte total
        System.out.println("Nada que reportar! se termina el programa");
        this.generarArchivoReporteTotal(vacDisp, personasEnEspera);
    }
}
