package ec.edu.ups.pds.chain;

public class ResultadoValidacion {

    private final boolean aprobado;
    private final String motivo;

    private ResultadoValidacion(boolean aprobado, String motivo) {
        this.aprobado = aprobado;
        this.motivo   = motivo;
    }

    public static ResultadoValidacion aprobado() {
        return new ResultadoValidacion(true, null);
    }

    public static ResultadoValidacion rechazado(String motivo) {
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("El motivo de rechazo no puede ser nulo ni vacio");
        }
        return new ResultadoValidacion(false, motivo);
    }

    public boolean isAprobado() { return aprobado; }
    public String getMotivo()   { return motivo; }

    @Override
    public String toString() {
        return aprobado ? "APROBADO" : "RECHAZADO: " + motivo;
    }
}
