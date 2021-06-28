package Hilos;

import static Planificador.MLQ.MLQ;
import static Modelado.Agenda.AGENDA;
import Modelado.DiaAgenda;
import Modelado.Estadistica;
import Modelado.EstadisticaConTiempo;
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

    private final static String PATH_ARCHIVOS = "src/Archivos/";
    private final static String PATH_CSV_CANTIDAD_POR_MOMENTO = PATH_ARCHIVOS + "SalidaCSV/CantidadPorMomento.csv";
    private final static String PATH_CSV_CANTIDAD_TOTAL = PATH_ARCHIVOS + "SalidaCSV/CantidadTotal.csv";
    private final static String PATH_CSV_PORCENTAJE_POR_MOMENTO = PATH_ARCHIVOS + "SalidaCSV/PorcentajePorMomento.csv";
    private final static String PATH_CSV_PORCENTAJE_TOTAL = PATH_ARCHIVOS + "SalidaCSV/PorcentajeTotal.csv";
    private final static String PATH_CSV_TIEMPO_ESPERA_PROMEDIO = PATH_ARCHIVOS + "SalidaCSV/TiempoEsperaPromedio.csv";

    private final static Semaphore semReportes = new Semaphore(0);

    private final int cantidadDespachadores;
    private final int cantidadMomentos;
    private final boolean escribirReportesDiarios;

    public Reportador(int cantidadMomentos, int cantidadDespachadores, boolean escribirReportesDiarios) {
        super("Reportador");
        this.cantidadDespachadores = cantidadDespachadores;
        this.cantidadMomentos = cantidadMomentos;
        this.escribirReportesDiarios = escribirReportesDiarios;
    }

    public static void release() {
        semReportes.release();
    }

    public void generarReporteTotal(int momentosTranscurridos) {
        Estadistica entradaTotal = MLQ.getEstadisticaTotalEntrada();
        EstadisticaConTiempo salidaTotal = AGENDA.getEstadisticaTotalDeSalida();

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
        ManejadorArchivos.escribirArchivo(PATH_ARCHIVOS + "SalidaDiarios/reporteTotal.txt", texto, false);
        ManejadorArchivos.escribirArchivo(PATH_CSV_CANTIDAD_TOTAL, Estadistica.csvCantidadTotal(entradaTotal, salidaTotal), true);
        ManejadorArchivos.escribirArchivo(PATH_CSV_PORCENTAJE_TOTAL, Estadistica.csvPorcentajeTotal(entradaTotal, salidaTotal), true);
        ManejadorArchivos.escribirArchivo(PATH_CSV_TIEMPO_ESPERA_PROMEDIO, salidaTotal.csvTiempoEsperaPromedio(), true);
    }

    private void generarReporteDiario(int momento, Map<String, LinkedList<Vacunatorio>> agendados) {
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
                    DiaAgenda dia = vacunatorio.removerDiaActual(momento, true);
                    texto += "\n    " + numDepartamento + "." + numVacunatorio + ") " + vacunatorio.getNombre()
                            + "\n" + dia.getEstadisticaDiaria() + "\n";
                    numVacunatorio++;
                }
                numDepartamento++;
            }
            if (escribirReportesDiarios) {
                ManejadorArchivos.escribirArchivo(PATH_CSV_CANTIDAD_POR_MOMENTO, estadisticaDiariaSalida.csvCantidadPorMomento(momento) + "\n", true);
                ManejadorArchivos.escribirArchivo(PATH_CSV_PORCENTAJE_POR_MOMENTO, Estadistica.csvPorcentajePorMomento(estadisticaDiariaEntrada, estadisticaDiariaSalida, momento) + "\n", true);
                ManejadorArchivos.escribirArchivo(getPathReporteDiario(momento), texto, false);
            }
        } catch (ArithmeticException e) {
            System.out.println(e);
        }
    }

    private void generarArchivoReporteRestantes(int momento) {
        try {
            if (escribirReportesDiarios) {
                Map<String, LinkedList<Vacunatorio>> agendadosPorDepartamento = Agendador.getSolicitudesAgendadas();
                int ultimoDia = 1;
                for (Map.Entry<String, LinkedList<Vacunatorio>> departamento : agendadosPorDepartamento.entrySet()) {
                    for (Vacunatorio vacunatorio : departamento.getValue()) {
                        if (ultimoDia < vacunatorio.getUltimoDiaAgendado()) {
                            ultimoDia = vacunatorio.getUltimoDiaAgendado();
                        }
                    }
                }
                for (int i = momento; i <= ultimoDia; i++) {
                    String texto
                            = "------------ REPORTE DIA " + i + " ------------"
                            + "\n\nAGENDADO POR DEPARTAMENTO";
                    int numDepartamento = 1;
                    for (Map.Entry<String, LinkedList<Vacunatorio>> departamento : agendadosPorDepartamento.entrySet()) {
                        int numVacunatorio = 1;
                        texto += "\n  " + numDepartamento + ") " + departamento.getKey();
                        for (Vacunatorio vacunatorio : departamento.getValue()) {
                            DiaAgenda dia = vacunatorio.removerDiaActual(i, false);
                            texto += "\n    " + numDepartamento + "." + numVacunatorio + ") " + vacunatorio.getNombre()
                                    + "\n" + (dia != null ? dia.getEstadisticaDiaria() : "Ningun Agendado") + "\n";
                            numVacunatorio++;
                        }
                        numDepartamento++;
                    }
                    ManejadorArchivos.escribirArchivo(getPathReporteDiarioRestante(i), texto, false);
                }
            }
        } catch (ArithmeticException e) {
            System.out.println(e);
        }
    }

    private String getPathReporteDiario(int dia) {
        return PATH_ARCHIVOS + "SalidaDiarios/reporteDia_" + (dia < 10 ? "0" : "") + dia + ".txt";
    }

    private String getPathReporteDiarioRestante(int dia) {
        return PATH_ARCHIVOS + "SalidaDiasRestantes/reporteDia_" + (dia < 10 ? "0" : "") + dia + ".txt";
    }

    private void agregarHeadersCSV() {
        String headerDiario = "Momento;Alto riesgo;Bajo riesgo (18-30);Bajo riesgo (31-50);Bajo riesgo (51-65)\n";
        String headerTotal = "Total de agendados;Alto riesgo;Bajo riesgo (18-30);Bajo riesgo (31-50);Bajo riesgo (51-65)\n";
        ManejadorArchivos.escribirArchivo(PATH_CSV_CANTIDAD_POR_MOMENTO, headerDiario, false);
        ManejadorArchivos.escribirArchivo(PATH_CSV_CANTIDAD_TOTAL, ";" + headerTotal, false);
        ManejadorArchivos.escribirArchivo(PATH_CSV_PORCENTAJE_POR_MOMENTO, headerDiario, false);
        ManejadorArchivos.escribirArchivo(PATH_CSV_PORCENTAJE_TOTAL, headerTotal, false);
        ManejadorArchivos.escribirArchivo(PATH_CSV_TIEMPO_ESPERA_PROMEDIO, headerTotal, false);
    }

    @Override
    public void run() {
        int momento = 1;
        agregarHeadersCSV();
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
                this.generarReporteDiario(momento, agendados);
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
        this.generarArchivoReporteRestantes(momento);
        this.generarReporteTotal(momento - 1);
    }
}
