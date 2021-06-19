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

/**
 *
 * @author Seba Mazzey
 */
public class Agenda {
    private HashMap<String, LinkedList<Vacunatorio>> vacunatoriosDpto;
    
    public Agenda(String archDepartamentos) {
        cargarDepartamentos(archDepartamentos);
    }
    
    private void cargarDepartamentos(String archDepartamentos) {
        Collection<String> departamentos = ManejadorArchivos.leerArchivo(archDepartamentos, true);
        int tamanioHash = (int)Math.ceil(departamentos.size()/0.75);
        this.vacunatoriosDpto = new HashMap<>(tamanioHash);
        for (String departamento: departamentos) {
            vacunatoriosDpto.put(departamento, new LinkedList<>());
        }
    }
    
    /**
     * Obtiene la lista de vacunatorios en el departamento solicitado
     * @param departamento el departamento
     * @return la lista de vacunatorios para el departamento
     */
    public LinkedList<Vacunatorio> getVacunatorios(String departamento) {
        return vacunatoriosDpto.get(departamento);
    }
    
    /**
     * Busca en el departamento un vacunatorio para agendar lo antes posible
     * a una persona
     * @param departamento el departamento
     * @return un vacunatorio con buena disponibilidad
     */
    public Vacunatorio getMejorVacunatorio(String departamento) {
        return vacunatoriosDpto.get(departamento).getFirst();
    }
}
    