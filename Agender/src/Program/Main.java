package Program;

import Hilos.Reportador;
import Hilos.Agendador;
import Hilos.Debugger;
import Hilos.Despachador;
import Hilos.DespachadorVacunas;
import Hilos.Estadistico;
import Modelado.Agenda;
import Util.ManejadorArchivos;
import Util.Par;
import java.util.List;
import java.util.concurrent.Semaphore;

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

        // #####################################################################
        // #   PARAMETROS INICIALES
        // #####################################################################
        int cantidadDias = 30;
        int cantidadDeAgendadores = 10;
        int cantidadDeDespachadoresPorArchivo = 3;
        int cantidadDeEstadisticos = 5;
        boolean generarArchivoEntradaSolicitudes = false;
        boolean generarArchivoEntradaVacunas = false;
        boolean escribirReportesDiarios = true;
        boolean modoDebug = false;

        // #####################################################################
        // #   CONFIGURACION DE SIMULACION
        // #####################################################################
        if (generarArchivoEntradaSolicitudes) {
            generarArchivosEntradaSolicitudes(cantidadDias);
        }
        if (generarArchivoEntradaVacunas) {
            generarArchivoEntradaVacunas(cantidadDias);
        }
        if (modoDebug) {
            new Debugger().start();
        }
        Agenda.AGENDA = new Agenda(PATH_ARCHIVOS_ENTRADA + PATH_VACUNATORIOS);
        ManejadorArchivos.borrarArchivosSalida();

        // #####################################################################
        // #   CREACION DE HILOS
        // #####################################################################
        // 1) Despachador de vacunas
        Thread vacunador = new DespachadorVacunas(PATH_ARCHIVOS_ENTRADA + ARCHIVO_VACUNAS);
        vacunador.setPriority(7);
        vacunador.start();

        // 2) Despachador de solicitudes
        for (String archivoEntrada : ARCHIVOS_ENTRADA_SOLICITUDES) {
            Par<List<String>, Semaphore> listaSolicitudes = new Par(ManejadorArchivos.leerArchivo(PATH_ARCHIVOS_ENTRADA + archivoEntrada, true), new Semaphore(1, true));
            for (int i = 0; i < cantidadDeDespachadoresPorArchivo; i++) {
                Thread despachador = new Despachador(listaSolicitudes);
                despachador.setPriority(7);
                despachador.start();
            }
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
        Reportador reportador = new Reportador(cantidadDias, ARCHIVOS_ENTRADA_SOLICITUDES.length * cantidadDeDespachadoresPorArchivo + 1, escribirReportesDiarios);
        reportador.setPriority(3);
        reportador.start();
    }

    private static void generarArchivosEntradaSolicitudes(int cantidadMomentos) {

        // #####################################################################
        // #   PARAMETROS DE CREACION DE ARCHIVOS DE ENTRADA
        // #####################################################################
        int minPersonasPorMomento = 20000;
        int maxPersonasPorMomento = 30000;
        float probabilidadRiesgo = 0.3f;

        int i = 1;
        for (String archivo : ARCHIVOS_ENTRADA_SOLICITUDES) {
            ManejadorArchivos.generarArchivosEntradaSolicitudes(PATH_ARCHIVOS_ENTRADA + archivo, cantidadMomentos, i++, minPersonasPorMomento, maxPersonasPorMomento, probabilidadRiesgo);
        }
    }

    private static void generarArchivoEntradaVacunas(int cantidadMomentos) {
        int minVacunasPorMomento = 0;
        int maxVacunasPorMomento = 0;
        ManejadorArchivos.generarArchivoEntradaVacunas(PATH_ARCHIVOS_ENTRADA + ARCHIVO_VACUNAS, cantidadMomentos, minVacunasPorMomento, maxVacunasPorMomento);
    }
}
