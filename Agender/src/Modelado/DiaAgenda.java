package Modelado;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

/**
 *
 * @author PaoloMazza, SebaMazzey, NicoPuig
 */
public class DiaAgenda {

    private Queue<Solicitud> personasAgendadas;
    private int cantAgendadosTotal = 0;
    private final Semaphore mutexAgenda;
    private final Semaphore mutexContador;
    private final int numeroDia;
    private final Estadistica estadisticaDiaria = new EstadisticaConTiempo();

    public DiaAgenda(int numeroDia) {
        personasAgendadas = new LinkedList();
        this.mutexAgenda = new Semaphore(1, true);
        this.mutexContador = new Semaphore(1, true);
        this.numeroDia = numeroDia;
    }

    public String getEstadisticaDiaria() {
        estadisticaDiaria.esperarFinAnalisis();
        return estadisticaDiaria.toString();
    }

    public Queue<Solicitud> getPersonasAgendadas() {
        return this.personasAgendadas;
    }

    public int getCantAgendados() throws InterruptedException {
        mutexContador.acquire();
        int cantidad = this.cantAgendadosTotal;
        mutexContador.release();
        return cantidad;
    }

    public void aumentarAgendados() throws InterruptedException {
        mutexContador.acquire();
        cantAgendadosTotal++;
        mutexContador.release();
    }

    public void agendar(Solicitud solicitud) throws InterruptedException {
        mutexAgenda.acquire();
        personasAgendadas.add(solicitud);
        mutexAgenda.release();
        solicitud.setMomentoFinSolicitud(numeroDia);
        estadisticaDiaria.pedirAnalisis(solicitud);
        estadisticaDiaria.pedirAnalisis(solicitud);
    }
}
