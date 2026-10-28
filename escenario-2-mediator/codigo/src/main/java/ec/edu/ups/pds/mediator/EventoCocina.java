package ec.edu.ups.pds.mediator;

public class EventoCocina {

    private TipoEvento tipo;
    private String mesaId;
    private String plato;
    private PedidoMesa pedido;

    public EventoCocina(TipoEvento tipo, String mesaId, String plato, PedidoMesa pedido) {
        this.tipo   = tipo;
        this.mesaId = mesaId;
        this.plato  = plato;
        this.pedido = pedido;
    }

    public static EventoCocina pedidoRegistrado(PedidoMesa pedido) {
        return new EventoCocina(TipoEvento.PEDIDO_REGISTRADO, pedido.getMesaId(), null, pedido);
    }

    public static EventoCocina platoListo(String mesaId, String plato) {
        return new EventoCocina(TipoEvento.PLATO_LISTO, mesaId, plato, null);
    }

    public static EventoCocina insumoAgotado(String mesaId, String plato) {
        return new EventoCocina(TipoEvento.INSUMO_AGOTADO, mesaId, plato, null);
    }

    public static EventoCocina platosServidos(String mesaId) {
        return new EventoCocina(TipoEvento.PLATOS_SERVIDOS, mesaId, null, null);
    }

    public TipoEvento getTipo() {
        return tipo;
    }

    public String getMesaId() {
        return mesaId;
    }

    public String getPlato() {
        return plato;
    }

    public PedidoMesa getPedido() {
        return pedido;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[").append(tipo).append("|mesa=").append(mesaId);
        if (plato  != null) sb.append("|plato=").append(plato);
        if (pedido != null) sb.append("|pedido=").append(pedido.getMesaId());
        return sb.append("]").toString();
    }
}
