package Program;

import Planificador.Solicitud;
import Util.ManejadorArchivos;

/**
 *
 * @author NicoPuig
 */
public class Reporte {

    private Solicitud[] solicitudes;
    private int vacunasDisponibles;
    private int cantAgendados;
    private int personasEnEspera;

    public Reporte() {
    }

    public Reporte(Solicitud[] solicitudes, int vacunasDisponibles, int personasEnEspera) {
        this.solicitudes = solicitudes;
        this.vacunasDisponibles = vacunasDisponibles;
        this.cantAgendados = solicitudes.length;
        this.personasEnEspera = personasEnEspera;
    }

    public int getCantidadDeAgendados() {
        return cantAgendados;
    }

    public int getVacunasDisponibles() {
        return vacunasDisponibles;
    }

    public int getPersonasEnEspera() {
        return personasEnEspera;
    }

    public boolean esVacio() {
        return solicitudes.length == 0;
    }

    public static void generarArchivoReporteTotal(long[] datos) {
        String texto
                = "--- REPORTE TOTAL ---"
                + "\nESTADISTICAS"
                + "\n - Cantidad Agendados:\t" + datos[0]
                + "\n - Cantidad Solicitudes en espera:\t" + datos[1]
                + "\n - Vacunas disponibles:\t" + datos[2];
        ManejadorArchivos.escribirArchivo("src/Archivos/total.txt", texto, false);
    }

    public void generarArchivoReporteDiario(int dia, boolean imprimirListaAgendados) {
        String texto
                = "--- REPORTE DIA " + dia + " ---"
                + "\nESTADISTICAS"
                + "\n - Cantidad Agendados:\t" + cantAgendados
                + "\n - Cantidad Solicitudes en espera:\t" + personasEnEspera
                + "\n - Vacunas disponibles:\t" + vacunasDisponibles;
        if (imprimirListaAgendados) {
            texto += "\n\nLISTA DE AGENDADOS";
            for (int i = 0; i < solicitudes.length; i++) {
                texto += "\n" + i + ")\t" + solicitudes[i].toString();
            }
        }
        ManejadorArchivos.escribirArchivo(getPath(dia), texto, false);
    }

    private String getPath(int dia) {
        return "src/Archivos/dia_" + (dia < 10 ? "0" : "") + dia + ".txt";
    }
}
