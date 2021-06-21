package Modelado;

import Planificador.MLQ;

/**
 *
 * @author NicoPuig
 */
public class Debugger implements Runnable {

    public static MLQ mlq = MLQ.MLQ;
    public static Agenda agenda = Agenda.AGENDA;

    public Debugger() {
        Thread thread = new Thread(this, "Debugger");
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void run() {
        while (true);
    }
}
