package Modelado;

import java.util.HashMap;
import Util.ManejadorArchivos;
import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * @author PaoloMazza, SebaMazzey, NicoPuig
 */
public class Agenda {

    public static Agenda AGENDA = new Agenda("src/Archivos/vacunatoriosTest.txt");

    private final HashMap<String, LinkedList<Vacunatorio>> vacunatoriosPorDepartamento = new HashMap<>();
    private final Estadistica estadisticaTotal = new Estadistica();
    private Estadistica estadisticaDiaria = new Estadistica();

    private Agenda(String archDepartamentos) {
        cargarVacunatorios(archDepartamentos);
    }

    public void agendar(Solicitud solicitud) throws InterruptedException {
        Vacunatorio vacunatorio = getMejorVacunatorio(solicitud.getDepartamento());
        vacunatorio.agendar(solicitud);
        estadisticaTotal.analizarSolicitudConTiempo(solicitud);
        estadisticaDiaria.analizarSolicitudConTiempo(solicitud);
    }

    private void cargarVacunatorios(String archDepartamentos) {
        Collection<String> lineas = ManejadorArchivos.leerArchivo(archDepartamentos, true);
        for (String linea : lineas) {
            String[] datos = linea.split(",");
            LinkedList<Vacunatorio> dptoActual = vacunatoriosPorDepartamento.get(datos[0]);
            if (dptoActual == null) {
                dptoActual = new LinkedList<>();
                vacunatoriosPorDepartamento.put(datos[0], dptoActual);
            }
            dptoActual.add(new Vacunatorio(datos[1], Integer.parseInt(datos[2])));
        }
    }

    public HashMap<String, LinkedList<Vacunatorio>> getVacunatoriosPorDepartamento() {
        return vacunatoriosPorDepartamento;
    }

    /**
     * Obtiene la lista de vacunatorios en el departamento solicitado
     *
     * @param departamento el departamento
     * @return la lista de vacunatorios para el departamento
     */
    public LinkedList<Vacunatorio> getVacunatorios(String departamento) {
        return vacunatoriosPorDepartamento.get(departamento);
    }

    /**
     * Busca en el departamento un vacunatorio para agendar lo antes posible a
     * una persona
     *
     * @param departamento el departamento
     * @return un vacunatorio con buena disponibilidad
     */
    public Vacunatorio getMejorVacunatorio(String departamento) {
        return vacunatoriosPorDepartamento.get(departamento).getFirst();
    }

    public Estadistica getEstadisticaDiariaDeSalida() {
        Estadistica estadisticaDiaActual = this.estadisticaDiaria;
        this.estadisticaDiaria = new Estadistica();
        return estadisticaDiaActual;
    }

    public Estadistica getEstadisticaTotalDeSalida() {
        return estadisticaTotal;
    }
}
