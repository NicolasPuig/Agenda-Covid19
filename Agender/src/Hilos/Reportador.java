package Hilos;

import static Planificador.MLQ.MLQ;
import static Modelado.Agenda.AGENDA;
import Modelado.DiaAgenda;
import Modelado.Estadistica;
import Modelado.Vacunatorio;
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

    private final int cantidadDespachadores;
    private final int cantidadMomentos;

    public Reportador(int cantidadMomentos, int cantidadDespachadores) {
        super("Reportador");
        this.cantidadDespachadores = cantidadDespachadores;
        this.cantidadMomentos = cantidadMomentos;
    }

    public static void release() {
        semReportes.release();
    }

    public void generarArchivoReporteTotal(int momentosTranscurridos) {
        Estadistica entradaTotal = MLQ.getEstadisticaTotalEntrada();
        Estadistica salidaTotal = AGENDA.getEstadisticaTotalDeSalida();

        String texto
                = "------------ REPORTE TOTAL ------------"
                + "\nSOLICITUDES ENTRANTES\n" + Estadistica.comparar(entradaTotal, salidaTotal)
                + "\n\nSOLICITUDES AGENDADAS\n" + salidaTotal.toString()
                + "\n\nDATOS GENERALES"
                + "\n  -Solicitudes en cola de espera:\t" + MLQ.getSolicitudesEnEspera()
                + "\n  -Vacunas disponibles:\t" + MLQ.getVacunasDisponibles()
                + "\n  -Vacunas ingresadas:\t" + entradaTotal.getCantidadVacunas()
                + "\n  -Dias transcurridos:\t" + momentosTranscurridos
                + "\n  -Estado del planificador:\t" + MLQ.getEstado();
        ManejadorArchivos.escribirArchivo("src/Archivos/reporteTotal.txt", texto, false);
    }

    private void generarArchivoReporteDiario(int momento, Map<String, LinkedList<Vacunatorio>> agendados) {
        try {
            Estadistica estadisticaDiariaEntrada = MLQ.getEstadisticaDiariaEntrada();
            Estadistica estadisticaDiariaSalida = AGENDA.getEstadisticaDiariaDeSalida();

            String texto
                    = "------------ REPORTE DIA " + momento + " ------------"
                    + "\nSOLICITUDES ENTRANTES\n" + Estadistica.comparar(estadisticaDiariaEntrada, estadisticaDiariaSalida)
                    + "\n\nSOLICITUDES AGENDADAS\n" + estadisticaDiariaSalida.toString()
                    + "\n\nDATOS GENERALES"
                    + "\n  -Solicitudes en cola de espera:\t" + MLQ.getSolicitudesEnEspera()
                    + "\n  -Vacunas disponibles:\t" + MLQ.getVacunasDisponibles()
                    + "\n  -Vacunas ingresadas:\t" + estadisticaDiariaEntrada.getCantidadVacunas()
                    + "\n  -Estado del planificador:\t" + MLQ.getEstado()
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
            ManejadorArchivos.escribirArchivo(getPathReporteDiario(momento), texto, false);
        } catch (ArithmeticException e) {
            System.out.println(e);
        }
    }

    private String getPathReporteDiario(int dia) {
        return "src/Archivos/reporteDia_" + (dia < 10 ? "0" : "") + dia + ".txt";
    }

    @Override
    public void run() {
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
                // Creo el reporte diario
                System.out.println("Fin del dia\nComienza escritura de reporte");
                this.generarArchivoReporteDiario(momento, agendados);
                System.out.println("Termino reporte dia " + momento + " solicitudes\n");
                // Despierto a los despachadores y agendadores
                Despachador.releaseAll();
                Agendador.releaseAll();
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
        // Creo el reporte total
        System.out.println("Nada mas que reportar! Se termina el programa");
        this.generarArchivoReporteTotal(momento - 1);
    }
}
