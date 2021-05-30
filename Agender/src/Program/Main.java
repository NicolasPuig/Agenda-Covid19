package Program;

import Planificador.MLQ;
import Util.ManejadorArchivos;

/**
 *
 * @author NicoPuig
 */
public class Main {

    public static void main(String[] args) {
        ManejadorArchivos.borrarArchivos();

        String[] archivosEntrada = {"entradaWSP.txt", "entradaWeb.txt", "entradaApp.txt", "entradaSMS.txt"};
        String archivoVacunas = "vacunas.txt";

        // ---- Parametros Iniciales ----
        int cantidadDeProductores = archivosEntrada.length;
        int cantidadDeArchivadores = 10;
        boolean reportarListaAgendados = true;
        // ------------------------------

        ProductorVacunas productorVacunas = new ProductorVacunas("src/Archivos/" + archivoVacunas);
        productorVacunas.start();

        for (String archivoEntrada : archivosEntrada) {
            new Productor("src/Archivos/" + archivoEntrada).start();
        }

        System.out.println(""); // BrakePoint para chequear carga de solicitudes al MLQ

        for (int i = 0; i < cantidadDeArchivadores; i++) {
            new Agendador().start();
        }

        Reporte reporte = new Reporte(5, cantidadDeProductores + 1, cantidadDeArchivadores, reportarListaAgendados);
        reporte.start();

        System.out.println(""); // BrakePoint para chequear agendado
    }
}

/*
::::::: COMPORTAMIENTO ESPERADO :::::::
TODO LIST:
    - Funcion que cree los archivos de salida
    - Buscar la forma de identificar cuando no queden mas solicitudes o vacunas, y ahi crear el archivo de salida
    - Ver que hacer cuando los Removers esten todos bloqueados por falta de solicitudes o vacunas. Matarlos o dejarlos esperando a mas recursos?
 */
