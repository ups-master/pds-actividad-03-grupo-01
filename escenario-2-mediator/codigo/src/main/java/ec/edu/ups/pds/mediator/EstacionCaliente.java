package ec.edu.ups.pds.mediator;

import java.util.Set;

public class EstacionCaliente extends ComponenteCocina {

    private final Set<String> menu;

    public EstacionCaliente(MediadorCocina mediador, Set<String> menu) {
        super(mediador);
        this.menu = Set.copyOf(menu);
    }

    public void reportarFaltante(String mesaId, String plato) {
        System.out.println("[EstacionCaliente] Faltante de insumo: " + plato + " (mesa " + mesaId + ")");
        informar(EventoCocina.insumoAgotado(mesaId, plato));
    }

    public void prepararPlato(String mesaId, String plato) {
        System.out.println("[EstacionCaliente] Plato listo: " + plato + " (mesa " + mesaId + ")");
        informar(EventoCocina.platoListo(mesaId, plato));
    }

    @Override
    public void recibir(EventoCocina evento) {
        if (evento.getTipo() == TipoEvento.PEDIDO_REGISTRADO) {
            System.out.println("[EstacionCaliente] Recibe pedido mesa " + evento.getMesaId()
                    + ": " + evento.getPedido().getPlatos());
        }
    }

    @Override
    public boolean atiende(TipoEvento tipo) {
        return tipo == TipoEvento.PEDIDO_REGISTRADO;
    }

    @Override
    public boolean prepara(String plato) {
        return menu.contains(plato);
    }
}
