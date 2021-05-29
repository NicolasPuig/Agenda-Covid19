package Program;

import Planificador.*;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Clase prototipo de Agendador Hilo que inserta solicitudes random al MLQ La
 * idea es tener varios de estos hilos insertando La sincronizacion ya esta
 * resuelta en el MLQ, dentro de cada cola
 *
 * @author NicoPuig
 */
public class Inserter implements Runnable {

    private final Thread thread;
    private final String name;
    public static MLQ MLQ;
    public static int cantidadDeSolicitudes = 1000;
    public static Collection<String> lista = new LinkedList<>();

    public Inserter(String name) {
        this.name = "I-" + name;
        this.thread = new Thread(this, this.name);
    }

    public void start() {
        thread.start();
    }

    @Override
    public void run() {
        for (int i = 0; i < cantidadDeSolicitudes; i++) {
            // Simulacion de Solicitud
            // Se genera edad random entre 18 y 115, si es mayor a 65 tiene riesgo mayor a 0
            // Si es menor a 65, hay 0.2 de posibilidad que salga con comorbilidad, y el riesgo sea mayor a 0
            // Riesgo es de 0 a 5
            
            int edad = (int) (Math.floor(Math.random() * (90 - 18)) + 18);
            int riesgo = (edad > 65 || Math.random() > 0.8) ? (int) (Math.ceil(Math.random() * 5)) : 0;
            Solicitud solicitud = new Solicitud(this.name + "-" + String.valueOf(edad), edad, riesgo);
            try {
                MLQ.insert(solicitud);
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
        }
    }
}
