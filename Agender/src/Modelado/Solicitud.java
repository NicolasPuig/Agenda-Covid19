package Modelado;

/**
 *
 * @author PaoloMazza, SebaMazzey, NicoPuig
 */
public class Solicitud {

    private String CI;
    private int edad;
    private final int riesgo;
    private final long momentoInicioSolicitud;
    private long momentoFinSolicitud;

    public Solicitud(String CI, int edad, int riesgo, int momentoInicial) {
        this.CI = CI;
        this.edad = edad;
        this.riesgo = riesgo;
        this.momentoInicioSolicitud = momentoInicial;
        this.momentoFinSolicitud = -1;
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

    public long getMomentoFinSolicitud() {
        return momentoFinSolicitud;
    }

    public long getMomentoInicioSolicitud() {
        return momentoInicioSolicitud;
    }

    public void setMomentoFinSolicitud(long momentoFinSolicitud) {
        this.momentoFinSolicitud = momentoFinSolicitud;
    }

    @Override
    public String toString() {
        return String.format("CI:%s | Edad:%s | Riesgo:%s | Momento Inicio:%s | Momento Fin:%s",
                CI, edad, riesgo, momentoInicioSolicitud, momentoFinSolicitud);
    }
}
