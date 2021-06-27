package Program;

import Hilos.Reportador;
import Hilos.Agendador;
import Hilos.Debugger;
import Hilos.Despachador;
import Hilos.DespachadorVacunas;
import Hilos.Estadistico;
import Modelado.Agenda;
import Util.ManejadorArchivos;

/**
 *
 * @author PaoloMazza, SebaMazzey, NicoPuig
 */
public class Main {

    private final static String[] ARCHIVOS_ENTRADA_SOLICITUDES = {"entradaWSP.txt", "entradaWeb.txt", "entradaApp.txt", "entradaSMS.txt"};
    private final static String PATH_VACUNATORIOS = "vacunatoriosReal.txt";
    private final static String ARCHIVO_VACUNAS = "entradaVacunas.txt";
    private final static String PATH_ARCHIVOS_ENTRADA = "src/Archivos/Entrada/";

    public static void main(String[] args) throws InterruptedException {

        /* -------------------- Parametros Iniciales -------------------------*/
        int cantidadDias = 30;
        int cantidadDeAgendadores = 10;
        int cantidadDeEstadisticos = 5;
        boolean generarArchivoEntrada = true;
        boolean modoDebug = false;
        boolean escribirReportesDiarios = true;
        // ------------------------------------------------------------------ //

        /* --- Creacion de hilos de despachadores y agendadores --- */
        if (generarArchivoEntrada) {
            generarArchivosEntrada(cantidadDias);
        }
        if (modoDebug) {
            new Debugger().start();
        }
        Agenda.AGENDA = new Agenda(PATH_ARCHIVOS_ENTRADA + PATH_VACUNATORIOS);
        ManejadorArchivos.borrarArchivosSalida();

        /* --- Creacion de hilos de despachadores y agendadores --- */
        // 1) Despachador de vacunas
        Thread vacunador = new DespachadorVacunas(PATH_ARCHIVOS_ENTRADA + ARCHIVO_VACUNAS);
        vacunador.setPriority(7);
        vacunador.start();

        // 2) Despachador de solicitudes
        for (String archivoEntrada : ARCHIVOS_ENTRADA_SOLICITUDES) {
            Thread despachador = new Despachador(PATH_ARCHIVOS_ENTRADA + archivoEntrada);
            despachador.setPriority(7);
            despachador.start();
        }

        // 3) Agendadores
        for (int i = 0; i < cantidadDeAgendadores; i++) {
            new Agendador().start();
        }

        // 4) Estadisticos
        for (int i = 0; i < cantidadDeEstadisticos; i++) {
            new Estadistico().start();
        }

        // 5) Reportador 
        Reportador reportador = new Reportador(cantidadDias, ARCHIVOS_ENTRADA_SOLICITUDES.length + 1, escribirReportesDiarios);
        reportador.setPriority(3);
        reportador.start();
        // -------------------------------------------------------------
    }

    private static void generarArchivosEntrada(int cantidadMomentos) {

        // --- Configuracion de Archivos ---
        int minPersonasPorMomento = 5000;
        int maxPersonasPorMomento = 6000;
        float probabilidadRiesgo = 0.1f;

        int minVacunasPorMomento = 10000;
        int maxVacunasPorMomento = 50000;
        // ---------------------------------

        int i = 1;
        for (String archivo : ARCHIVOS_ENTRADA_SOLICITUDES) {
            ManejadorArchivos.generarArchivosEntradaSolicitudes(PATH_ARCHIVOS_ENTRADA + archivo, cantidadMomentos, i++, minPersonasPorMomento, maxPersonasPorMomento, probabilidadRiesgo);
        }
        ManejadorArchivos.generarArchivoEntradaVacunas(PATH_ARCHIVOS_ENTRADA + ARCHIVO_VACUNAS, cantidadMomentos, minVacunasPorMomento, maxVacunasPorMomento);
    }
}
