package Hilos;

import static Planificador.MLQ.MLQ;
import Modelado.Solicitud;
import Util.Par;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 *
 * @author PaoloMazza, SebaMazzey, NicoPuig
 */
public class Despachador extends Thread {

    private final static Semaphore semDespachador = new Semaphore(0, true);
    private final Par<List<String>, Semaphore> listaLineasEntrada;
    private static int cantidadDespachadores = 0;

    public Despachador(Par<List<String>, Semaphore> listaLineasEntrada) {
        super("Despachador-" + cantidadDespachadores++);
        this.listaLineasEntrada = listaLineasEntrada;
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
        List<String> solicitudesEntrantes = listaLineasEntrada.getPrimero();
        Semaphore mutexLista = listaLineasEntrada.getSegundo();
        int momentoActual = 1;
        while (true) {
            try {
                mutexLista.acquire();
                String informacionSolicitud = solicitudesEntrantes.remove(0);
                mutexLista.release();
                String[] datos = informacionSolicitud.split(";");
                int momento = Integer.parseInt(datos[0]);
                Solicitud solicitud = new Solicitud(datos[1], Integer.parseInt(datos[2]), Integer.parseInt(datos[3]), momento, datos[4]);
                if (momento != momentoActual) {
                    Reportador.release();
                    semDespachador.acquire();
                    momentoActual = momento;
                }
                MLQ.insertar(solicitud);
            } catch (IndexOutOfBoundsException ib) {
                // No mas solicitudes, termina de despachar
                mutexLista.release();
                Reportador.release();
                return;
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
        }
    }
}
