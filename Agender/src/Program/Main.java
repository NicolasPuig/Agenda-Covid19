package Program;

import Modelado.Reporte;
import Modelado.Agendador;
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

    public static void main(String[] args) {

        // ---- Parametros Iniciales ----
        int cantidadDias = 5;
        int cantidadDeArchivadores = 10;
        int cantidadDeProductores = ARCHIVOS_ENTRADA_SOLICITUDES.length;
        boolean reportarListaAgendados = true;
        // ------------------------------

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
        Reporte reporte = new Reporte(cantidadDias, cantidadDeProductores + 1, cantidadDeArchivadores, reportarListaAgendados);
        reporte.start();
        // -------------------------------------------------------------
    }
}

/*
::::::: COMPORTAMIENTO ESPERADO :::::::
 */
