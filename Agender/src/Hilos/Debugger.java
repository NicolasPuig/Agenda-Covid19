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
    private Thread thread;

    public Debugger() {
        this.thread = new Thread(this, "Debugger");
        thread.setDaemon(true);
        thread.setPriority(2);
    }

    public void start() {
        thread.start();
    }

    @Override
    public void run() {
        while (true);
    }
}
