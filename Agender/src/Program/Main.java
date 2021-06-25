package Program;

import Hilos.Reportador;
import Hilos.Agendador;
import Hilos.Debugger;
import Hilos.Despachador;
import Hilos.DespachadorVacunas;
import Hilos.Estadistico;
import Util.ManejadorArchivos;

/**
 *
 * @author PaoloMazza, SebaMazzey, NicoPuig
 */
public class Main {

    private final static String[] ARCHIVOS_ENTRADA_SOLICITUDES = {"entradaWSP.txt", "entradaWeb.txt", "entradaApp.txt", "entradaSMS.txt"};
    private final static String ARCHIVO_VACUNAS = "entradaVacunas.txt";
    private final static String PATH_ARCHIVOS = "src/Archivos/";

    public static void main(String[] args) throws InterruptedException {

        // ---- Parametros Iniciales ----
        int cantidadDias = 30;
        int cantidadDeArchivadores = 10;
        int cantidadDeEstadisticos = 5;
        int cantidadDeProductores = ARCHIVOS_ENTRADA_SOLICITUDES.length;
        // ------------------------------

        generarArchivosEntrada(cantidadDias);

        new Debugger(); // Solo correr para modo Debug, relantiza el programa

        // Eliminar archivos de reportes diarios viejos
        ManejadorArchivos.borrarArchivosSalida();

        // ---- Creacion de hilos de productores y agendadores ----
        new DespachadorVacunas(PATH_ARCHIVOS + ARCHIVO_VACUNAS).start();

        for (String archivoEntrada : ARCHIVOS_ENTRADA_SOLICITUDES) {
            new Despachador(PATH_ARCHIVOS + archivoEntrada).start();
        }

        for (int i = 0; i < cantidadDeArchivadores; i++) {
            new Agendador().start();
        }

        for (int i = 0; i < cantidadDeEstadisticos; i++) {
            new Estadistico().start();
        }

        // --------------------------------------------------------
        // ---- Modelado de lo dias y generador de reportes diarios ----
        Reportador reportador = new Reportador(cantidadDias, cantidadDeProductores + 1);
        reportador.setPriority(4);
        reportador.start();
        // -------------------------------------------------------------
    }

    private static void generarArchivosEntrada(int cantidadMomentos) {

        // --- Configuracion ---
        int minPersonasPorMomento = 50000;
        int maxPersonasPorMomento = 100000;
        float probabilidadRiesgo = 0.1f;

        int minVacunasPorMomento = 50000;
        int maxVacunasPorMomento = 500000;
        // ---------------------

//        int i = 1;
//        for (String archivo : ARCHIVOS_ENTRADA_SOLICITUDES) {
//            ManejadorArchivos.generarArchivosEntradaSolicitudes(PATH_ARCHIVOS + archivo, cantidadMomentos, i++, minPersonasPorMomento, maxPersonasPorMomento, probabilidadRiesgo);
//        }
        ManejadorArchivos.generarArchivoEntradaVacunas(PATH_ARCHIVOS + ARCHIVO_VACUNAS, cantidadMomentos, minVacunasPorMomento, maxVacunasPorMomento);
    }
}
