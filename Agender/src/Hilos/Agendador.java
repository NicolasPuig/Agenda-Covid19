package Hilos;

import static Planificador.MLQ.MLQ;
import static Modelado.Agenda.AGENDA;
import Modelado.Vacunatorio;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 *
 * @author PaoloMazza, SebaMazzey, NicoPuig
 */
public class Agendador implements Runnable {

    private final static Semaphore semAgendador = new Semaphore(0);

    private static int cantidadAgendadores = 0;

    private final Thread thread;

    public Agendador() {
        this.thread = new Thread(this, "Agendador-" + cantidadAgendadores++);
        this.thread.setPriority(7);
        this.thread.setDaemon(true);
    }

    public void start() {
        thread.start();
    }

    public static Map<String, LinkedList<Vacunatorio>> getSolicitudesAgendadas() {
        return AGENDA.getVacunatoriosPorDepartamento();
    }

    public static void acquire() throws InterruptedException {
        semAgendador.acquire();
    }

    public static void release() {
        semAgendador.release();
    }

    public static void acquireAll() throws InterruptedException {
        semAgendador.acquire(cantidadAgendadores);
    }

    public static void releaseAll() {
        semAgendador.release(cantidadAgendadores);
    }

    @Override
    public void run() {
        while (true) {
            try {
                MLQ.acquireSolicitud();
                AGENDA.agendar(MLQ.proximaSolicitud());
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
        }
    }
}
