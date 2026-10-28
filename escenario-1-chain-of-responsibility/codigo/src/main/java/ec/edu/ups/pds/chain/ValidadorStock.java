package ec.edu.ups.pds.chain;

public class ValidadorStock extends ValidadorPedido {

    private final Inventario inventario;

    public ValidadorStock(Inventario inventario) {
        this.inventario = inventario;
    }

    @Override
    protected ResultadoValidacion validarPropio(Pedido pedido) {
        if (!inventario.hayStock(pedido)) {
            return ResultadoValidacion.rechazado("Sin stock disponible para el pedido");
        }
        return ResultadoValidacion.aprobado();
    }
}
