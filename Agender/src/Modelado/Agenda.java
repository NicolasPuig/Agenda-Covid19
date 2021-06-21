/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelado;

import java.util.HashMap;
import Util.ManejadorArchivos;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Seba Mazzey
 */
public class Agenda {

    private final HashMap<String, LinkedList<Vacunatorio>> vacunatoriosPorDepartamento = new HashMap<>();
    public static Agenda AGENDA = new Agenda("src/Archivos/vacunatoriosTest.txt");
    private final Estadistica estadisticaTotal = new Estadistica();
    private Estadistica estadisticaDiaria = new Estadistica();
    private Semaphore sem = new Semaphore(1);

    public Agenda(String archDepartamentos) {
        cargarVacunatorios(archDepartamentos);
    }

    public void agendar(Solicitud solicitud) throws InterruptedException {
        Vacunatorio vacunatorio = getMejorVacunatorio(solicitud.getDepartamento());
        vacunatorio.agendar(solicitud);
        estadisticaTotal.analizarSolicitud(solicitud);
        estadisticaDiaria.analizarSolicitud(solicitud);
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
        try {
            sem.acquire();
            LinkedList<Vacunatorio> vacs = vacunatoriosPorDepartamento.get(departamento);
            Vacunatorio vac = vacs.getFirst();
            sem.release();
            return vac;

        } catch (InterruptedException ex) {
            System.out.println(ex);
        } catch (NullPointerException np) {
            System.out.println(vacunatoriosPorDepartamento.toString());
        }
        return null;
    }

    public Estadistica getEstadisticaDiaria() {
        Estadistica estadisticaDiaActual = this.estadisticaDiaria;
        this.estadisticaDiaria = new Estadistica();
        return estadisticaDiaActual;
    }

    public Estadistica getEstadisticaTotal() {
        return estadisticaTotal;
    }
}
