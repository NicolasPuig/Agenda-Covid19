package Program;

import Planificador.MLQ;
import Planificador.Solicitud;
import java.util.LinkedList;

/**
 *
 * @author NicoPuig
 */
public class Main {
    
    public static void main(String[] args) {

        // Boceto de funcionamiento
        // TODO: Emprolijar
        LinkedList<Solicitud> archivo = new LinkedList();
        MLQ mlq = new MLQ();
        Inserter.MLQ = mlq;
        Remover.MLQ = mlq;
        Remover.archivo = archivo;
        Inserter.cantidadDeSolicitudes = 5;
        int cantidadInserters = 10;
        int cantidadRemovers = 10;
        mlq.agregarVacunas(11);
        
        for (int i = 0; i < cantidadInserters; i++) {
            Inserter inserter = new Inserter(String.valueOf(i));
            inserter.start();
        }
        
        System.out.println(archivo); // Brakepoint para chequear carga de solicitudes en MLQ

        for (int i = 0; i < cantidadRemovers; i++) {
            Remover remover = new Remover(String.valueOf(i));
            remover.start();
        }

        /*
        Para chequear funcionamiento poner brakepoint en la proxima linea, y 'archivo' se ira llenando en paralelo
        Sin el brakepoint no se imprimira nada porque cuando se llegue aca todavia no se habra cargado nada
        TODO: Arreglar esto para que se espere a que se terminen de cargar solicitudes antes de imprimir
        Posible solucion: Esperar a que no queden mas vacunas, despues imprimir
         */
        archivo.forEach(N -> System.out.println(N.toString()));
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
