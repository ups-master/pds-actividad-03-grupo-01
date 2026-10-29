package ec.edu.ups.pds.mediator;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PedidoMesaTest {

    @Test
    void rechazaUnaMesaNulaOVacia() {
        assertThrows(IllegalArgumentException.class, () -> new PedidoMesa(null, List.of("lomo")));
        assertThrows(IllegalArgumentException.class, () -> new PedidoMesa(" ", List.of("lomo")));
    }

    @Test
    void rechazaUnaListaDePlatosNulaOVacia() {
        assertThrows(IllegalArgumentException.class, () -> new PedidoMesa("1", null));
        assertThrows(IllegalArgumentException.class, () -> new PedidoMesa("1", List.of()));
    }

    @Test
    void rechazaPlatosNulosOVacios() {
        assertThrows(IllegalArgumentException.class, () -> new PedidoMesa("1", Arrays.asList("lomo", null)));
        assertThrows(IllegalArgumentException.class, () -> new PedidoMesa("1", List.of("lomo", "")));
    }

    @Test
    void laListaDePlatosEsInmutable() {
        PedidoMesa pedido = new PedidoMesa("1", List.of("lomo"));

        assertThrows(UnsupportedOperationException.class, () -> pedido.getPlatos().add("fritada"));
    }

    @Test
    void guardaUnaCopiaDeLaListaOriginal() {
        List<String> original = new ArrayList<>(List.of("lomo"));
        PedidoMesa pedido = new PedidoMesa("1", original);

        original.add("fritada");

        assertEquals(List.of("lomo"), pedido.getPlatos());
    }
}
