package Program;

import Planificador.MLQ;
import Planificador.Solicitud;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

/**
 * Hilo encargado de retirar solicitudes del MLQ - //TODO: Cambiar nombre
 *
 * @author NicoPuig
 */
public class Archivador implements Runnable {

    private final static LinkedList<Solicitud> buffer = new LinkedList<>();
    private final static Semaphore mlqMutex = new Semaphore(0);
    private final static Semaphore bufferMutex = new Semaphore(1, true);
    private final static MLQ mlq = MLQ.MLQ;

    private static int cantidadArchivadores = 0;

    private final Thread thread;
    private final String name;

    public Archivador(String name) {
        this.name = "A-" + name;
        this.thread = new Thread(this, this.name);
        this.thread.setDaemon(true);
        cantidadArchivadores++;
    }

    public void start() {
        mlqMutex.release();
        thread.start();
    }
    
    public static Semaphore getMlqMutex() {
        return mlqMutex;
    }
    
    public static Solicitud[] getSolicitudesSalida() {
        return buffer.toArray(new Solicitud[buffer.size()]);
    }
    
    public static void limpiarBufferSalida() {
        buffer.clear();
    }

    public static Reporte getReporteDiario() {
        try {
            // semaforoPepito.acquire(cantidadProductores);
            // espera a que se quede sin vacunas o sin personas el mlq
            mlqMutex.acquire(cantidadArchivadores); // +cantidadProductores
            Solicitud[] solicitudes = buffer.toArray(new Solicitud[buffer.size()]);
            int vacunasDisponibles = mlq.getVacunasDisponibles();
            int personasEnEspera = mlq.getLargoColaEspera();
            buffer.clear();
            //mlqMutex.release(cantidadArchivadores + cantidadProductores);
            // semaforoProductores.release(cantidadProductores)
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
                mlq.acquireSolicitud();
                mlqMutex.acquire();
                Solicitud solicitud = mlq.removeNext();
                solicitud.setHoraFinSolicitud(System.nanoTime());
                bufferMutex.acquire();
                buffer.add(solicitud);
                bufferMutex.release();
                mlqMutex.release();
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
    }
}
