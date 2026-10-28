package ec.edu.ups.pds.mediator;

public class EstacionFria extends ComponenteCocina {

    public EstacionFria(MediadorCocina mediador) {
        super(mediador);
    }

    public void prepararPlato(String mesaId, String plato) {
        System.out.println("[EstacionFria] Plato listo: " + plato + " (mesa " + mesaId + ")");
        informar(EventoCocina.platoListo(mesaId, plato));
    }

    @Override
    public void recibir(EventoCocina evento) {
        if (evento.getTipo() == TipoEvento.PEDIDO_REGISTRADO) {
            System.out.println("[EstacionFria] Recibe pedido mesa " + evento.getMesaId()
                    + ": " + evento.getPedido().getPlatos());
        }
    }

    @Override
    public boolean atiende(TipoEvento tipo) {
        return tipo == TipoEvento.PEDIDO_REGISTRADO;
    }
}
