package ec.edu.ups.pds.mediator;

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

    /** Devuelve false si el plato no era parte del pedido, en cuyo caso no cambia nada. */
    public boolean retirar(String plato) {
        return moverUno(pendientes, retirados, plato) || moverUno(listos, retirados, plato);
    }

    /** Lista para servir: sin pendientes, con algo que servir y sin haberse servido ya. */
    public boolean estaLista() {
        return !servida && pendientes.isEmpty() && !listos.isEmpty();
    }

    public boolean estaServida() {
        return servida;
    }

    public Map<String, Integer> getPendientes() {
        return pendientes;
    }

    public Map<String, Integer> getListos() {
        return listos;
    }

    public Map<String, Integer> getServidos() {
        return servidos;
    }

    public Map<String, Integer> getRetirados() {
        return retirados;
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
