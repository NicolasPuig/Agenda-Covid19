package Hilos;

import Modelado.Agenda;
import Planificador.MLQ;

/**
 *
 * @author PaoloMazza, SebaMazzey, NicoPuig
 */
public class Debugger implements Runnable {
    
    public static MLQ mlq = MLQ.MLQ;
    public static Agenda agenda = Agenda.AGENDA;
    
    public Debugger() {
        Thread thread = new Thread(this, "Debugger");
        thread.setDaemon(true);
        thread.setPriority(2);
        thread.start();
    }
    
    @Override
    public void run() {
        while (true);
    }
}
