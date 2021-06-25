package Planificador;

import Hilos.Agendador;
import Modelado.Estadistica;
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

    private final Estadistica estadisticaTotalEntrada = new Estadistica();
    private Estadistica estadisticaDiariaEntrada = new Estadistica();

    private MLQ() {
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
        estadisticaDiariaEntrada.pedirAnalisis(solicitud);
        estadisticaTotalEntrada.pedirAnalisis(solicitud);
    }

    public Solicitud proximaSolicitud() throws InterruptedException {
        for (FCFSQueue<Solicitud> queue : queues) {
            Solicitud solicitud = queue.pop();
            if (solicitud != null) {
                return solicitud;
            }
        }
        return null;
    }

    public int getVacunasDisponibles() {
        return vacunas.availablePermits();
    }

    public int getSolicitudesEnEspera() {
        return solicitudes.availablePermits();
    }

    public void agregarVacunas(int cantidad) throws InterruptedException {
        vacunas.release(cantidad);
        estadisticaDiariaEntrada.agregarVacunas(cantidad);
        estadisticaTotalEntrada.agregarVacunas(cantidad);
    }

    public Estadistica getEstadisticaDiariaEntrada() {
        this.estadisticaDiariaEntrada.esperarFinAnalisis();
        Estadistica estadistica = this.estadisticaDiariaEntrada;
        this.estadisticaDiariaEntrada = new Estadistica();
        return estadistica;
    }

    public Estadistica getEstadisticaTotalEntrada() {
        estadisticaTotalEntrada.esperarFinAnalisis();
        return estadisticaTotalEntrada;
    }

    public String getEstado() {
        if (vacunas.availablePermits() < 2) {
            return "SIN VACUNAS";
        }
        if (solicitudes.availablePermits() == 0) {
            return "NINGUNA SOLICITUD PARA AGENDAR";
        }
        return "AGENDANDO";
    }
}
