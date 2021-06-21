package Planificador;

import Modelado.Agendador;
import Modelado.Solicitud;
import java.util.concurrent.Semaphore;

/**
 *
 * @author PaoloMazza, SebaMazzey, NicoPuig
 */
public class MLQ {

    public final static MLQ MLQ = new MLQ();

    private final FCFSQueue<Solicitud> lowRiskQ18_30 = new FCFSQueue<>();
    private final FCFSQueue<Solicitud> lowRiskQ31_50 = new FCFSQueue<>();
    private final FCFSQueue<Solicitud> lowRiskQ51_65 = new FCFSQueue<>();
    private final FCFSQueue<Solicitud> highRiskQ = new FCFSQueue<>();
    private final FCFSQueue<Solicitud>[] queues = new FCFSQueue[]{highRiskQ, lowRiskQ18_30, lowRiskQ51_65, lowRiskQ31_50};

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
        if (!solicitudes.tryAcquire()) {
            Agendador.release();
            solicitudes.acquire();
            Agendador.acquire();
        }
        if (!vacunas.tryAcquire(2)) {
            Agendador.release();
            vacunas.acquire(2);
            Agendador.acquire();
        }
    }

    public boolean canAcquireSolicitud() {
        return solicitudes.availablePermits() > 0 && vacunas.availablePermits() > 0;
    }

    public void insertar(Solicitud solicitud) throws InterruptedException {
        int riesgo = solicitud.getRiesgo();
        if (riesgo > 0) {
            highRiskQ.push(solicitud);
        } else {
            int edad = solicitud.getEdad();
            if (edad < 31) {
                lowRiskQ18_30.push(solicitud);
            } else if (edad < 51) {
                lowRiskQ31_50.push(solicitud);
            } else {
                lowRiskQ51_65.push(solicitud);
            }
        }
        solicitudes.release();
    }

    public Solicitud proximaSolicitud() throws InterruptedException {
        for (FCFSQueue<Solicitud> queue : queues) {
            Solicitud solicitud = queue.pop();
            if (solicitud != null) {
                return solicitud;
            }
//            if (!queue.isEmpty()) {
//                return queue.pop();
//            }
        }
        return null;
    }
}
