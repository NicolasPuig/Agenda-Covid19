package Program;

/**
 * TODO: REVISAR Modela el paso de los dias.
 *
 * @author nicolas
 */
public class Ciclo {

    private final long duracionDia_ms;
    private final long[] data = new long[3];
    private final boolean reportarListaAgendados;

    public Ciclo(long duracionDia_ms, boolean reportarListaAgendados) {
        this.duracionDia_ms = duracionDia_ms;
        this.reportarListaAgendados = reportarListaAgendados;
    }

    public void start() {
        int dia = 1;
        while (true) {
            try {
                
                
                // se lee archivo entrada dia 1
                // Se producen solicitudes
                    /// En paralelo se agenda y se trabaja el mlq
                // Se espera a que se genere el reporte
                // repetir para dia 2
                
                
                System.out.println("Agendado dia " + dia + " en proceso...");
                Thread.sleep(duracionDia_ms);   // TODO: Buscar alternativa al sleep
                System.out.println("Fin del dia. Esperando terminar agendado para obtener reporte...");
                Reporte reporte = Archivador.getReporteDiario();
                analizarReporte(reporte);
                if (reporte.esVacio()) {
                    System.out.println("Nada que reportar! se termina el programa");
                    Reporte.generarArchivoReporteTotal(data);
                    return;
                }
                System.out.println("Comenzo escritura de reporte");
                reporte.generarArchivoReporteDiario(dia++, reportarListaAgendados);
                System.out.println("Termino reporte para " + reporte.getCantidadDeAgendados() + " solicitudes");
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
        }
    }

    private void analizarReporte(Reporte reporte) {
        data[0] += reporte.getCantidadDeAgendados();
        data[1] = reporte.getPersonasEnEspera();
        data[2] = reporte.getVacunasDisponibles();
    }
}
