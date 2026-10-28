package ec.edu.ups.pds.mediator;

import java.util.ArrayList;
import java.util.List;

public class PedidoMesa {

    private String mesaId;
    private List<String> platos;

    public PedidoMesa(String mesaId, List<String> platos) {
        this.mesaId = mesaId;
        this.platos = new ArrayList<>(platos);
    }

    public String getMesaId() {
        return mesaId;
    }

    public List<String> getPlatos() {
        return platos;
    }
}
