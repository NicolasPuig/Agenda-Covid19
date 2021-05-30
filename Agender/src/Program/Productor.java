/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Program;

import Planificador.MLQ;
import Planificador.Solicitud;
import Util.ManejadorArchivos;
import java.util.Collection;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Seba Mazzey
 */
public class Productor extends Thread {

    private final String archEntrada;
    private static MLQ mlq = MLQ.MLQ;
    private static Semaphore semaforoProductores = new Semaphore(0, true);

    private static int cantidadProductores = 0;

    public Productor(String archEntrada) {
        super("P-" + cantidadProductores++);
        this.archEntrada = archEntrada;
    }

    public static Semaphore getSemaforoProductores() {
        return semaforoProductores;
    }

    @Override
    public void run() {
        // Leo el archivo de entrada
        Collection<String> personas = ManejadorArchivos.leerArchivo(archEntrada, true);
        int momentoActual = 1;
        for (String persona : personas) {
            // personas = momento;CI;edad;Riesgo
            String[] datos = persona.split(";");
            // Mientras el momento no cambia
            if (Integer.parseInt(datos[0]) == momentoActual) {
                // Genero y agrego la solicitud al mlq
                int edad = Integer.parseInt(datos[2]);
                int riesgo = Integer.parseInt(datos[3]);
                try {
                    mlq.insert(new Solicitud(datos[1], edad, riesgo));
                } catch (Exception e) {
                    System.out.println(e);
                }
            } else {
                // Aviso que termine de procesar las solicitudes del dia
                // semaforoPepito.release();
                Reporte.getSemReportes().release();
                // Espero a que se emita el reporte y me avisen
                semaforoProductores.acquireUninterruptibly();
                momentoActual++;
                // Proceso a la persona que me quedo pendiente
                int edad = Integer.parseInt(datos[2]);
                int riesgo = Integer.parseInt(datos[3]);
                try {
                    mlq.insert(new Solicitud(datos[1], edad, riesgo));
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
        Reporte.getSemReportes().release();
        // Si llego aca deje de producir
    }
}
