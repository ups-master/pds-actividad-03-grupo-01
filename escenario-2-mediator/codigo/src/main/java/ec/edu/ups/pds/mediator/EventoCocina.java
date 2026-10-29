package ec.edu.ups.pds.mediator;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class EventoCocina {

    private TipoEvento tipo;
    private String mesaId;
    private String plato;
    private PedidoMesa pedido;
    private Map<String, Integer> consumo;

    public EventoCocina(TipoEvento tipo, String mesaId, String plato, PedidoMesa pedido) {
        this(tipo, mesaId, plato, pedido, Map.of());
    }

    public EventoCocina(TipoEvento tipo, String mesaId, String plato, PedidoMesa pedido,
                        Map<String, Integer> consumo) {
        this.tipo    = tipo;
        this.mesaId  = mesaId;
        this.plato   = plato;
        this.pedido  = pedido;
        this.consumo = Collections.unmodifiableMap(new LinkedHashMap<>(consumo));
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

    public static EventoCocina cuentaSolicitada(String mesaId) {
        return new EventoCocina(TipoEvento.CUENTA_SOLICITADA, mesaId, null, null);
    }

    public static EventoCocina mesaLista(String mesaId) {
        return new EventoCocina(TipoEvento.MESA_LISTA, mesaId, null, null);
    }

    public static EventoCocina retirarPlato(String mesaId, String plato) {
        return new EventoCocina(TipoEvento.RETIRAR_PLATO, mesaId, plato, null);
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

    public Map<String, Integer> getConsumo() {
        return consumo;
    }
}
