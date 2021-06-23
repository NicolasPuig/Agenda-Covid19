package Hilos;

import static Planificador.MLQ.MLQ;
import Util.ManejadorArchivos;
import Modelado.Solicitud;
import java.util.Collection;
import java.util.concurrent.Semaphore;

/**
 *
 * @author PaoloMazza, SebaMazzey, NicoPuig
 */
public class Despachador extends Thread {

    private final String archivoEntrada;
    private final static Semaphore semDespachador = new Semaphore(0, true);

    private static int cantidadDespachadores = 0;

    public Despachador(String archivoEntrada) {
        super("Despachador-" + cantidadDespachadores++);
        this.archivoEntrada = archivoEntrada;
    }

    public static void releaseAll() {
        semDespachador.release(cantidadDespachadores + 1);
    }

    public static void acquire() throws InterruptedException {
        semDespachador.acquire();
    }

    @Override
    public void run() {
        // Leo el archivo de entrada
        Collection<String> personas = ManejadorArchivos.leerArchivo(archivoEntrada, true);
        int momentoActual = 1;
        for (String persona : personas) {
            // personas = momento;CI;edad;Riesgo
            String[] datos = persona.split(";");
            // Mientras el momento no cambia
            if (Integer.parseInt(datos[0]) == momentoActual) {
                // Genero y agrego la solicitud al mlq
                String ci = datos[1];
                int edad = Integer.parseInt(datos[2]);
                int riesgo = Integer.parseInt(datos[3]);
                String departamento = datos[4];
                try {
                    MLQ.insertar(new Solicitud(ci, edad, riesgo, momentoActual, departamento));
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
            } else {
                // Aviso que termine de procesar las solicitudes del dia
                Reportador.release();
                // Espero a que se emita el reporte y me avisen
                semDespachador.acquireUninterruptibly();
                momentoActual++;
                // Proceso a la persona que me quedo pendiente
                String ci = datos[1];
                int edad = Integer.parseInt(datos[2]);
                int riesgo = Integer.parseInt(datos[3]);
                String departamento = datos[4];
                try {
                    MLQ.insertar(new Solicitud(ci, edad, riesgo, momentoActual, departamento));
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
            }
        }
        // Si llego aca deje de producir
        Reportador.release();
    }
}