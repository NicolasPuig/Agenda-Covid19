package Modelado;

import Planificador.MLQ;
import java.util.LinkedList;
import java.util.Map;
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
        thread.start();
    }

    public static Map<String, LinkedList<Vacunatorio>> getSolicitudesAgendadas() {
        return Agenda.AGENDA.getVacunatoriosPorDepartamento();
    }

    public static void acquireAll() {
        semAgendador.tryAcquire(cantidadAgendadores);
//            semAgendador.acquire(cantidadAgendadores);
    }

    public static void releaseAll() {
        semAgendador.release(cantidadAgendadores);
    }

    private void acquireAgendador() throws InterruptedException {
        mlq.acquireSolicitud();     // Hay solicitudes y vacunas
        semAgendador.acquire();     // Entrar a trabajar
//        mutexBuffer.acquire();      // Esperar su turno entre otros agendadores
    }

//    private void releaseAgendador() {
////        mutexBuffer.release();      // Terminar su turno entre agendadores
//        semAgendador.release();     // Salir de trabajar
//    }

    @Override
    public void run() {
        semAgendador.release();
        while (true) {
            try {
                acquireAgendador();
                Agenda.AGENDA.agendar(mlq.proximaSolicitud());
                semAgendador.release();
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
        }
    }
}
