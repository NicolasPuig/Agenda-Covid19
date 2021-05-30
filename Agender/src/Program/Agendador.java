package Program;

import Planificador.MLQ;
import Planificador.Solicitud;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

/**
 *
 * @author NicoPuig
 */
public class Agendador implements Runnable {

    private final static LinkedList<Solicitud> buffer = new LinkedList<>();
    private final static Semaphore semAgendador = new Semaphore(0);
    private final static Semaphore mutexBuffer = new Semaphore(1);
    private final static MLQ mlq = MLQ.MLQ;

    private static int cantidadAgendadores = 0;

    private final Thread thread;
    private final String nombre;

    public Agendador() {
        this.nombre = "Agendador-" + cantidadAgendadores++;
        this.thread = new Thread(this, this.nombre);
        this.thread.setDaemon(true);
    }

    public void start() {
        semAgendador.release();
        thread.start();
    }

    public static Solicitud[] getSolicitudesSalida() {
        return buffer.toArray(new Solicitud[buffer.size()]);
    }

    public static void limpiarBufferSalida() {
        buffer.clear();
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
                buffer.add(mlq.removeNext());
                releaseAgendador();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
