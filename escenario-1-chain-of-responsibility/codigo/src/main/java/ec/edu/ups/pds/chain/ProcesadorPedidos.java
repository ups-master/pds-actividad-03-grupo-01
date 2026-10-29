package ec.edu.ups.pds.chain;

public class ProcesadorPedidos {

    private final ValidadorPedido primerValidador;

    public ProcesadorPedidos(ValidadorPedido primerValidador) {
        if (primerValidador == null) {
            throw new IllegalArgumentException("El primer validador es obligatorio");
        }
        this.primerValidador = primerValidador;
    }

    public ResultadoValidacion procesar(Pedido pedido) {
        if (pedido == null) {
            throw new IllegalArgumentException("El pedido es obligatorio");
        }
        return primerValidador.validar(pedido);
    }
}
