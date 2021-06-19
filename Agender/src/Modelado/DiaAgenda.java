/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelado;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Seba Mazzey
 */
public class DiaAgenda {
    private Queue<Solicitud> personasAgendadas;
    private int cantAgendados;
    private Semaphore mutex;
    
    public DiaAgenda() {
        personasAgendadas = new LinkedList();
        this.cantAgendados = 0;
        this.mutex = new Semaphore(1,true);
    }
    
    public int getCantAgendados() {
        return this.cantAgendados;
    }
    
    public void aumentarAgendados() {
        cantAgendados++;
    }
    
    public void agendarPersona(Solicitud persona) {
        mutex.acquireUninterruptibly();
        personasAgendadas.add(persona);
        this.cantAgendados++;
        mutex.release();
        // TODO: Hacer cosas de estadisticas
    }
}
