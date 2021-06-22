package Program;

import Modelado.Agenda;
import Modelado.Reportador;
import Modelado.Agendador;
import Modelado.Debugger;
import Modelado.Despachador;
import Modelado.DespachadorVacunas;
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
        int cantidadDias = 5;
        int cantidadDeArchivadores = 10;
        int cantidadDeProductores = ARCHIVOS_ENTRADA_SOLICITUDES.length;
        boolean reportarListaAgendados = true;
        // ------------------------------
        new Debugger();

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
        // --------------------------------------------------------

        // ---- Modelado de lo dias y generador de reportes diarios ----
        Reportador reportador = new Reportador(cantidadDias, cantidadDeProductores + 1, reportarListaAgendados);
        reportador.setPriority(4);
        reportador.start();
        // -------------------------------------------------------------
    }

    private static void generaArchivosEntrada() {
        ManejadorArchivos.generarArchivoEntradaConMomentos(PATH_ARCHIVOS + "entradaAPP.txt", 5, 1);
        ManejadorArchivos.generarArchivoEntradaConMomentos(PATH_ARCHIVOS + "entradaWSP.txt", 5, 2);
        ManejadorArchivos.generarArchivoEntradaConMomentos(PATH_ARCHIVOS + "entradaWEB.txt", 5, 3);
        ManejadorArchivos.generarArchivoEntradaConMomentos(PATH_ARCHIVOS + "entradaSMS.txt", 5, 4);
    }
}
