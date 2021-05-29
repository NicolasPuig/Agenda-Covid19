package Program;

import Planificador.Solicitud;
import Util.ManejadorArchivos;
import java.util.LinkedList;

/**
 *
 * @author NicoPuig
 */
public class Reporte {

    private final Solicitud[] solicitudes;
    private final int vacunasDisponibles;
    private final int cantAgendados;
    private final int personasEnEspera;

    public Reporte(Solicitud[] solicitudes, int vacunasDisponibles, int personasEnEspera) {
        this.solicitudes = solicitudes;
        this.vacunasDisponibles = vacunasDisponibles;
        this.cantAgendados = solicitudes.length;
        this.personasEnEspera = personasEnEspera;
    }

    public int getCantidadDeAgendados() {
        return cantAgendados;
    }

    public boolean esVacio() {
        return solicitudes.length == 0;
    }

    public void generarArchivoReporteDiario(int dia, boolean imprimirListaAgendados) {
        LinkedList<String> lineas = new LinkedList<>();
        lineas.add("--- REPORTE DIA " + dia + " ---");
        lineas.add("ESTADISTICAS");
        lineas.add("- Cantidad de agendados:\t" + cantAgendados);
        lineas.add("- Cantidad de solicitudes en espera:\t" + personasEnEspera);
        lineas.add("- Vacunas disponibles:\t" + vacunasDisponibles);
        if (imprimirListaAgendados) {
            lineas.add("\nLISTA DE AGENDADOS");
            int i = 0;
            for (Solicitud solicitud : solicitudes) {
                lineas.add(++i + ")\t" + solicitud.toString());
            }
        }
        ManejadorArchivos.escribirArchivo(getPath(dia), lineas, false);
    }

    private String getPath(int dia) {
        return "src/Archivos/dia_" + dia + ".txt";
    }
}
