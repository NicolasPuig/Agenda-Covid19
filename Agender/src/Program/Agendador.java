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
        this.nombre = "A-" + cantidadAgendadores++;
        this.thread = new Thread(this, this.nombre);
        this.thread.setDaemon(true);
    }

    public void start() {
        semAgendador.release();
        thread.start();
    }

    public static Reporte getReporteDiario() {
        try {
            semAgendador.acquire(cantidadAgendadores); // +cantidadProductores
            Solicitud[] solicitudes = buffer.toArray(new Solicitud[buffer.size()]);
            int vacunasDisponibles = mlq.getVacunasDisponibles();
            int personasEnEspera = mlq.getLargoColaEspera();
            buffer.clear();
            semAgendador.release(cantidadAgendadores); // +cantidaProductores
            return new Reporte(solicitudes, vacunasDisponibles, personasEnEspera);
        } catch (InterruptedException ex) {
            System.out.println(ex);
        }
        return null;
    }

    private void acquireArchivador() throws InterruptedException {
        mlq.acquireSolicitud();     // Hay solicitudes y vacunas
        semAgendador.acquire();  // Entrar a trabajar
        mutexBuffer.acquire();      // Esperar su turno entre otros archivadores
    }

    private void releaseArchivador() {
        mutexBuffer.release();      // Terminar su turno entre archivadores
        semAgendador.release();  // Salir de trabajar
    }

    @Override
    public void run() {
        while (true) {
            try {
                acquireArchivador();
                buffer.add(mlq.removeNext());
                releaseArchivador();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
