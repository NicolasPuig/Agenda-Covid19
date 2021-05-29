package Planificador;

/**
 * TODO: Agregar campos faltantes
 * - Fecha de request de solicitud
 * - Fecha de agendado final
 * 
 * @author NicoPuig
 */
public class Solicitud {

    private String CI;
    private int edad;
    private final int riesgo;

    public Solicitud(String CI, int edad, int riesgo) {
        this.CI = CI;
        this.edad = edad;
        this.riesgo = riesgo;
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

    @Override
    public String toString() {
        return "CI: " + CI + " | Edad:" + edad + " | Riesgo: " + riesgo;
    }
}
