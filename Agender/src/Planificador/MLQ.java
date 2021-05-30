package Planificador;

import java.util.concurrent.Semaphore;

/**
 *
 * @author NicoPuig
 */
public class MLQ {

    public final static MLQ MLQ = new MLQ();

    private final FCFSQueue<Solicitud> lowRiskQ18_30 = new FCFSQueue<>();
    private final FCFSQueue<Solicitud> lowRiskQ31_50 = new FCFSQueue<>();
    private final FCFSQueue<Solicitud> lowRiskQ51_65 = new FCFSQueue<>();
    private final FCFSQueue<Solicitud> highRiskQ = new FCFSQueue<>();
    private final Semaphore solicitudes = new Semaphore(0);
    private final Semaphore vacunas = new Semaphore(0);

    private MLQ() {
    }

    public int getVacunasDisponibles() {
        return vacunas.availablePermits();
    }

    public int getLargoColaEspera() {
        return solicitudes.availablePermits();
    }

    public void agregarVacunas(int cantidad) {
        vacunas.release(cantidad);
    }

    public void acquireSolicitud() throws InterruptedException {
        solicitudes.acquire();
        vacunas.acquire(2);
    }

    public void insert(Solicitud solicitud) throws InterruptedException {
        int riesgo = solicitud.getRiesgo();
        if (riesgo > 0) {
            highRiskQ.push(solicitud);
        } else {
            int edad = solicitud.getEdad();
            if (edad < 31) {
                lowRiskQ18_30.push(solicitud);
            } else if (edad < 51) {
                lowRiskQ31_50.push(solicitud);
            } else if (edad < 66) {
                lowRiskQ51_65.push(solicitud);
            } else {
                // Se suponse que edad > 65 y riesgo = 0
                highRiskQ.push(solicitud);
            }
        }
        solicitudes.release();
    }

    public Solicitud removeNext() throws InterruptedException, Exception {
        if (!highRiskQ.isEmpty()) {
            Solicitud solicitud = highRiskQ.pop();
            solicitud.setHoraFinSolicitud(System.nanoTime());
            return solicitud;
        }
        if (!lowRiskQ18_30.isEmpty()) {
            Solicitud solicitud = lowRiskQ18_30.pop();
            solicitud.setHoraFinSolicitud(System.nanoTime());
            return solicitud;
        }
        if (!lowRiskQ51_65.isEmpty()) {
            Solicitud solicitud = lowRiskQ51_65.pop();
            solicitud.setHoraFinSolicitud(System.nanoTime());
            return solicitud;
        }
        if (!lowRiskQ31_50.isEmpty()) {
            Solicitud solicitud = lowRiskQ31_50.pop();
            solicitud.setHoraFinSolicitud(System.nanoTime());
            return solicitud;
        }
        return null;
    }
}
