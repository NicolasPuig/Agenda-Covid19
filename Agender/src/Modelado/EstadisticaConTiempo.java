package Modelado;

import java.util.concurrent.Semaphore;

/**
 *
 * @author PaoloMazza, SebaMazzey, NicoPuig
 */
public class EstadisticaConTiempo extends Estadistica {

    private float tiempoEsperaTotal = 0;
    private float tiempoEsperaRiesgoAlto = 0;
    private float tiempoEsperaRiesgoBajo18_30 = 0;
    private float tiempoEsperaRiesgoBajo31_50 = 0;
    private float tiempoEsperaRiesgoBajo51_65 = 0;

    private final Semaphore mutexTiempoEsperaTotal = new Semaphore(1);

    @Override
    public void analizarSolicitud(Solicitud solicitud) throws InterruptedException {
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

    public String csvTiempoEsperaPromedio() {
        float mediaTiempoEspera = cantAgendadosTotal > 0 ? tiempoEsperaTotal / cantAgendadosTotal : 0f;
        float mediaTiempoEsperaRiesgo = cantRiesgoAlto > 0 ? tiempoEsperaRiesgoAlto / cantRiesgoAlto : 0f;
        float mediaTiempoEspera18_30 = cantRiesgoBajo18_30 > 0 ? tiempoEsperaRiesgoBajo18_30 / cantRiesgoBajo18_30 : 0f;
        float mediaTiempoEspera31_50 = cantRiesgoBajo31_50 > 0 ? tiempoEsperaRiesgoBajo31_50 / cantRiesgoBajo31_50 : 0f;
        float mediaTiempoEspera51_65 = cantRiesgoBajo51_65 > 0 ? tiempoEsperaRiesgoBajo51_65 / cantRiesgoBajo51_65 : 0f;
        return String.join(";",
                String.valueOf(mediaTiempoEspera),
                String.valueOf(mediaTiempoEsperaRiesgo),
                String.valueOf(mediaTiempoEspera18_30),
                String.valueOf(mediaTiempoEspera31_50),
                String.valueOf(mediaTiempoEspera51_65)
        );
    }

    @Override
    public String toString() {
        float mediaTiempoEspera = cantAgendadosTotal > 0 ? tiempoEsperaTotal / cantAgendadosTotal : 0f;
        float mediaTiempoEsperaRiesgo = cantRiesgoAlto > 0 ? tiempoEsperaRiesgoAlto / cantRiesgoAlto : 0f;
        float mediaTiempoEspera18_30 = cantRiesgoBajo18_30 > 0 ? tiempoEsperaRiesgoBajo18_30 / cantRiesgoBajo18_30 : 0f;
        float mediaTiempoEspera31_50 = cantRiesgoBajo31_50 > 0 ? tiempoEsperaRiesgoBajo31_50 / cantRiesgoBajo31_50 : 0f;
        float mediaTiempoEspera51_65 = cantRiesgoBajo51_65 > 0 ? tiempoEsperaRiesgoBajo51_65 / cantRiesgoBajo51_65 : 0f;
        return super.toString()
                + "\n\n  -Tiempo de espera promedio:\t" + mediaTiempoEspera + (mediaTiempoEspera == 1 ? " día" : " días")
                + "\n  -Tiempo de espera promedio para personas con riesgo alto:\t" + mediaTiempoEsperaRiesgo + (mediaTiempoEsperaRiesgo == 1 ? " día" : " días")
                + "\n  -Tiempo de espera promedio para personas de 18-30 años:\t" + mediaTiempoEspera18_30 + (mediaTiempoEspera18_30 == 1 ? " día" : " días")
                + "\n  -Tiempo de espera promedio para personas de 31-50 años:\t" + mediaTiempoEspera31_50 + (mediaTiempoEspera31_50 == 1 ? " día" : " días")
                + "\n  -Tiempo de espera promedio para personas de 51-65 años:\t" + mediaTiempoEspera51_65 + (mediaTiempoEspera51_65 == 1 ? " día" : " días");
    }
}
