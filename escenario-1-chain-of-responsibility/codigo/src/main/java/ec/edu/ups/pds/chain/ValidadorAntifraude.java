package ec.edu.ups.pds.chain;

public class ValidadorAntifraude extends ValidadorPedido {

    private final ServicioAntifraude antifraude;

    public ValidadorAntifraude(ServicioAntifraude antifraude) {
        this.antifraude = antifraude;
    }

    @Override
    protected ResultadoValidacion validarPropio(Pedido pedido) {
        if (pedido.isClienteCorporativo()) {
            return ResultadoValidacion.aprobado();
        }
        if (antifraude.esSospechoso(pedido)) {
            return ResultadoValidacion.rechazado("Sospecha de fraude detectada");
        }
        return ResultadoValidacion.aprobado();
    }
}
