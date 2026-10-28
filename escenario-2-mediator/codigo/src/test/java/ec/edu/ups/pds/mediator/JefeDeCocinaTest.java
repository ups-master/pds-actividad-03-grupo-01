package ec.edu.ups.pds.mediator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JefeDeCocinaTest {

    private JefeDeCocina jefe;
    private Mesero mesero;
    private EstacionCaliente caliente;
    private EstacionFria fria;
    private Caja caja;

    @BeforeEach
    void setUp() {
        jefe     = new JefeDeCocina();
        mesero   = new Mesero("Carlos", jefe);
        caliente = new EstacionCaliente(jefe);
        fria     = new EstacionFria(jefe);
        caja     = new Caja(jefe);

        jefe.registrar(mesero);
        jefe.registrar(caliente);
        jefe.registrar(fria);
        jefe.registrar(caja);
    }

    @Test
    void pedidoRegistrado_CreaEstadoConPlatosEnPendientes() {
        PedidoMesa pedido = new PedidoMesa("1", List.of("fritada", "ensalada"));
        mesero.registrarPedido(pedido);

        EstadoMesa estado = jefe.obtenerEstado("1");
        assertNotNull(estado, "Debe crearse el estado de la mesa");
        assertEquals(1, estado.getPendientes().get("fritada"));
        assertEquals(1, estado.getPendientes().get("ensalada"));
    }

    @Test
    void platoListo_MueveDesPendientesAListos() {
        // Dos platos: al marcar listo uno solo, la mesa no llega a MESA_LISTA todavia
        mesero.registrarPedido(new PedidoMesa("2", List.of("ceviche", "lomo")));
        fria.prepararPlato("2", "ceviche");

        EstadoMesa estado = jefe.obtenerEstado("2");
        assertFalse(estado.getPendientes().containsKey("ceviche"), "ceviche no debe estar pendiente");
        assertEquals(1, estado.getListos().get("ceviche"), "ceviche debe quedar en listos");
        assertTrue(estado.getPendientes().containsKey("lomo"), "lomo sigue pendiente");
    }

    @Test
    void todosLosPlatosListos_MesaListaYMeseroConfirmaYCajaEmiteCuenta() {
        mesero.registrarPedido(new PedidoMesa("3", List.of("lomo")));
        caliente.prepararPlato("3", "lomo");

        EstadoMesa estado = jefe.obtenerEstado("3");
        assertTrue(estado.estaLista(), "Mesa debe estar lista cuando todos los platos estan listos");
        assertEquals(1, estado.getServidos().get("lomo"), "El mesero confirma PLATOS_SERVIDOS al recibir MESA_LISTA");
    }

    @Test
    void insumoAgotado_RetiraElPlatoYMesaQuedaListaSiNoHayMasPendientes() {
        mesero.registrarPedido(new PedidoMesa("7", List.of("fritada", "ensalada")));
        fria.prepararPlato("7", "ensalada");
        caliente.reportarFaltante("7", "fritada");

        EstadoMesa estado = jefe.obtenerEstado("7");
        assertTrue(estado.getRetirados().containsKey("fritada"), "fritada debe quedar en retirados");
        assertFalse(estado.getPendientes().containsKey("fritada"), "fritada no debe estar en pendientes");
        assertEquals(1, estado.getServidos().get("ensalada"), "ensalada debe quedar servida tras MESA_LISTA");
    }

    @Test
    void insumoAgotado_ConPlatosAunPendientes_MesaNoQuedalista() {
        mesero.registrarPedido(new PedidoMesa("5", List.of("caldo", "fritada", "ensalada")));
        fria.prepararPlato("5", "ensalada");
        caliente.reportarFaltante("5", "fritada");

        EstadoMesa estado = jefe.obtenerEstado("5");
        assertFalse(estado.estaLista(), "Mesa no esta lista: aun falta el caldo pendiente");
        assertTrue(estado.getPendientes().containsKey("caldo"));
    }

    @Test
    void estadoMesa_PlatosDuplicados_SeContabilizanPorSeparado() {
        mesero.registrarPedido(new PedidoMesa("6", List.of("ceviche", "ceviche")));

        EstadoMesa estado = jefe.obtenerEstado("6");
        assertEquals(2, estado.getPendientes().get("ceviche"), "Dos ceviches deben quedar pendientes");

        fria.prepararPlato("6", "ceviche");
        assertEquals(1, estado.getPendientes().get("ceviche"), "Queda un ceviche pendiente");
        assertEquals(1, estado.getListos().get("ceviche"));

        fria.prepararPlato("6", "ceviche");
        assertTrue(estado.estaLista(), "Mesa lista cuando ambos ceviches estan listos");
    }

    @Test
    void colegas_NoSeReferencianEntreEllos() {
        for (java.lang.reflect.Field campo : mesero.getClass().getDeclaredFields()) {
            Class<?> tipo = campo.getType();
            assertFalse(tipo == EstacionCaliente.class || tipo == EstacionFria.class || tipo == Caja.class,
                    "Mesero no debe referenciar a otros colegas concretos");
        }
        for (java.lang.reflect.Field campo : caliente.getClass().getDeclaredFields()) {
            Class<?> tipo = campo.getType();
            assertFalse(tipo == Mesero.class || tipo == EstacionFria.class || tipo == Caja.class,
                    "EstacionCaliente no debe referenciar a otros colegas concretos");
        }
    }

    @Test
    void reentrada_MeseroEmitePlatosServidosDuranteMesaLista_NoLanzaExcepcion() {
        assertDoesNotThrow(() -> {
            mesero.registrarPedido(new PedidoMesa("9", List.of("lomo")));
            caliente.prepararPlato("9", "lomo");
        }, "La reentrada durante MESA_LISTA no debe lanzar ConcurrentModificationException");
    }
}
