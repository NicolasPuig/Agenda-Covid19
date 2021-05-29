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

        // ---- Parametros Iniciales ----
        Inserter.cantidadDeSolicitudes = 1000;
        int cantidadDeInserters = 10;
        int cantidadDeArchivadores = 10;
        int cantidadDeVacunas = 1123145;
        long duracionDia_ms = 500;
        boolean reportarListaAgendados = true;
        // ------------------------------

        MLQ.MLQ.agregarVacunas(cantidadDeVacunas);
        Ciclo ciclo = new Ciclo(duracionDia_ms, reportarListaAgendados);

        for (int i = 0; i < cantidadDeInserters; i++) {
            new Inserter(String.valueOf(i)).start();
        }

        System.out.println(""); // BrakePoint para chequear carga de solicitudes al MLQ

        for (int i = 0; i < cantidadDeArchivadores; i++) {
            new Archivador(String.valueOf(i)).start();
        }

        System.out.println(""); // BrakePoint para chequear agendado
        ciclo.start();
    }
}

/*
::::::: COMPORTAMIENTO ESPERADO :::::::
Si se corre el programa, se colgara y no imprimira nada, porque los hilos Remover quedan esperando a que hayan solicitudes
Se debe debuggear el programa con un brakepoint en la ultima linea para ver las personas agendadas
'archivo' deberia estar ordenado, donde los primeros son pacientes con rieso > 0
Luego vienen los de riesgo = 0, empezando por los de la franja de 18 a 30
Luego la franja de 31 a 50
Por ultimo los de la franja de 51 a 65

Actualmente esta funcionando bien el MLQ, falta arreglar bocetos y prototipos de hilos

TODO LIST:
    - Agendador y Archivador
    - Funcion que interprete los archivos de entrada
    - Funcion que cree los archivos de salida
    - Buscar la forma de identificar cuando no queden mas solicitudes o vacunas, y ahi crear el archivo de salida
    - Ver que hacer cuando los Removers esten todos bloqueados por falta de solicitudes o vacunas. Matarlos o dejarlos esperando a mas recursos?
 */
