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

    private final static Semaphore semReportes = new Semaphore(0);
    private final static Semaphore semDespachador = Despachador.getSemaforoDespachadores();

    private final int cantidadDespachadores;
    private final int cantidadMomentos;
    private final boolean imprimirListaAgendados;

    public Reportador(int cantidadMomentos, int cantidadDespachadores,
            boolean imprimirListaAgendados) {
        super("Reporte");
        this.cantidadDespachadores = cantidadDespachadores;
        this.cantidadMomentos = cantidadMomentos;
        this.imprimirListaAgendados = imprimirListaAgendados;
    }

    public static Semaphore getSemReportes() {
        return semReportes;
    }

    public void generarArchivoReporteTotal(int vacunasDisponibles, int personasEnEspera, int momentosTranscurridos) {
        Estadistica entradaTotal = MLQ.MLQ.getEstadisticaTotalEntrada();
        Estadistica salidaTotal = Agenda.AGENDA.getEstadisticaTotalDeSalida();
        String texto
                = "------------ REPORTE TOTAL ------------"
                + "\nSOLICITUDES ENTRANTES\n" + Estadistica.comparar(entradaTotal, salidaTotal)
                + "\n\nSOLICITUDES AGENDADAS\n" + salidaTotal.toString()
                + "\n\nDATOS GENERALES"
                + "\n  -Solicitudes en cola de espera:\t" + personasEnEspera
                + "\n  -Vacunas disponibles:\t" + vacunasDisponibles
                + "\n  -Dias transcurridos:\t" + momentosTranscurridos
                + "\n  -Estado del planificador:\t" + MLQ.MLQ.getEstado();
        ManejadorArchivos.escribirArchivo("src/Archivos/reporteTotal.txt", texto, false);
    }

    private void generarArchivoReporteDiario(int momento, Map<String, LinkedList<Vacunatorio>> agendados, int personasEnEspera, int vacunasDisponibles) {
        try {

            Estadistica estadisticaDiariaEntrada = MLQ.MLQ.getEstadisticaDiariaEntrada();
            Estadistica estadisticaDiariaSalida = Agenda.AGENDA.getEstadisticaDiariaDeSalida();
            String texto
                    = "------------ REPORTE DIA " + momento + " ------------"
                    + "\nSOLICITUDES ENTRANTES\n" + Estadistica.comparar(estadisticaDiariaEntrada, estadisticaDiariaSalida)
                    + "\n\nSOLICITUDES AGENDADAS\n" + estadisticaDiariaSalida.toString()
                    + "\n\nDATOS GENERALES"
                    + "\n  -Solicitudes en cola de espera:\t" + personasEnEspera
                    + "\n  -Vacunas disponibles:\t" + vacunasDisponibles
                    + "\n  -Estado del planificador:\t" + MLQ.MLQ.getEstado()
                    + "\n\nAGENDADO POR DEPARTAMENTO";

            int numDepartamento = 1;
            for (Map.Entry<String, LinkedList<Vacunatorio>> departamento : agendados.entrySet()) {
                int numVacunatorio = 1;
                texto += "\n  " + numDepartamento + ") " + departamento.getKey();
                for (Vacunatorio vacunatorio : departamento.getValue()) {
                    DiaAgenda dia = vacunatorio.removerDiaActual(momento);
                    texto += "\n    " + numDepartamento + "." + numVacunatorio + ") " + vacunatorio.getNombre()
                            + "\n" + dia.getEstadisticaDiaria() + "\n";
                    numVacunatorio++;
                }
                numDepartamento++;
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
        int momento = 1;
        for (; momento <= cantidadMomentos; momento++) {
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
        this.generarArchivoReporteTotal(vacunasDisponibles, personasEnEspera, momento - 1);
    }
}
