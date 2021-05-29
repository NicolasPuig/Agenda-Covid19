package Planificador;

/**
 * TODO: Agregar campos faltantes - Fecha de request de solicitud - Fecha de
 * agendado final
 *
 * @author NicoPuig
 */
public class Solicitud {

    private String CI;
    private int edad;
    private final int riesgo;
    private final long horaInicioSolicitud;
    private long horaFinSolicitud;
    private long tiempoDeEspera;

    public Solicitud(String CI, int edad, int riesgo) {
        this.CI = CI;
        this.edad = edad;
        this.riesgo = riesgo;
        this.horaInicioSolicitud = System.nanoTime();
        this.horaFinSolicitud = -1;
        this.tiempoDeEspera = -1;
    }

    public int getRiesgo() {
        return riesgo;
    }

    public String getCI() {
        return CI;
    }

    public void setCI(String CI) {
        this.CI = CI;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    private double nanoToSeconds(long nano) {
        return Math.round(nano / 10000000) / 100D;
    }

    public void setHoraFinSolicitud(long horaFinSolicitud) {
        this.horaFinSolicitud = horaFinSolicitud;
        tiempoDeEspera = horaFinSolicitud - horaInicioSolicitud;
    }

    @Override
    public String toString() {
        return String.format("CI:%s | Edad:%s | Riesgo:%s | Hora Inicio:%s | Hora Fin:%s | Tiempo Espera:%s",
                CI, edad, riesgo, nanoToSeconds(horaInicioSolicitud),
                nanoToSeconds(horaFinSolicitud), nanoToSeconds(tiempoDeEspera));
    }
}
