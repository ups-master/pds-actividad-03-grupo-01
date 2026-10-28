package ec.edu.ups.pds.mediator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EstadoMesa {

    private Map<String, Integer> pendientes = new HashMap<>();
    private Map<String, Integer> listos     = new HashMap<>();
    private Map<String, Integer> servidos   = new HashMap<>();
    private Map<String, Integer> retirados  = new HashMap<>();

    public EstadoMesa(List<String> platos) {
        for (String plato : platos) {
            pendientes.merge(plato, 1, Integer::sum);
        }
    }

    public void marcarListo(String plato) {
        moverUno(pendientes, listos, plato);
    }

    public void marcarServidos() {
        listos.forEach((plato, cantidad) -> servidos.merge(plato, cantidad, Integer::sum));
        listos.clear();
    }

    public void retirar(String plato) {
        if (!moverUno(pendientes, retirados, plato)) {
            moverUno(listos, retirados, plato);
        }
    }

    public boolean estaLista() {
        return pendientes.isEmpty();
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
