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
    private String departamento;

    public Solicitud(String CI, int edad, int riesgo, int momentoInicial, String departamento) {
        this.CI = CI;
        this.edad = edad;
        this.riesgo = riesgo;
        this.momentoInicioSolicitud = momentoInicial;
        this.momentoFinSolicitud = -1;
        this.departamento = departamento;
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
        if (this.momentoFinSolicitud == -1) {
            this.momentoFinSolicitud = momentoFinSolicitud;
        }
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public long getTiempoEspera() {
        return this.momentoFinSolicitud - this.momentoInicioSolicitud;
    }

    @Override
    public String toString() {
        return String.format("CI:%s | Edad:%s | Riesgo:%s | Momento Inicio:%s | Momento Fin:%s",
                CI, edad, riesgo, momentoInicioSolicitud, momentoFinSolicitud);
    }
}
