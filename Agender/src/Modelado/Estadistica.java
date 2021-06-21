package Modelado;

import java.util.concurrent.Semaphore;

/**
 *
 * @author NicoPuig
 */
public class Estadistica {

    private int cantAgendadosTotal = 0;
    private int cantAgendadosRiesgoAlto = 0;
    private int cantAgendadosRiesgoBajo18_30 = 0;
    private int cantAgendadosRiesgoBajo31_50 = 0;
    private int cantAgendadosRiesgoBajo51_65 = 0;
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
        mutexTiempoEsperaTotal.acquire();
        long tiempoEspera =  solicitud.getTiempoEspera();
        tiempoEsperaTotal += tiempoEspera;
        mutexTiempoEsperaTotal.release();

        int riesgo = solicitud.getRiesgo();
        if (riesgo > 0) {
            mutexRiesgoAlto.acquire();
            cantAgendadosRiesgoAlto++;
            tiempoEsperaRiesgoAlto += tiempoEspera;
            mutexRiesgoAlto.release();
        } else {
            int edad = solicitud.getEdad();
            if (edad < 31) {
                mutexRiesgoBajo18_30.acquire();
                cantAgendadosRiesgoBajo18_30++;
                tiempoEsperaRiesgoBajo18_30 += tiempoEspera;
                mutexRiesgoBajo18_30.release();
            } else if (edad < 51) {
                mutexRiesgoBajo31_50.acquire();
                cantAgendadosRiesgoBajo31_50++;
                tiempoEsperaRiesgoBajo31_50 += tiempoEspera;
                mutexRiesgoBajo31_50.release();
            } else {
                mutexRiesgoBajo51_65.acquire();
                cantAgendadosRiesgoBajo51_65++;
                tiempoEsperaRiesgoBajo51_65 += tiempoEspera;
                mutexRiesgoBajo51_65.release();
            }
        }
    }

    private float porcentaje(int cantidad) throws ArithmeticException {
        return cantAgendadosTotal != 0 ? Math.round(1000 * 100 * cantidad / cantAgendadosTotal) / 1000f : 0f;
    }

    @Override
    public String toString() {
        if (cantAgendadosTotal == 0) {
            return " -Cantidad Total de Agendados:\t0";
        }
        int bajoRiesgoTotal = cantAgendadosRiesgoBajo18_30 + cantAgendadosRiesgoBajo31_50 + cantAgendadosRiesgoBajo51_65;
        float mediaTiempoEspera = (float) tiempoEsperaTotal/cantAgendadosTotal;
        float mediaTiempoEsperaRiesgo = cantAgendadosRiesgoAlto > 0 ? (float) tiempoEsperaRiesgoAlto/cantAgendadosRiesgoAlto : 0f;
        float mediaTiempoEspera18_30 = cantAgendadosRiesgoBajo18_30 > 0 ? (float) tiempoEsperaRiesgoBajo18_30/cantAgendadosRiesgoBajo18_30 : 0f;
        float mediaTiempoEspera31_50 = cantAgendadosRiesgoBajo31_50 > 0 ? (float) tiempoEsperaRiesgoBajo31_50/cantAgendadosRiesgoBajo31_50 : 0f;
        float mediaTiempoEspera51_65 = cantAgendadosRiesgoBajo51_65 > 0 ? (float) tiempoEsperaRiesgoBajo51_65/cantAgendadosRiesgoBajo51_65 : 0f;
        
        return " -Cantidad Total de Agendados:\t" + cantAgendadosTotal
                + "\n -Cantidad Agendados de alto riesgo:\t" + cantAgendadosRiesgoAlto + " (" + porcentaje(cantAgendadosRiesgoAlto) + "%)"
                + "\n -Cantidad Agendados de bajo riesgo:\t" + bajoRiesgoTotal + " (" + porcentaje(bajoRiesgoTotal) + "%)"
                + "\n -Cantidad Agendados de 18 a 30 años:\t" + cantAgendadosRiesgoBajo18_30 + " (" + porcentaje(cantAgendadosRiesgoBajo18_30) + "%)"
                + "\n -Cantidad Agendados de 31 a 50 años:\t" + cantAgendadosRiesgoBajo31_50 + " (" + porcentaje(cantAgendadosRiesgoBajo31_50) + "%)"
                + "\n -Cantidad Agendados de 51 a 65 años:\t" + cantAgendadosRiesgoBajo51_65 + " (" + porcentaje(cantAgendadosRiesgoBajo51_65) + "%)"
                + "\n -Tiempo de espera promedio:\t" + mediaTiempoEspera + (mediaTiempoEspera == 1 ? " día" : " días")
                + "\n -Tiempo de espera promedio para personas con riesgo alto:\t" + mediaTiempoEsperaRiesgo + (mediaTiempoEsperaRiesgo == 1 ? " día" : " días")
                + "\n -Tiempo de espera promedio para personas de 18-30 años:\t" + mediaTiempoEspera18_30 + (mediaTiempoEspera18_30 == 1 ? " día" : " días")
                + "\n -Tiempo de espera promedio para personas de 31-50 años:\t" + mediaTiempoEspera31_50 + (mediaTiempoEspera31_50 == 1 ? " día" : " días")
                + "\n -Tiempo de espera promedio para personas de 51-65 años:\t" + mediaTiempoEspera51_65 + (mediaTiempoEspera51_65 == 1 ? " día" : " días");
    }
}
