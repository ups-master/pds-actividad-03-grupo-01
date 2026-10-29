package ec.edu.ups.pds.mediator;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EstadoMesaTest {

    @Test
    void losGettersNoPermitenModificarElEstadoInterno() {
        EstadoMesa estado = new EstadoMesa(List.of("lomo"));

        assertThrows(UnsupportedOperationException.class, () -> estado.getPendientes().put("x", 0));
        assertThrows(UnsupportedOperationException.class, () -> estado.getListos().put("x", 1));
        assertThrows(UnsupportedOperationException.class, () -> estado.getServidos().put("x", 1));
        assertThrows(UnsupportedOperationException.class, () -> estado.getRetirados().put("x", 1));
        assertEquals(Map.of("lomo", 1), estado.getPendientes());
    }

    @Test
    void laCopiaEsIndependienteDelOriginal() {
        EstadoMesa original = new EstadoMesa(List.of("lomo"));
        EstadoMesa copia = new EstadoMesa(original);

        copia.marcarListo("lomo");

        assertEquals(Map.of("lomo", 1), original.getPendientes());
        assertTrue(original.getListos().isEmpty());
        assertEquals(Map.of("lomo", 1), copia.getListos());
    }

    @Test
    void retirarTodoLoNuevoDeUnaMesaYaServidaLaDejaServida() {
        EstadoMesa estado = new EstadoMesa(List.of("lomo"));
        estado.marcarListo("lomo");
        estado.marcarServidos();
        estado.agregarPlatos(List.of("fritada"));
        assertFalse(estado.estaServida(), "El pedido nuevo reabre la mesa");

        assertTrue(estado.retirar("fritada"));

        assertTrue(estado.estaServida());
        assertFalse(estado.estaLista(), "No hay nada nuevo por servir");
    }

    @Test
    void retirarTodoSinHaberServidoNadaNoMarcaLaMesaComoServida() {
        EstadoMesa estado = new EstadoMesa(List.of("lomo"));

        assertTrue(estado.retirar("lomo"));

        assertFalse(estado.estaServida());
        assertFalse(estado.estaLista());
    }

    @Test
    void retirarUnPlatoAjenoAlPedidoNoCambiaNada() {
        EstadoMesa estado = new EstadoMesa(List.of("lomo"));

        assertFalse(estado.retirar("fritada"));

        assertEquals(Map.of("lomo", 1), estado.getPendientes());
        assertTrue(estado.getRetirados().isEmpty());
    }
}
