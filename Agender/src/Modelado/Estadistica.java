package Modelado;

import Hilos.Estadistico;
import java.util.concurrent.Semaphore;

/**
 *
 * @author PaoloMazza, SebaMazzey, NicoPuig
 */
public class Estadistica {

    protected int cantVacunas = 0;
    protected int cantAgendadosTotal = 0;
    protected int cantRiesgoAlto = 0;
    protected int cantRiesgoBajo18_30 = 0;
    protected int cantRiesgoBajo31_50 = 0;
    protected int cantRiesgoBajo51_65 = 0;

    protected final Semaphore mutexTotal = new Semaphore(1);
    protected final Semaphore mutexRiesgoAlto = new Semaphore(1);
    protected final Semaphore mutexRiesgoBajo18_30 = new Semaphore(1);
    protected final Semaphore mutexRiesgoBajo31_50 = new Semaphore(1);
    protected final Semaphore mutexRiesgoBajo51_65 = new Semaphore(1);

    protected final Semaphore mutexVacunas = new Semaphore(1);
    protected final Semaphore semEstadistica = new Semaphore(0);

    public void pedirAnalisis(Solicitud solicitud) throws InterruptedException {
        Estadistico.analizar(this, solicitud);
    }

    public void analizarSolicitud(Solicitud solicitud) throws InterruptedException {
        mutexTotal.acquire();
        cantAgendadosTotal++;
        mutexTotal.release();

        int riesgo = solicitud.getRiesgo();
        if (riesgo > 0) {
            mutexRiesgoAlto.acquire();
            cantRiesgoAlto++;
            mutexRiesgoAlto.release();
        } else {
            int edad = solicitud.getEdad();
            if (edad < 31) {
                mutexRiesgoBajo18_30.acquire();
                cantRiesgoBajo18_30++;
                mutexRiesgoBajo18_30.release();
            } else if (edad < 51) {
                mutexRiesgoBajo31_50.acquire();
                cantRiesgoBajo31_50++;
                mutexRiesgoBajo31_50.release();
            } else {
                mutexRiesgoBajo51_65.acquire();
                cantRiesgoBajo51_65++;
                mutexRiesgoBajo51_65.release();
            }
        }
    }

    public void agregarVacunas(int cantidad) throws InterruptedException {
        mutexVacunas.acquire();
        cantVacunas += cantidad;
        mutexVacunas.release();
    }

    private static float porcentaje(int cantidad, int total) throws ArithmeticException {
        return total != 0 ? Math.round(1000L * 100 * cantidad / total) / 1000f : 0f;
    }

    public int getCantidadVacunas() {
        return cantVacunas;
    }

    public void esperarFinAnalisis() {
        try {
            Estadistico.avisarCuandoTermine(this);
            semEstadistica.acquire();
        } catch (InterruptedException ex) {
            System.out.println(ex);
        }
    }

    public void terminarAnalisis() {
        semEstadistica.release();
    }

    public static String csvCantidadTotal(Estadistica entrada, Estadistica salida) {
        String texto = String.join(";", "Solicitudes entrantes",
                String.valueOf(entrada.cantAgendadosTotal),
                String.valueOf(entrada.cantRiesgoAlto),
                String.valueOf(entrada.cantRiesgoBajo18_30),
                String.valueOf(entrada.cantRiesgoBajo31_50),
                String.valueOf(entrada.cantRiesgoBajo51_65));
        return texto + "\n" + String.join(";", "Solicitudes agendadas",
                String.valueOf(salida.cantAgendadosTotal),
                String.valueOf(salida.cantRiesgoAlto),
                String.valueOf(salida.cantRiesgoBajo18_30),
                String.valueOf(salida.cantRiesgoBajo31_50),
                String.valueOf(salida.cantRiesgoBajo51_65));
    }

    public String csvCantidadPorMomento(int momento) {
        return String.join(";", String.valueOf(momento),
                String.valueOf(cantRiesgoAlto),
                String.valueOf(cantRiesgoBajo18_30),
                String.valueOf(cantRiesgoBajo31_50),
                String.valueOf(cantRiesgoBajo51_65));
    }

    public static String csvPorcentajePorMomento(Estadistica entrada, Estadistica salida, int momento) {
        return String.join(";", String.valueOf(momento),
                String.valueOf(porcentaje(salida.cantRiesgoAlto, entrada.cantRiesgoAlto)),
                String.valueOf(porcentaje(salida.cantRiesgoBajo18_30, entrada.cantRiesgoBajo18_30)),
                String.valueOf(porcentaje(salida.cantRiesgoBajo31_50, entrada.cantRiesgoBajo31_50)),
                String.valueOf(porcentaje(salida.cantRiesgoBajo51_65, entrada.cantRiesgoBajo51_65)));
    }

    public static String csvPorcentajeTotal(Estadistica entrada, Estadistica salida) {
        return String.join(";",
                String.valueOf(porcentaje(salida.cantAgendadosTotal, entrada.cantAgendadosTotal)),
                String.valueOf(porcentaje(salida.cantRiesgoAlto, entrada.cantRiesgoAlto)),
                String.valueOf(porcentaje(salida.cantRiesgoBajo18_30, entrada.cantRiesgoBajo18_30)),
                String.valueOf(porcentaje(salida.cantRiesgoBajo31_50, entrada.cantRiesgoBajo31_50)),
                String.valueOf(porcentaje(salida.cantRiesgoBajo51_65, entrada.cantRiesgoBajo51_65)));
    }

    public static String comparar(Estadistica entrada, Estadistica salida) {
        int bajoRiesgoTotalEntrada = entrada.cantRiesgoBajo18_30 + entrada.cantRiesgoBajo31_50 + entrada.cantRiesgoBajo51_65;
        int bajoRiesgoTotalSalida = salida.cantRiesgoBajo18_30 + salida.cantRiesgoBajo31_50 + salida.cantRiesgoBajo51_65;
        boolean deColaDeEsperaTotal = salida.cantAgendadosTotal > entrada.cantAgendadosTotal;
        boolean deColaDeEsperaAltoRiesgo = salida.cantRiesgoAlto > entrada.cantRiesgoAlto;
        boolean deColaDeEspera18_30 = salida.cantRiesgoBajo18_30 > entrada.cantRiesgoBajo18_30;
        boolean deColaDeEspera31_50 = salida.cantRiesgoBajo31_50 > entrada.cantRiesgoBajo31_50;
        boolean deColaDeEspera51_65 = salida.cantRiesgoBajo51_65 > entrada.cantRiesgoBajo51_65;
        boolean deColaDeEspera = deColaDeEsperaTotal || deColaDeEsperaAltoRiesgo || deColaDeEspera18_30 || deColaDeEspera31_50 || deColaDeEspera51_65;
        boolean deColaDeEsperaBajoRiesgo = deColaDeEspera18_30 || deColaDeEspera31_50 || deColaDeEspera51_65;

        return "  -Cantidad Total de Solicitudes: " + entrada.cantAgendadosTotal + ", donde " + salida.cantAgendadosTotal + " (" + porcentaje(salida.cantAgendadosTotal, entrada.cantAgendadosTotal) + "%) fueron agendadas" + (deColaDeEsperaTotal ? "*" : "")
                + "\n  -El " + porcentaje(entrada.cantRiesgoAlto, entrada.cantAgendadosTotal) + "% de las solicitudes totales son de alto riesgo, donde " + porcentaje(salida.cantRiesgoAlto, entrada.cantRiesgoAlto) + "% de ellas fueron agendadas" + (deColaDeEsperaAltoRiesgo ? "*" : "")
                + "\n  -El " + porcentaje(bajoRiesgoTotalEntrada, entrada.cantAgendadosTotal) + "% de las solicitudes totales son de bajo riesgo, donde " + porcentaje(bajoRiesgoTotalSalida, bajoRiesgoTotalEntrada) + "% de ellas fueron agendadas" + (deColaDeEsperaBajoRiesgo ? "*" : "")
                + "\n  -El " + porcentaje(entrada.cantRiesgoBajo18_30, bajoRiesgoTotalEntrada) + "% de las solicitudes de bajo riesgo fueron entre 18 y 30 años, donde " + porcentaje(salida.cantRiesgoBajo18_30, entrada.cantRiesgoBajo18_30) + "% de ellas fueron agendadas" + (deColaDeEspera18_30 ? "*" : "")
                + "\n  -El " + porcentaje(entrada.cantRiesgoBajo31_50, bajoRiesgoTotalEntrada) + "% de las solicitudes de bajo riesgo fueron entre 31 y 50 años, donde " + porcentaje(salida.cantRiesgoBajo31_50, entrada.cantRiesgoBajo31_50) + "% de ellas fueron agendadas" + (deColaDeEspera31_50 ? "*" : "")
                + "\n  -El " + porcentaje(entrada.cantRiesgoBajo51_65, bajoRiesgoTotalEntrada) + "% de las solicitudes de bajo riesgo fueron entre 51 y 65 años, donde " + porcentaje(salida.cantRiesgoBajo51_65, entrada.cantRiesgoBajo51_65) + "% de ellas fueron agendadas" + (deColaDeEspera51_65 ? "*" : "")
                + (deColaDeEspera ? "\n(*) Se agendaron solicitudes en espera de dias anteriores" : "");
    }

    @Override
    public String toString() {
        if (cantAgendadosTotal == 0) {
            return "  -Cantidad Total de Agendados:\t0";
        }
        int bajoRiesgoTotal = cantRiesgoBajo18_30 + cantRiesgoBajo31_50 + cantRiesgoBajo51_65;
        return "  -Cantidad Total de Agendados:\t" + cantAgendadosTotal
                + "\n  -Cantidad Agendados de alto riesgo:\t" + cantRiesgoAlto + " (" + porcentaje(cantRiesgoAlto, cantAgendadosTotal) + "%)"
                + "\n  -Cantidad Agendados de bajo riesgo:\t" + bajoRiesgoTotal + " (" + porcentaje(bajoRiesgoTotal, cantAgendadosTotal) + "%)"
                + "\n  -Cantidad Agendados de 18 a 30 años:\t" + cantRiesgoBajo18_30 + " (" + porcentaje(cantRiesgoBajo18_30, cantAgendadosTotal) + "%)"
                + "\n  -Cantidad Agendados de 31 a 50 años:\t" + cantRiesgoBajo31_50 + " (" + porcentaje(cantRiesgoBajo31_50, cantAgendadosTotal) + "%)"
                + "\n  -Cantidad Agendados de 51 a 65 años:\t" + cantRiesgoBajo51_65 + " (" + porcentaje(cantRiesgoBajo51_65, cantAgendadosTotal) + "%)";
    }
}
