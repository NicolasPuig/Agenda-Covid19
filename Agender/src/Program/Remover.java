package Program;

import Planificador.*;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

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
    public static FCFSQueue<String> solicitudes = new FCFSQueue<>();
    public final static Semaphore mutex = new Semaphore(0);

    public Remover(String name) {
        this.name = "R-" + name;
        this.thread = new Thread(this, this.name);
    }

    public void start() {
        mutex.release();
        thread.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Solicitud solicitud = MLQ.removeNext();
                mutex.acquire();
                solicitudes.push(solicitud.toString());
                mutex.release();
            } catch(NullPointerException np){
                System.out.println("NP: ");
                np.printStackTrace();
            }catch (Exception ex) {
                System.out.println(ex);
            }
        }
    }
}
