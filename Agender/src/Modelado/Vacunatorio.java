/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelado;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Seba Mazzey
 */
public class Vacunatorio {
    private String nombre;
    // Una lista de dias con un array de solicitudes
    // La posicion en el array determina a que turno pertenece
    private HashMap<Integer,DiaAgenda> dias;
    private int capacidadTurno;
    private int capacidadDia;
    private Semaphore lugaresDisp;
    private Semaphore lugaresOcupados;
    private int diaActual;

    public Vacunatorio (String nombre, int capacidadTurno) {
        this.nombre = nombre;
        this.dias = new HashMap<>(50);
        this.diaActual = 1;
        // Agrego el dia 1 y el dia 1+30
        this.dias.put(diaActual, new DiaAgenda());
        this.dias.put(diaActual+28, new DiaAgenda());
        // Los vac funcionan de 8am a 9pm = 13 horas
        // Los turnos serian de 15min -> 4 turnos por hora
        // Total de 52 Turnos por Dia
        this.capacidadTurno = capacidadTurno;
        this.capacidadDia = 52*capacidadTurno;
        // Semaforos para poder agendar correctamente
        this.lugaresDisp = new Semaphore(this.capacidadDia,true);
        this.lugaresOcupados = new Semaphore(0);
    }
    
    public HashMap<Integer,DiaAgenda> getDias() {
        return this.dias;
    }
    
    public void agendar(Solicitud solActual) {    
        lugaresDisp.acquireUninterruptibly();// arranca con capacidadDia permisos
        DiaAgenda diaParaAgendar1 = this.dias.get(this.diaActual);
        diaParaAgendar1.aumentarAgendados();
        DiaAgenda diaParaAgendar2 = this.dias.get(this.diaActual+28);
        diaParaAgendar2.aumentarAgendados();
        
        lugaresOcupados.release();
        if (lugaresOcupados.tryAcquire(capacidadDia)) {
            // cambio el dia actual para el resto
            lugaresDisp.release(this.cambiarDiaActual());
        }
        
        // Si llego aca ya se en que dia me agendo
        diaParaAgendar1.agendarPersona(solActual);
        diaParaAgendar2.agendarPersona(solActual);
    }
    
    public int cambiarDiaActual() {
        while(true) {
            this.diaActual++;
            DiaAgenda diaBuscar = this.dias.get(this.diaActual);
            if (diaBuscar != null) {
                int capacidadRestante = this.capacidadDia - diaBuscar.getCantAgendados();
                if(capacidadRestante > 0) {
                    // Me sirve el diaBuscar necesito dia+28 y se que no existe
                    this.dias.put(diaActual+28, new DiaAgenda());
                    return capacidadRestante;
                }
                // El dia que consegui esta lleno y salteo
                continue;
            } else {
                // El dia es nulo, creo dia y dia +28
                this.dias.put(diaActual, new DiaAgenda());
                this.dias.put(diaActual + 28, new DiaAgenda());
                return this.capacidadDia;
            }
        }
    }
}
