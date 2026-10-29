package ec.edu.ups.pds.mediator;

public class Mesero extends ComponenteCocina {

    private final String nombre;

    public Mesero(String nombre, MediadorCocina mediador) {
        super(mediador);
        this.nombre = nombre;
    }

    public void registrarPedido(PedidoMesa pedido) {
        System.out.println("[" + nombre + "] Pedido registrado para mesa " + pedido.getMesaId()
                + ": " + pedido.getPlatos());
        informar(EventoCocina.pedidoRegistrado(pedido));
    }

    public void solicitarCuenta(String mesaId) {
        System.out.println("[" + nombre + "] Solicitando la cuenta de mesa " + mesaId);
        informar(EventoCocina.cuentaSolicitada(mesaId));
    }

    @Override
    public void recibir(EventoCocina evento) {
        switch (evento.getTipo()) {
            case INSUMO_AGOTADO:
                System.out.println("[" + nombre + "] Informando al cliente mesa " + evento.getMesaId()
                        + ": faltante de " + evento.getPlato());
                break;
            case MESA_LISTA:
                System.out.println("[" + nombre + "] Mesa " + evento.getMesaId()
                        + " lista. Sirviendo platos y confirmando.");
                informar(EventoCocina.platosServidos(evento.getMesaId()));
                break;
            default:
                break;
        }
    }

    @Override
    public boolean atiende(TipoEvento tipo) {
        return tipo == TipoEvento.INSUMO_AGOTADO || tipo == TipoEvento.MESA_LISTA;
    }
}
