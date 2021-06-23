package Modelado;

import java.util.concurrent.Semaphore;

/**
 *
 * @author PaoloMazza, SebaMazzey, NicoPuig
 */
public class Estadistica {

    private int cantAgendadosTotal = 0;
    private int cantRiesgoAlto = 0;
    private int cantRiesgoBajo18_30 = 0;
    private int cantRiesgoBajo31_50 = 0;
    private int cantRiesgoBajo51_65 = 0;
    private long tiempoEsperaTotal = 0;
    private long tiempoEsperaRiesgoAlto = 0;
    private long tiempoEsperaRiesgoBajo18_30 = 0;
    private long tiempoEsperaRiesgoBajo31_50 = 0;
    private long tiempoEsperaRiesgoBajo51_65 = 0;

    private final Semaphore mutexTotal = new Semaphore(1);
    private final Semaphore mutexRiesgoAlto = new Semaphore(1);
    private final Semaphore mutexRiesgoBajo18_30 = new Semaphore(1);
    private final Semaphore mutexRiesgoBajo31_50 = new Semaphore(1);
    private final Semaphore mutexRiesgoBajo51_65 = new Semaphore(1);
    private final Semaphore mutexTiempoEsperaTotal = new Semaphore(1);

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

    public void analizarSolicitudConTiempo(Solicitud solicitud) throws InterruptedException {
        mutexTotal.acquire();
        cantAgendadosTotal++;
        mutexTotal.release();

        mutexTiempoEsperaTotal.acquire();
        long tiempoEspera = solicitud.getTiempoEspera();
        tiempoEsperaTotal += tiempoEspera;
        mutexTiempoEsperaTotal.release();

        int riesgo = solicitud.getRiesgo();
        if (riesgo > 0) {
            mutexRiesgoAlto.acquire();
            cantRiesgoAlto++;
            tiempoEsperaRiesgoAlto += tiempoEspera;
            mutexRiesgoAlto.release();
        } else {
            int edad = solicitud.getEdad();
            if (edad < 31) {
                mutexRiesgoBajo18_30.acquire();
                cantRiesgoBajo18_30++;
                tiempoEsperaRiesgoBajo18_30 += tiempoEspera;
                mutexRiesgoBajo18_30.release();
            } else if (edad < 51) {
                mutexRiesgoBajo31_50.acquire();
                cantRiesgoBajo31_50++;
                tiempoEsperaRiesgoBajo31_50 += tiempoEspera;
                mutexRiesgoBajo31_50.release();
            } else {
                mutexRiesgoBajo51_65.acquire();
                cantRiesgoBajo51_65++;
                tiempoEsperaRiesgoBajo51_65 += tiempoEspera;
                mutexRiesgoBajo51_65.release();
            }
        }
    }

    private static float porcentaje(int cantidad, int total) throws ArithmeticException {
        return total != 0 ? Math.round(1000L * 100 * cantidad / total) / 1000f : 0f;
    }

    public static String comparar(Estadistica entrada, Estadistica salida) {
        int bajoRiesgoTotalEntrada = entrada.cantRiesgoBajo18_30 + entrada.cantRiesgoBajo31_50 + entrada.cantRiesgoBajo51_65;
        int bajoRiesgoTotalSalida = salida.cantRiesgoBajo18_30 + salida.cantRiesgoBajo31_50 + salida.cantRiesgoBajo51_65;
        return "  -Cantidad Total de Solicitudes: " + entrada.cantAgendadosTotal + ", donde " + salida.cantAgendadosTotal + " (" + porcentaje(salida.cantAgendadosTotal, entrada.cantAgendadosTotal) + "%) fueron agendadas"
                + "\n  -El " + porcentaje(entrada.cantRiesgoAlto, entrada.cantAgendadosTotal) + "% de las solicitudes totales son de alto riesgo, donde " + porcentaje(salida.cantRiesgoAlto, entrada.cantRiesgoAlto) + "% de ellas fueron agendadas"
                + "\n  -El " + porcentaje(bajoRiesgoTotalEntrada, entrada.cantAgendadosTotal) + "% de las solicitudes totales son de bajo riesgo, donde " + porcentaje(bajoRiesgoTotalSalida, bajoRiesgoTotalEntrada) + "% de ellas fueron agendadas"
                + "\n  -El " + porcentaje(entrada.cantRiesgoBajo18_30, bajoRiesgoTotalEntrada) + "% de las solicitudes de bajo riesgo fueron entre 18 y 30 años, donde " + porcentaje(salida.cantRiesgoBajo18_30, entrada.cantRiesgoBajo18_30) + "% de ellas fueron agendadas"
                + "\n  -El " + porcentaje(entrada.cantRiesgoBajo31_50, bajoRiesgoTotalEntrada) + "% de las solicitudes de bajo riesgo fueron entre 31 y 50 años, donde " + porcentaje(salida.cantRiesgoBajo31_50, entrada.cantRiesgoBajo31_50) + "% de ellas fueron agendadas"
                + "\n  -El " + porcentaje(entrada.cantRiesgoBajo51_65, bajoRiesgoTotalEntrada) + "% de las solicitudes de bajo riesgo fueron entre 51 y 65 años, donde " + porcentaje(salida.cantRiesgoBajo51_65, entrada.cantRiesgoBajo51_65) + "% de ellas fueron agendadas";
    }

    @Override
    public String toString() {
        if (cantAgendadosTotal == 0) {
            return "  -Cantidad Total de Agendados:\t0";
        }
        int bajoRiesgoTotal = cantRiesgoBajo18_30 + cantRiesgoBajo31_50 + cantRiesgoBajo51_65;
        float mediaTiempoEspera = (float) tiempoEsperaTotal / cantAgendadosTotal;
        float mediaTiempoEsperaRiesgo = cantRiesgoAlto > 0 ? (float) tiempoEsperaRiesgoAlto / cantRiesgoAlto : 0f;
        float mediaTiempoEspera18_30 = cantRiesgoBajo18_30 > 0 ? (float) tiempoEsperaRiesgoBajo18_30 / cantRiesgoBajo18_30 : 0f;
        float mediaTiempoEspera31_50 = cantRiesgoBajo31_50 > 0 ? (float) tiempoEsperaRiesgoBajo31_50 / cantRiesgoBajo31_50 : 0f;
        float mediaTiempoEspera51_65 = cantRiesgoBajo51_65 > 0 ? (float) tiempoEsperaRiesgoBajo51_65 / cantRiesgoBajo51_65 : 0f;

        return "  -Cantidad Total de Agendados:\t" + cantAgendadosTotal
                + "\n  -Cantidad Agendados de alto riesgo:\t" + cantRiesgoAlto + " (" + porcentaje(cantRiesgoAlto, cantAgendadosTotal) + "%)"
                + "\n  -Cantidad Agendados de bajo riesgo:\t" + bajoRiesgoTotal + " (" + porcentaje(bajoRiesgoTotal, cantAgendadosTotal) + "%)"
                + "\n  -Cantidad Agendados de 18 a 30 años:\t" + cantRiesgoBajo18_30 + " (" + porcentaje(cantRiesgoBajo18_30, cantAgendadosTotal) + "%)"
                + "\n  -Cantidad Agendados de 31 a 50 años:\t" + cantRiesgoBajo31_50 + " (" + porcentaje(cantRiesgoBajo31_50, cantAgendadosTotal) + "%)"
                + "\n  -Cantidad Agendados de 51 a 65 años:\t" + cantRiesgoBajo51_65 + " (" + porcentaje(cantRiesgoBajo51_65, cantAgendadosTotal) + "%)"
                + "\n\n  -Tiempo de espera promedio:\t" + mediaTiempoEspera + (mediaTiempoEspera == 1 ? " día" : " días")
                + "\n  -Tiempo de espera promedio para personas con riesgo alto:\t" + mediaTiempoEsperaRiesgo + (mediaTiempoEsperaRiesgo == 1 ? " día" : " días")
                + "\n  -Tiempo de espera promedio para personas de 18-30 años:\t" + mediaTiempoEspera18_30 + (mediaTiempoEspera18_30 == 1 ? " día" : " días")
                + "\n  -Tiempo de espera promedio para personas de 31-50 años:\t" + mediaTiempoEspera31_50 + (mediaTiempoEspera31_50 == 1 ? " día" : " días")
                + "\n  -Tiempo de espera promedio para personas de 51-65 años:\t" + mediaTiempoEspera51_65 + (mediaTiempoEspera51_65 == 1 ? " día" : " días");
    }
}
