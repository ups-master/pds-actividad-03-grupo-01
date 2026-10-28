package ec.edu.ups.pds.chain;

public interface ServicioAntifraude {
    boolean esSospechoso(Pedido pedido);
}
