package ec.edu.ups.pds.chain;

public class ValidadorDireccion extends ValidadorPedido {

    private final CoberturaEntrega cobertura;

    public ValidadorDireccion(CoberturaEntrega cobertura) {
        this.cobertura = cobertura;
    }

    @Override
    protected ResultadoValidacion validarPropio(Pedido pedido) {
        if (!cobertura.atiende(pedido.getDireccion())) {
            return ResultadoValidacion.rechazado("Direccion sin cobertura de entrega");
        }
        return ResultadoValidacion.aprobado();
    }
}
