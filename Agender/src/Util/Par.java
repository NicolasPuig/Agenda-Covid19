package Util;

/**
 *
 * @author NicoPuig
 */
public class Par<M, N> {

    private M primero;
    private N segundo;

    public Par(M primero, N segundo) {
        this.primero = primero;
        this.segundo = segundo;
    }

    public M getPrimero() {
        return primero;
    }

    public void setPrimero(M primero) {
        this.primero = primero;
    }

    public N getSegundo() {
        return segundo;
    }

    public void setSegundo(N segundo) {
        this.segundo = segundo;
    }
}
