package ec.edu.ups.pds.mediator;

import java.util.List;

public class PedidoMesa {

    private final String mesaId;
    private final List<String> platos;

    public PedidoMesa(String mesaId, List<String> platos) {
        if (mesaId == null || mesaId.isBlank()) {
            throw new IllegalArgumentException("mesaId no puede ser nulo ni vacio");
        }
        if (platos == null || platos.isEmpty()) {
            throw new IllegalArgumentException("El pedido debe tener al menos un plato");
        }
        for (String plato : platos) {
            if (plato == null || plato.isBlank()) {
                throw new IllegalArgumentException("Los platos no pueden ser nulos ni vacios");
            }
        }
        this.mesaId = mesaId;
        this.platos = List.copyOf(platos);
    }

    public String getMesaId() {
        return mesaId;
    }

    public List<String> getPlatos() {
        return platos;
    }
}
