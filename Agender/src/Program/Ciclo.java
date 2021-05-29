package Program;

import Planificador.MLQ;
import Planificador.Solicitud;
import Util.ManejadorArchivos;
import java.util.LinkedList;

/**
 * TODO: REVISAR
 * Modela el paso de los dias.
 * @author nicolas
 */
public class Ciclo {

    private final static MLQ mlq = MLQ.MLQ;
    
    private final LinkedList<Reporte> reportes = new LinkedList<>();
    private final long duracionDia_ms;

    public Ciclo(long duracionDia_ms) {
        this.duracionDia_ms = duracionDia_ms;
    }

    public void start() {
        int dia = 1;
        while (true) {
            try {
                System.out.println("Agendado dia " + dia + " en proceso...");
                Thread.sleep(duracionDia_ms);   // TODO: Buscar alternativa al sleep
                System.out.println("Fin del dia. Esperando terminar agendado para obtener reporte...");
                Reporte reporte = Archivador.getReporteDiario();
                if (reporte.esVacio()) {
                    System.out.println("Nada que reportar! se termina el programa");
                    return;
                }
                reportes.add(reporte);
                System.out.println("Comenzo escritura de reporte");
                reporte.generarArchivoReporteDiario(dia++, true);
                System.out.println("Termino reporte para " + reporte.getCantidadDeAgendados() + " solicitudes");
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
        }
    }
}
