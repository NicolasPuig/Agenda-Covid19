package Program;

import Planificador.MLQ;
import Planificador.Solicitud;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

/**
 * Hilo encargado de retirar solicitudes del MLQ
 * @author NicoPuig
 */
public class Archivador implements Runnable {

    private final static LinkedList<Solicitud> buffer = new LinkedList<>();
    private final static Semaphore mutex = new Semaphore(1, true);
    private final static MLQ mlq = MLQ.MLQ;
    
    private final Thread thread;
    private final String name;

    public Archivador(String name) {
        this.name = "A-" + name;
        this.thread = new Thread(this, this.name);
        this.thread.setDaemon(true);
    }

    public void start() {
        thread.start();
    }

    public static Reporte getReporteDiario() {
        try {
            mutex.acquire();
            Solicitud[] solicitudes = buffer.toArray(new Solicitud[buffer.size()]);
            int vacunasDisponibles = mlq.getVacunasDisponibles();
            int personasEnEspera = mlq.getLargoColaEspera();
            buffer.clear();
            mutex.release();
            return new Reporte(solicitudes, vacunasDisponibles, personasEnEspera);
        } catch (InterruptedException ex) {
            System.out.println(ex);
        }
        return null;
    }

    @Override
    public void run() {
        while (true) {
            try {
                mlq.acquire();
                mutex.acquire();
                Solicitud solicitud = mlq.removeNext();
                buffer.add(solicitud);
                mutex.release();
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
    }
}
