package ec.edu.ups.pds.chain;

public class ProcesadorPedidos {

    private final ValidadorPedido primerValidador;

    public ProcesadorPedidos(ValidadorPedido primerValidador) {
        this.primerValidador = primerValidador;
    }

    public ResultadoValidacion procesar(Pedido pedido) {
        return primerValidador.validar(pedido);
    }
}
