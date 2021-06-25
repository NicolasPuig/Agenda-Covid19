package Hilos;

import Modelado.Estadistica;
import Modelado.Solicitud;
import Util.Par;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

/**
 *
 * @author NicoPuig
 */
public class Estadistico implements Runnable {

    private static int cantidadEstadisticos = 0;
    private final Thread thread;
    private static final Semaphore full = new Semaphore(0);
    private static final Semaphore mutex = new Semaphore(1);
    private static final Queue<Par<Estadistica, Solicitud>> buffer = new LinkedList<>();

    public Estadistico() {
        this.thread = new Thread(this, "Estadistico-" + cantidadEstadisticos++);
        this.thread.setPriority(Thread.NORM_PRIORITY - 1);
        this.thread.setDaemon(true);
    }

    public void start() {
        this.thread.start();
    }

    public static void analizar(Estadistica estadistica, Solicitud solicitud) throws InterruptedException {
        Par<Estadistica, Solicitud> par = new Par(estadistica, solicitud);
        mutex.acquire();
        buffer.add(par);
        mutex.release();
        full.release();
    }

    public static void avisarCuandoTermine(Estadistica estaditica) throws InterruptedException {
        analizar(estaditica, null);
    }

    @Override
    public void run() {
        while (true) {
            try {
                full.acquire();
                mutex.acquire();
                Par<Estadistica, Solicitud> par = buffer.remove();
                mutex.release();
                Estadistica estadistica = par.getPrimero();
                Solicitud solicitud = par.getSegundo();
                if (solicitud == null) {
                    estadistica.terminarAnalisis();
                } else {
                    estadistica.analizarSolicitud(solicitud);
                }
            } catch (InterruptedException | RuntimeException ie) {
                System.out.println(ie);
            }
        }
    }
}
