package Modelado;

import Planificador.SyncHashMap;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Seba Mazzey
 */
public class Vacunatorio {

    private String nombre;
    // Una lista de dias con un array de solicitudes
    // La posicion en el array determina a que turno pertenece
    private SyncHashMap<Integer, DiaAgenda> dias;
    private int capacidadTurno;
    private final int capacidadDia;
    private int capacidadDiaActual;
    private Semaphore lugaresDisponibles;
    private Semaphore lugaresOcupados;
    private int diaActual;

    public Vacunatorio(String nombre, int capacidadTurno) {
        this.nombre = nombre;
        this.diaActual = 1;
        this.dias = new SyncHashMap(50);
        // Agrego el dia 1 y el dia 1+30
        this.dias.put(diaActual, new DiaAgenda(this.diaActual));
        this.dias.put(diaActual + 28, new DiaAgenda(this.diaActual + 28));
        // Los vac funcionan de 8am a 9pm = 13 horas
        // Los turnos serian de 15min -> 4 turnos por hora
        // Total de 52 Turnos por Dia
        this.capacidadTurno = capacidadTurno;
        this.capacidadDia = 52 * capacidadTurno;
        this.capacidadDiaActual = this.capacidadDia;
        // Semaforos para poder agendar correctamente
        this.lugaresDisponibles = new Semaphore(this.capacidadDia, true);
        this.lugaresOcupados = new Semaphore(0);
    }

    public String getNombre() {
        return nombre;
    }

    public String getEstadisticas() {
        return null;
    }

    public SyncHashMap<Integer, DiaAgenda> getDias() {
        return this.dias;
    }

    public DiaAgenda removerDiaActual(int indiceDia) {
        try {
            DiaAgenda dia = this.dias.remove(indiceDia);
            if (diaActual <= indiceDia) {
                capacidadDiaActual = this.cambiarDiaActual();
                lugaresDisponibles.drainPermits();
                lugaresDisponibles.release(capacidadDiaActual);
                lugaresOcupados.drainPermits();
            }
            return dia;
        } catch (InterruptedException ex) {
            System.out.println(ex);
            return null;
        }
    }

    public void agendar(Solicitud solicitud) throws InterruptedException {
        lugaresDisponibles.acquire();// arranca con capacidadDia permisos
        DiaAgenda diaPrimeraDosis = this.dias.get(this.diaActual);
        diaPrimeraDosis.aumentarAgendados();
        DiaAgenda diaSegundaDosis = this.dias.get(this.diaActual + 28);
        diaSegundaDosis.aumentarAgendados();
        lugaresOcupados.release();

        if (lugaresOcupados.tryAcquire(capacidadDiaActual)) {
            // cambio el dia actual para el resto
            capacidadDiaActual = this.cambiarDiaActual();
            lugaresDisponibles.release(capacidadDiaActual);
        }

        // Si llego aca ya se en que dia me agendo
        diaPrimeraDosis.agendarPersona(solicitud);
        diaSegundaDosis.agendarPersona(solicitud);
    }

    private int cambiarDiaActual() throws InterruptedException {
        while (true) {
            this.diaActual++;
            DiaAgenda diaBuscar = this.dias.get(this.diaActual);
            if (diaBuscar != null) {
                int capacidadRestante = this.capacidadDia - diaBuscar.getCantAgendados();
                if (capacidadRestante > 0) {
                    // Me sirve el diaBuscar necesito dia+28 y se que no existe
                    this.dias.put(this.diaActual + 28, new DiaAgenda(this.diaActual + 28));
                    return capacidadRestante;
                }
                // El dia que consegui esta lleno y salteo
            } else {
                // El dia es nulo, creo dia y dia +28
                this.dias.put(this.diaActual, new DiaAgenda(this.diaActual));
                this.dias.put(this.diaActual + 28, new DiaAgenda(this.diaActual + 28));
                return this.capacidadDia;
            }
        }
    }
}
