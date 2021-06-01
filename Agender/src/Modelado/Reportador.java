package Modelado;

import Planificador.MLQ;
import Util.ManejadorArchivos;
import java.util.concurrent.Semaphore;

/**
 *
 * @author PaoloMazza, SebaMazzey, NicoPuig
 */
public class Reportador extends Thread {

    private static Semaphore semReportes = new Semaphore(0);

    private int cantidadDespachadores;
    private int cantidadAgendadores;
    private int cantidadMomentos;
    private boolean imprimirListaAgendados;
    private Semaphore semDespachador;
    private int agendadosTotales;

    public Reportador(int cantidadMomentos, int cantidadDespachadores, int cantidadAgenadores,
            boolean imprimirListaAgendados) {
        super("Reporte");
        this.cantidadDespachadores = cantidadDespachadores;
        this.cantidadAgendadores = cantidadAgenadores;
        this.cantidadMomentos = cantidadMomentos;
        this.imprimirListaAgendados = imprimirListaAgendados;
        this.semDespachador = Despachador.getSemaforoDespachadores();
    }

    public static Semaphore getSemReportes() {
        return semReportes;
    }

    public void generarArchivoReporteTotal(int vacunasDisponibles, int personasEnEspera) {
        String texto
                = "--- REPORTE TOTAL ---"
                + "\nESTADISTICAS"
                + "\n - Cantidad Agendados:\t" + agendadosTotales
                + "\n - Cantidad Solicitudes en espera:\t" + personasEnEspera
                + "\n - Vacunas disponibles:\t" + vacunasDisponibles;
        ManejadorArchivos.escribirArchivo("src/Archivos/reporteTotal.txt", texto, false);
    }

    public void generarArchivoReporteDiario(int momento,
            Solicitud[] solicitudes, int personasEnEspera, int vacunasDisponibles) {
        String texto
                = "--- REPORTE DIA " + momento + " ---"
                + "\nESTADISTICAS"
                + "\n - Cantidad Agendados:\t" + solicitudes.length
                + "\n - Cantidad Solicitudes en espera:\t" + personasEnEspera
                + "\n - Vacunas disponibles:\t" + vacunasDisponibles;
        if (imprimirListaAgendados) {
            texto += "\n\nLISTA DE AGENDADOS";
            for (int i = 0; i < solicitudes.length; i++) {
                solicitudes[i].setMomentoFinSolicitud(momento);
                texto += "\n" + i + ")\t" + solicitudes[i].toString();
            }
        }
        ManejadorArchivos.escribirArchivo(getPath(momento), texto, false);
    }

    private String getPath(int dia) {
        return "src/Archivos/reporteDia_" + (dia < 10 ? "0" : "") + dia + ".txt";
    }

    @Override
    public void run() {
        int vacunasDisponibles = 0;
        int personasEnEspera = 0;
        for (int momento = 1; momento <= cantidadMomentos; momento++) {
            System.out.println("Dia " + momento);
            // Espero que los despachadores terminen de producir solicitudes
            semReportes.acquireUninterruptibly(cantidadDespachadores);
            // Espero a que se queden sin vacunas o sin personas para agendar
            Agendador.acquireAll();
            // Recupero el Archivo de salida y proceso los datos
            Solicitud[] agendados = Agendador.getSolicitudesAgendadas();
            // Los productores y arch se pueden despertar aca
            vacunasDisponibles = MLQ.MLQ.getVacunasDisponibles();
            personasEnEspera = MLQ.MLQ.getLargoColaEspera();
            this.agendadosTotales += agendados.length;
            // Creo el reporte diario
            System.out.println("Fin del dia\nComienza escritura de reporte");
            this.generarArchivoReporteDiario(momento, agendados,
                    personasEnEspera, vacunasDisponibles);
            System.out.println("Termino reporte para " + agendados.length + " solicitudes\n");
            // Despierto a los despachadores y agendadores
            semDespachador.release(cantidadDespachadores);
            Agendador.releaseAll();
        }
        // Creo el reporte total
        System.out.println("Nada mas que reportar! Se termina el programa");
        this.generarArchivoReporteTotal(vacunasDisponibles, personasEnEspera);
    }
}
