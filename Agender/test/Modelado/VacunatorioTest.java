/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelado;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Seba Mazzey
 */
public class VacunatorioTest {

    Vacunatorio vacTest;
    Solicitud solTest1;
    Solicitud solTest2;
    Solicitud solTest3;

    public VacunatorioTest() {
    }

    @Before
    public void setUp() {
        vacTest = new Vacunatorio("Vacunatorio Test", 1);
        solTest1 = new Solicitud("1", 20, 0, 0, "Dep");
        solTest2 = new Solicitud("2", 20, 0, 0, "Dep");
        solTest3 = new Solicitud("3", 20, 0, 0, "Dep");
    }

    /**
     * Test del metodo agendar al recibir una persona. Se espera que esta se
     * agende en el dia 1
     */
    @Test
    public void testAgendarPersonaDia1() {
        try {
            vacTest.agendar(solTest1);
        } catch (InterruptedException ex) {
            System.out.println(ex);
        }

        DiaAgenda diaActual = vacTest.getSolicitudesPorDia().get(1);
        String actual = diaActual.getPersonasAgendadas().remove().getCI();

        String expected = solTest1.getCI();
        assertEquals(expected, actual);
    }

    /**
     * Test del metodo agendar para 1 persona Se espera que su segunda dosis sea
     * 28 dias despues
     */
    @Test
    public void testAgendarPersona2Dosis() {
        try {
            vacTest.agendar(solTest1);
        } catch (InterruptedException ex) {
            System.out.println(ex);
        }

        DiaAgenda diaActual = vacTest.getSolicitudesPorDia().get(29);
        String actual = diaActual.getPersonasAgendadas().remove().getCI();

        String expected = solTest1.getCI();
        assertEquals(expected, actual);
    }

    /**
     * Test del metodo agendar para varias persona Se espera que esten todas en
     * el dia 1
     */
    @Test
    public void testAgendarVariasPersonasDia1() {
        try {
            vacTest.agendar(solTest1);
            vacTest.agendar(solTest2);
            vacTest.agendar(solTest3);

        } catch (InterruptedException ex) {
            Logger.getLogger(VacunatorioTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        DiaAgenda diaActual = vacTest.getSolicitudesPorDia().get(1);
        String agendado1 = diaActual.getPersonasAgendadas().remove().getCI();
        String agendado2 = diaActual.getPersonasAgendadas().remove().getCI();
        String agendado3 = diaActual.getPersonasAgendadas().remove().getCI();

        String expected1 = solTest1.getCI();
        String expected2 = solTest2.getCI();
        String expected3 = solTest3.getCI();
        assertEquals(expected1, agendado1);
        assertEquals(expected2, agendado2);
        assertEquals(expected3, agendado3);
    }

    /**
     * Test del metodo agendar una vez que se llena el dia 1 Se espera que la
     * persona entrante se agende el dia 2
     */
    @Test
    public void testAgendarDia2() {
        try {
            // Lleno el Dia1 del vacunatorio
            for (int i = 0; i < 52; i++) {
                vacTest.agendar(solTest1);
            }
            // Agendo al n°53 (deberia ir al dia 2)
            vacTest.agendar(solTest2);
            DiaAgenda diaActual = vacTest.getSolicitudesPorDia().get(2);
            String actual = diaActual.getPersonasAgendadas().remove().getCI();

            String expected = solTest2.getCI();
            assertEquals(expected, actual);
        } catch (InterruptedException ex) {
            System.out.println(ex);
        }
    }

    /**
     * Test del metodo agendar para una agenda con varios dias llenos Se espera
     * que al elegir siguiente dia para poner como dia actual, la agenda omita
     * los dias que ya se encuentran llenos
     */
    @Test
    public void testOmitirDiasLlenosAlElegirDiaSiguiente() {
        try {
            // Lleno los primeros 28*2 dias del vacunatorio
            // Los primeros 28 son de la 1era dosis
            // Mientras que los restantes 28 son de la 2nda dosis
            for (int i = 0; i < 52 * 28; i++) {
                vacTest.agendar(solTest1);
            }
            // La siguiente agenda deberia caer en el dia 28*2+1 = 57
            vacTest.agendar(solTest2);
            DiaAgenda diaActual = vacTest.getSolicitudesPorDia().get(57);
            String actual = diaActual.getPersonasAgendadas().remove().getCI();

            String expected = solTest2.getCI();
            assertEquals(expected, actual);
        } catch (InterruptedException ex) {
            System.out.println(ex);
        }
    }
}
