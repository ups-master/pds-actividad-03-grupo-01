package ec.edu.ups.pds.chain;

public class ValidadorCredito extends ValidadorPedido {

    @Override
    protected ResultadoValidacion validarPropio(Pedido pedido) {
        if (pedido.isPagaConCredito()
                && pedido.getTotal().compareTo(pedido.getCupoDisponible()) > 0) {
            return ResultadoValidacion.rechazado("Supera el cupo de credito disponible");
        }
        return ResultadoValidacion.aprobado();
    }
}
