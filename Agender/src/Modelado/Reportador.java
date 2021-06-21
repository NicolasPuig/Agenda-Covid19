package Modelado;

import Planificador.MLQ;
import Util.ManejadorArchivos;
import java.util.LinkedList;
import java.util.Map;
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
        Estadistica estadisticaTotal = Agenda.AGENDA.getEstadisticaTotal();
        String texto
                = "--- REPORTE TOTAL ---"
                + "\nESTADISTICAS"
                + "\n - Solicitudes en espera:\t" + personasEnEspera
                + "\n - Vacunas disponibles:\t" + vacunasDisponibles
                + "\n" + estadisticaTotal.toString();
        ManejadorArchivos.escribirArchivo("src/Archivos/reporteTotal.txt", texto, false);
    }

    private void generarArchivoReporteDiario(int momento, Map<String, LinkedList<Vacunatorio>> agendados, int personasEnEspera, int vacunasDisponibles) {
        try {

            Estadistica estadisticaDiariaTotal = Agenda.AGENDA.getEstadisticaDiaria();
            String texto = "--- REPORTE DIA " + momento + " ---\n" + estadisticaDiariaTotal.toString()
                    + "\n-Solicitudes en espera:\t" + personasEnEspera
                    + "\n-Vacunas disponibles:\t" + vacunasDisponibles;
            for (Map.Entry<String, LinkedList<Vacunatorio>> departamento : agendados.entrySet()) {
                texto += "\n\t" + departamento.getKey();
                for (Vacunatorio vacunatorio : departamento.getValue()) {
                    DiaAgenda dia = vacunatorio.removerDiaActual(momento);
                    texto += "\n\t\t" + vacunatorio.getNombre()
                            + "\n\t\t\t" + dia.getEstadisticaDiaria() + "\n";
                }
            }
            ManejadorArchivos.escribirArchivo(getPath(momento), texto, false);
        } catch (ArithmeticException e) {
            System.out.println(e);
        }
    }

    private String getPath(int dia) {
        return "src/Archivos/reporteDia_" + (dia < 10 ? "0" : "") + dia + ".txt";
    }

    @Override
    public void run() {
        int vacunasDisponibles = 0;
        int personasEnEspera = 0;
        for (int momento = 1; momento <= cantidadMomentos; momento++) {
            try {
                System.out.println("Dia " + momento);
                // Espero que los despachadores terminen de producir solicitudes
                semReportes.acquireUninterruptibly(cantidadDespachadores);
                // Espero a que se queden sin vacunas o sin personas para agendar
                Agendador.acquireAll();
                // Recupero el Archivo de salida y proceso los datos
                Map<String, LinkedList<Vacunatorio>> agendados = Agendador.getSolicitudesAgendadas();
                // Los productores y arch se pueden despertar aca
                vacunasDisponibles = MLQ.MLQ.getVacunasDisponibles();
                personasEnEspera = MLQ.MLQ.getLargoColaEspera();
                // Creo el reporte diario
                System.out.println("Fin del dia\nComienza escritura de reporte");
                this.generarArchivoReporteDiario(momento, agendados,
                        personasEnEspera, vacunasDisponibles);
                System.out.println("Termino reporte dia " + momento + " solicitudes\n");
                // Despierto a los despachadores y agendadores
                semDespachador.release(cantidadDespachadores);
                Agendador.releaseAll();
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
        // Creo el reporte total
        System.out.println("Nada mas que reportar! Se termina el programa");
        this.generarArchivoReporteTotal(vacunasDisponibles, personasEnEspera);
    }
}
