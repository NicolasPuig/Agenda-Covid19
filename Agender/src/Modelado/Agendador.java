package Modelado;

import Planificador.MLQ;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

/**
 *
 * @author PaoloMazza, SebaMazzey, NicoPuig
 */
public class Agendador implements Runnable {

    private final static LinkedList<Solicitud> buffer = new LinkedList<>();
    private final static Semaphore semAgendador = new Semaphore(0);
    private final static Semaphore mutexBuffer = new Semaphore(1);
    private final static MLQ mlq = MLQ.MLQ;

    private static int cantidadAgendadores = 0;

    private final Thread thread;

    public Agendador() {
        this.thread = new Thread(this, "Agendador-" + cantidadAgendadores++);
        this.thread.setDaemon(true);
    }

    public void start() {
        semAgendador.release();
        thread.start();
    }

    public static Solicitud[] getSolicitudesAgendadas() {
        Solicitud[] agendados = buffer.toArray(new Solicitud[buffer.size()]);
        buffer.clear();
        return agendados;
    }

    public static void acquireAll() {
        try {
            semAgendador.acquire(cantidadAgendadores);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void releaseAll() {
        semAgendador.release(cantidadAgendadores);
    }

    private void acquireAgendador() throws InterruptedException {
        mlq.acquireSolicitud();     // Hay solicitudes y vacunas
        semAgendador.acquire();     // Entrar a trabajar
        mutexBuffer.acquire();      // Esperar su turno entre otros agendadores
    }

    private void releaseAgendador() {
        mutexBuffer.release();      // Terminar su turno entre agendadores
        semAgendador.release();     // Salir de trabajar
    }

    @Override
    public void run() {
        while (true) {
            try {
                acquireAgendador();
                buffer.add(mlq.proximaSolicitud());
                releaseAgendador();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
