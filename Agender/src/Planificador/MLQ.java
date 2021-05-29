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

    private final Semaphore solicitudes = new Semaphore(0, true);
    private final Semaphore vacunas = new Semaphore(0, true);

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

    public void acquire() throws InterruptedException {
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
                // No se deberia llegar nunca aca
                // TODO: Emprolijar
                throw new IllegalArgumentException("Error +66 con riesgo 0: " + edad);
            }
        }
        solicitudes.release();
    }

    public Solicitud removeNext() throws InterruptedException, Exception {
        if (!highRiskQ.isEmpty()) {
            return highRiskQ.pop();
        }
        if (!lowRiskQ18_30.isEmpty()) {
            return lowRiskQ18_30.pop();
        }
        if (!lowRiskQ31_50.isEmpty()) {
            return lowRiskQ31_50.pop();
        }
        if (!lowRiskQ51_65.isEmpty()) {
            return lowRiskQ51_65.pop();
        }
        throw new IllegalArgumentException("ERROR: No se deberia llegar nunca aca"); // TODO: Emprolijar
    }
}
