package ec.edu.ups.pds.mediator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JefeDeCocina implements MediadorCocina {

    private List<ComponenteCocina> componentes = new ArrayList<>();
    private Map<String, EstadoMesa> estados    = new HashMap<>();

    @Override
    public void registrar(ComponenteCocina componente) {
        componentes.add(componente);
    }

    @Override
    public void notificar(ComponenteCocina emisor, EventoCocina evento) {
        switch (evento.getTipo()) {

            case PEDIDO_REGISTRADO: {
                PedidoMesa pedido = evento.getPedido();
                validarPlatos(pedido);
                EstadoMesa existente = estados.get(pedido.getMesaId());
                if (existente == null) {
                    estados.put(pedido.getMesaId(), new EstadoMesa(pedido.getPlatos()));
                } else {
                    existente.agregarPlatos(pedido.getPlatos());
                }
                repartirPedido(pedido);
                break;
            }

            case PLATO_LISTO: {
                EstadoMesa estado = estados.get(evento.getMesaId());
                if (estado != null && estado.marcarListo(evento.getPlato())
                        && mesaLista(evento.getMesaId())) {
                    despachar(EventoCocina.mesaLista(evento.getMesaId()));
                }
                break;
            }

            case INSUMO_AGOTADO: {
                EstadoMesa estado = estados.get(evento.getMesaId());
                if (estado != null && estado.retirar(evento.getPlato())) {
                    despachar(evento);
                    despachar(EventoCocina.retirarPlato(evento.getMesaId(), evento.getPlato()));
                    if (mesaLista(evento.getMesaId())) {
                        despachar(EventoCocina.mesaLista(evento.getMesaId()));
                    }
                }
                break;
            }

            case PLATOS_SERVIDOS: {
                EstadoMesa estado = estados.get(evento.getMesaId());
                if (estado != null) {
                    estado.marcarServidos();
                }
                break;
            }

            case CUENTA_SOLICITADA: {
                EstadoMesa estado = estados.get(evento.getMesaId());
                if (estado != null) {
                    despachar(new EventoCocina(TipoEvento.CUENTA_SOLICITADA, evento.getMesaId(),
                            null, null, estado.getServidos()));
                }
                break;
            }

            default:
                despachar(evento);
        }
    }

    /** Rechaza el pedido completo si algun plato no lo prepara ningun componente registrado. */
    private void validarPlatos(PedidoMesa pedido) {
        for (String plato : pedido.getPlatos()) {
            boolean hayQuienLoPrepare = componentes.stream().anyMatch(c -> c.prepara(plato));
            if (!hayQuienLoPrepare) {
                throw new IllegalArgumentException("Ninguna estacion prepara el plato: " + plato);
            }
        }
    }

    /** Entrega a cada estacion un pedido con solo los platos que ella prepara. */
    private void repartirPedido(PedidoMesa pedido) {
        List<ComponenteCocina> copia = new ArrayList<>(componentes);
        for (ComponenteCocina componente : copia) {
            if (!componente.atiende(TipoEvento.PEDIDO_REGISTRADO)) continue;
            List<String> propios = pedido.getPlatos().stream().filter(componente::prepara).toList();
            if (!propios.isEmpty()) {
                componente.recibir(EventoCocina.pedidoRegistrado(new PedidoMesa(pedido.getMesaId(), propios)));
            }
        }
    }

    private void despachar(EventoCocina evento) {
        List<ComponenteCocina> copia = new ArrayList<>(componentes);
        for (ComponenteCocina componente : copia) {
            if (componente.atiende(evento.getTipo())) {
                componente.recibir(evento);
            }
        }
    }

    private boolean mesaLista(String mesaId) {
        EstadoMesa estado = estados.get(mesaId);
        return estado != null && estado.estaLista();
    }

    public EstadoMesa obtenerEstado(String mesaId) {
        return estados.get(mesaId);
    }
}
