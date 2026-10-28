package ec.edu.ups.pds.chain;

public class ValidadorCupon extends ValidadorPedido {

    private final ServicioCupones cupones;

    public ValidadorCupon(ServicioCupones cupones) {
        this.cupones = cupones;
    }

    @Override
    protected ResultadoValidacion validarPropio(Pedido pedido) {
        String codigoCupon = pedido.getCupon();
        if (codigoCupon != null && !codigoCupon.isBlank() && !cupones.esValido(pedido)) {
            return ResultadoValidacion.rechazado("Cupon invalido, expirado o no aplica al pedido");
        }
        return ResultadoValidacion.aprobado();
    }
}
