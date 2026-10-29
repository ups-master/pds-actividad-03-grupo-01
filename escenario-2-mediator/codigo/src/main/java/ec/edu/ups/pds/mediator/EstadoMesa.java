package ec.edu.ups.pds.mediator;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EstadoMesa {

    private Map<String, Integer> pendientes = new HashMap<>();
    private Map<String, Integer> listos     = new HashMap<>();
    private Map<String, Integer> servidos   = new HashMap<>();
    private Map<String, Integer> retirados  = new HashMap<>();
    private boolean servida;

    public EstadoMesa(List<String> platos) {
        sumarPendientes(platos);
    }

    /** Copia independiente: el mediador la usa para poder deshacer un pedido que falla al repartirse. */
    public EstadoMesa(EstadoMesa otro) {
        pendientes = new HashMap<>(otro.pendientes);
        listos     = new HashMap<>(otro.listos);
        servidos   = new HashMap<>(otro.servidos);
        retirados  = new HashMap<>(otro.retirados);
        servida    = otro.servida;
    }

    /** Suma platos de un pedido posterior de la misma mesa y la vuelve a abrir. */
    public void agregarPlatos(List<String> platos) {
        sumarPendientes(platos);
        servida = false;
    }

    /** Devuelve false si el plato no estaba pendiente, en cuyo caso no cambia nada. */
    public boolean marcarListo(String plato) {
        return moverUno(pendientes, listos, plato);
    }

    public void marcarServidos() {
        listos.forEach((plato, cantidad) -> servidos.merge(plato, cantidad, Integer::sum));
        listos.clear();
        servida = true;
    }

    /**
     * Devuelve false si el plato no era parte del pedido, en cuyo caso no cambia nada. Si al retirarlo
     * no queda nada por resolver y la mesa ya habia servido platos, queda servida: asi un pedido
     * posterior que se agota por completo no la deja abierta.
     */
    public boolean retirar(String plato) {
        boolean retirado = moverUno(pendientes, retirados, plato) || moverUno(listos, retirados, plato);
        if (retirado && pendientes.isEmpty() && listos.isEmpty() && !servidos.isEmpty()) {
            servida = true;
        }
        return retirado;
    }

    /** Lista para servir: sin pendientes, con algo que servir y sin haberse servido ya. */
    public boolean estaLista() {
        return !servida && pendientes.isEmpty() && !listos.isEmpty();
    }

    public boolean estaServida() {
        return servida;
    }

    public Map<String, Integer> getPendientes() {
        return Collections.unmodifiableMap(pendientes);
    }

    public Map<String, Integer> getListos() {
        return Collections.unmodifiableMap(listos);
    }

    public Map<String, Integer> getServidos() {
        return Collections.unmodifiableMap(servidos);
    }

    public Map<String, Integer> getRetirados() {
        return Collections.unmodifiableMap(retirados);
    }

    private void sumarPendientes(List<String> platos) {
        for (String plato : platos) {
            pendientes.merge(plato, 1, Integer::sum);
        }
    }

    private boolean moverUno(Map<String, Integer> origen, Map<String, Integer> destino, String plato) {
        int cantidad = origen.getOrDefault(plato, 0);
        if (cantidad == 0) return false;
        if (cantidad == 1) {
            origen.remove(plato);
        } else {
            origen.put(plato, cantidad - 1);
        }
        destino.merge(plato, 1, Integer::sum);
        return true;
    }
}
