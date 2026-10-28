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
                estados.put(pedido.getMesaId(), new EstadoMesa(pedido.getPlatos()));
                despachar(evento);
                break;
            }

            case PLATO_LISTO: {
                EstadoMesa estado = estados.get(evento.getMesaId());
                if (estado != null) {
                    estado.marcarListo(evento.getPlato());
                    if (mesaLista(evento.getMesaId())) {
                        despachar(new EventoCocina(TipoEvento.MESA_LISTA, evento.getMesaId(), null, null));
                    }
                }
                break;
            }

            case INSUMO_AGOTADO: {
                EstadoMesa estado = estados.get(evento.getMesaId());
                if (estado != null) {
                    estado.retirar(evento.getPlato());
                    despachar(new EventoCocina(TipoEvento.INSUMO_AGOTADO, evento.getMesaId(), evento.getPlato(), null));
                    despachar(new EventoCocina(TipoEvento.RETIRAR_PLATO,  evento.getMesaId(), evento.getPlato(), null));
                    if (mesaLista(evento.getMesaId())) {
                        despachar(new EventoCocina(TipoEvento.MESA_LISTA, evento.getMesaId(), null, null));
                    }
                }
                break;
            }

            case PLATOS_SERVIDOS: {
                EstadoMesa estado = estados.get(evento.getMesaId());
                if (estado != null) {
                    estado.marcarServidos();
                }
                despachar(new EventoCocina(TipoEvento.CUENTA_SOLICITADA, evento.getMesaId(), null, null));
                break;
            }

            default:
                despachar(evento);
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
