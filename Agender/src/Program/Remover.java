package Program;

import Planificador.*;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase prototipo de Archivador Hilo que retira solicitudes al MLQ La idea es
 * tener varios de estos hilos retirando La sincronizacion ya esta resuelta en
 * el MLQ TODO: Agregar semaforo para control de cantidad de vacunas
 *
 * @author NicoPuig
 */
public class Remover implements Runnable {

    private final Thread thread;
    private final String name;
    public static MLQ MLQ;
    public static LinkedList<Solicitud> archivo;
    private final static Semaphore mutex = new Semaphore(1, true);

    public Remover(String name) {
        this.name = "R-" + name;
        this.thread = new Thread(this, this.name);
    }

    public void start() {
        thread.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Solicitud solicitud = MLQ.removeNext();
                mutex.acquire();
                archivo.add(solicitud);
                mutex.release();
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
    }
}
