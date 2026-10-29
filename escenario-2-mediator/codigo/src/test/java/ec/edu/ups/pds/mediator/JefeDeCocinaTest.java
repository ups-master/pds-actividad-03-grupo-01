package ec.edu.ups.pds.mediator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JefeDeCocinaTest {

    /** Colega de prueba: registra los eventos que recibe y declara el menu que prepara. */
    static class Registro extends ComponenteCocina {
        private final Set<String> menu;
        private final Set<TipoEvento> tipos;
        final List<EventoCocina> recibidos = new ArrayList<>();

        Registro(MediadorCocina mediador, Set<String> menu, TipoEvento... tipos) {
            super(mediador);
            this.menu = menu;
            this.tipos = Set.of(tipos);
        }

        @Override public void recibir(EventoCocina evento) { recibidos.add(evento); }
        @Override public boolean atiende(TipoEvento tipo) { return tipos.contains(tipo); }
        @Override public boolean prepara(String plato)    { return menu.contains(plato); }

        long contar(TipoEvento tipo) {
            return recibidos.stream().filter(e -> e.getTipo() == tipo).count();
        }
    }

    /** Estacion de parrillada que falla al recibir su parte de un pedido. */
    static class EstacionRota extends ComponenteCocina {
        EstacionRota(MediadorCocina mediador) { super(mediador); }

        @Override public void recibir(EventoCocina evento) {
            throw new IllegalStateException("estacion fuera de servicio");
        }
        @Override public boolean atiende(TipoEvento tipo) { return tipo == TipoEvento.PEDIDO_REGISTRADO; }
        @Override public boolean prepara(String plato)    { return plato.equals("parrillada"); }
    }

    private JefeDeCocina jefe;
    private Mesero mesero;
    private EstacionCaliente caliente;
    private EstacionFria fria;
    private Caja caja;
    private Registro oyente;

    @BeforeEach
    void setUp() {
        jefe     = new JefeDeCocina();
        mesero   = new Mesero("Carlos", jefe);
        caliente = new EstacionCaliente(jefe, Set.of("fritada", "lomo", "caldo", "seco de pollo"));
        fria     = new EstacionFria(jefe, Set.of("ceviche", "ensalada", "limonada"));
        caja     = new Caja(jefe);
        oyente   = new Registro(jefe, Set.of(),
                TipoEvento.MESA_LISTA, TipoEvento.INSUMO_AGOTADO, TipoEvento.RETIRAR_PLATO);

        jefe.registrar(mesero);
        jefe.registrar(caliente);
        jefe.registrar(fria);
        jefe.registrar(caja);
        jefe.registrar(oyente);
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
    void platoListo_MueveDePendientesAListos() {
        // Dos platos: al marcar listo uno solo, la mesa no llega a MESA_LISTA todavia
        mesero.registrarPedido(new PedidoMesa("2", List.of("ceviche", "lomo")));
        fria.prepararPlato("2", "ceviche");

        EstadoMesa estado = jefe.obtenerEstado("2");
        assertFalse(estado.getPendientes().containsKey("ceviche"), "ceviche no debe estar pendiente");
        assertEquals(1, estado.getListos().get("ceviche"), "ceviche debe quedar en listos");
        assertTrue(estado.getPendientes().containsKey("lomo"), "lomo sigue pendiente");
        assertEquals(0, oyente.contar(TipoEvento.MESA_LISTA));
    }

    @Test
    void todosLosPlatosListos_MesaSeSirveYElMeseroConfirma() {
        mesero.registrarPedido(new PedidoMesa("3", List.of("lomo")));
        caliente.prepararPlato("3", "lomo");

        EstadoMesa estado = jefe.obtenerEstado("3");
        assertTrue(estado.estaServida(), "El mesero confirma PLATOS_SERVIDOS al recibir MESA_LISTA");
        assertEquals(1, estado.getServidos().get("lomo"));
        assertTrue(estado.getListos().isEmpty(), "Lo servido sale de listos");
        assertEquals(1, oyente.contar(TipoEvento.MESA_LISTA));
    }

    @Test
    void insumoAgotado_RetiraElPlatoYSirveLoQueYaEstabaListo() {
        mesero.registrarPedido(new PedidoMesa("7", List.of("fritada", "ensalada")));
        fria.prepararPlato("7", "ensalada");
        caliente.reportarFaltante("7", "fritada");

        EstadoMesa estado = jefe.obtenerEstado("7");
        assertTrue(estado.getRetirados().containsKey("fritada"), "fritada debe quedar en retirados");
        assertFalse(estado.getPendientes().containsKey("fritada"), "fritada no debe estar en pendientes");
        assertEquals(1, estado.getServidos().get("ensalada"), "ensalada debe quedar servida tras MESA_LISTA");
        assertEquals(1, oyente.contar(TipoEvento.INSUMO_AGOTADO), "El mesero debe enterarse del faltante");
        assertEquals(1, oyente.contar(TipoEvento.RETIRAR_PLATO), "La caja debe retirar el plato");
    }

    @Test
    void insumoAgotado_ConPlatosAunPendientes_MesaNoQuedaLista() {
        mesero.registrarPedido(new PedidoMesa("5", List.of("caldo", "fritada", "ensalada")));
        fria.prepararPlato("5", "ensalada");
        caliente.reportarFaltante("5", "fritada");

        EstadoMesa estado = jefe.obtenerEstado("5");
        assertFalse(estado.estaLista(), "Mesa no esta lista: aun falta el caldo pendiente");
        assertTrue(estado.getPendientes().containsKey("caldo"));
        assertEquals(0, oyente.contar(TipoEvento.MESA_LISTA));
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
        assertEquals(2, estado.getServidos().get("ceviche"), "Con ambos ceviches listos se sirve la mesa");
    }

    @Test
    void reentrada_ColegaQueSeRegistraDuranteElReparto_NoAlteraLaIteracion() {
        boolean[] avisado = {false};
        jefe.registrar(new ComponenteCocina(jefe) {
            @Override public void recibir(EventoCocina evento) {
                avisado[0] = true;
                jefe.registrar(new Registro(jefe, Set.of()));
            }
            @Override public boolean atiende(TipoEvento tipo) { return tipo == TipoEvento.MESA_LISTA; }
        });

        assertDoesNotThrow(() -> {
            mesero.registrarPedido(new PedidoMesa("9", List.of("lomo")));
            caliente.prepararPlato("9", "lomo");
        }, "Registrar un colega durante MESA_LISTA no debe lanzar ConcurrentModificationException");

        assertTrue(avisado[0]);
        assertTrue(jefe.obtenerEstado("9").estaServida(),
                "El mesero responde con PLATOS_SERVIDOS dentro del mismo reparto");
    }

    @Test
    void reparto_CadaEstacionRecibeSoloLosPlatosQueLeCorresponden() {
        JefeDeCocina otro = new JefeDeCocina();
        Registro estacionCaliente = new Registro(otro, Set.of("lomo"), TipoEvento.PEDIDO_REGISTRADO);
        Registro estacionFria     = new Registro(otro, Set.of("ceviche"), TipoEvento.PEDIDO_REGISTRADO);
        otro.registrar(estacionCaliente);
        otro.registrar(estacionFria);

        otro.notificar(estacionCaliente,
                EventoCocina.pedidoRegistrado(new PedidoMesa("4", List.of("ceviche", "lomo", "ceviche"))));

        assertEquals(List.of("lomo"), estacionCaliente.recibidos.get(0).getPedido().getPlatos());
        assertEquals(List.of("ceviche", "ceviche"), estacionFria.recibidos.get(0).getPedido().getPlatos());
        assertEquals("4", estacionFria.recibidos.get(0).getMesaId());
    }

    @Test
    void estacionSinPlatosEnElPedido_NoRecibeNada() {
        Registro parrilla = new Registro(jefe, Set.of("parrillada"), TipoEvento.PEDIDO_REGISTRADO);
        jefe.registrar(parrilla);

        mesero.registrarPedido(new PedidoMesa("10", List.of("lomo")));

        assertTrue(parrilla.recibidos.isEmpty(), "La parrilla no prepara el lomo y no debe enterarse");
    }

    @Test
    void platoSinEstacion_RechazaElPedidoSinCrearEstado() {
        PedidoMesa pedido = new PedidoMesa("11", List.of("lomo", "hamburguesa"));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> mesero.registrarPedido(pedido));

        assertTrue(ex.getMessage().contains("hamburguesa"));
        assertNull(jefe.obtenerEstado("11"), "Un pedido rechazado no debe dejar estado de mesa");
    }

    @Test
    void cuenta_ContieneSoloLoServido() {
        mesero.registrarPedido(new PedidoMesa("7", List.of("fritada", "ensalada")));
        fria.prepararPlato("7", "ensalada");
        caliente.reportarFaltante("7", "fritada");
        mesero.solicitarCuenta("7");

        assertEquals(Map.of("ensalada", 1), caja.obtenerCuenta("7"),
                "La fritada retirada no debe aparecer en la cuenta");
    }

    @Test
    void cuentaAntesDeServir_EstaVacia() {
        mesero.registrarPedido(new PedidoMesa("13", List.of("lomo")));
        mesero.solicitarCuenta("13");

        assertTrue(caja.obtenerCuenta("13").isEmpty(), "Lo pendiente no se cobra");
    }

    @Test
    void mesaServida_NoSeVuelveAServirNiRepiteElAviso() {
        mesero.registrarPedido(new PedidoMesa("3", List.of("lomo")));
        caliente.prepararPlato("3", "lomo");
        caliente.prepararPlato("3", "lomo");

        assertEquals(1, oyente.contar(TipoEvento.MESA_LISTA), "MESA_LISTA debe repartirse una sola vez");
        assertEquals(1, jefe.obtenerEstado("3").getServidos().get("lomo"));
    }

    @Test
    void faltanteDeTodosLosPlatos_NoSirveUnaMesaVacia() {
        mesero.registrarPedido(new PedidoMesa("4", List.of("fritada")));
        caliente.reportarFaltante("4", "fritada");

        EstadoMesa estado = jefe.obtenerEstado("4");
        assertEquals(1, estado.getRetirados().get("fritada"));
        assertFalse(estado.estaServida());
        assertEquals(0, oyente.contar(TipoEvento.MESA_LISTA), "No hay nada que servir");
    }

    @Test
    void faltanteDeUnPlatoAjenoAlPedido_SeIgnora() {
        mesero.registrarPedido(new PedidoMesa("14", List.of("ensalada")));
        caliente.reportarFaltante("14", "lomo");

        assertTrue(jefe.obtenerEstado("14").getRetirados().isEmpty());
        assertEquals(0, oyente.contar(TipoEvento.INSUMO_AGOTADO), "Nadie debe enterarse de un faltante ajeno");
    }

    @Test
    void segundoPedidoDeLaMismaMesa_ReabreLaMesa() {
        mesero.registrarPedido(new PedidoMesa("8", List.of("lomo")));
        caliente.prepararPlato("8", "lomo");
        assertTrue(jefe.obtenerEstado("8").estaServida());

        mesero.registrarPedido(new PedidoMesa("8", List.of("ceviche")));
        EstadoMesa estado = jefe.obtenerEstado("8");
        assertFalse(estado.estaServida(), "El pedido nuevo reabre la mesa");
        assertEquals(1, estado.getServidos().get("lomo"), "Lo ya servido se conserva");

        fria.prepararPlato("8", "ceviche");
        assertEquals(1, estado.getServidos().get("ceviche"));
        assertEquals(2, oyente.contar(TipoEvento.MESA_LISTA));
    }

    @Test
    void platoListoDeMesaDesconocida_SeIgnora() {
        assertDoesNotThrow(() -> caliente.prepararPlato("99", "lomo"));
        assertNull(jefe.obtenerEstado("99"));
    }

    @Test
    void pedidoQueFallaAlRepartirse_NoDejaEstadoDeMesa() {
        jefe.registrar(new EstacionRota(jefe));

        assertThrows(IllegalStateException.class,
                () -> mesero.registrarPedido(new PedidoMesa("20", List.of("lomo", "parrillada"))));

        assertNull(jefe.obtenerEstado("20"), "Un pedido que falla no debe dejar platos sin dueno");
    }

    @Test
    void segundoPedidoQueFalla_ConservaElEstadoAnterior() {
        mesero.registrarPedido(new PedidoMesa("21", List.of("lomo")));
        EstadoMesa antes = jefe.obtenerEstado("21");
        jefe.registrar(new EstacionRota(jefe));

        assertThrows(IllegalStateException.class,
                () -> mesero.registrarPedido(new PedidoMesa("21", List.of("parrillada"))));

        assertSame(antes, jefe.obtenerEstado("21"), "Debe restaurarse el estado previo");
        assertEquals(Map.of("lomo", 1), jefe.obtenerEstado("21").getPendientes());
    }

    @Test
    void segundoPedidoTotalmenteRetirado_CierraLaMesaSinRepetirElAviso() {
        mesero.registrarPedido(new PedidoMesa("22", List.of("lomo")));
        caliente.prepararPlato("22", "lomo");
        mesero.registrarPedido(new PedidoMesa("22", List.of("fritada")));
        caliente.reportarFaltante("22", "fritada");

        EstadoMesa estado = jefe.obtenerEstado("22");
        assertTrue(estado.estaServida(), "No queda nada por resolver: la mesa no puede quedar abierta");
        assertEquals(Map.of("lomo", 1), estado.getServidos(), "Lo ya servido se conserva");
        assertEquals(Map.of("fritada", 1), estado.getRetirados());
        assertEquals(1, oyente.contar(TipoEvento.MESA_LISTA), "No hay nada nuevo que servir");
    }

    @Test
    void cuentaSePuedeVolverAPedir_YReflejaLoServidoHastaEseMomento() {
        mesero.registrarPedido(new PedidoMesa("23", List.of("lomo", "ceviche")));
        caliente.prepararPlato("23", "lomo");
        mesero.solicitarCuenta("23");
        assertTrue(caja.obtenerCuenta("23").isEmpty(), "El lomo esta listo pero aun no se sirvio");

        fria.prepararPlato("23", "ceviche");
        mesero.solicitarCuenta("23");

        assertEquals(Map.of("lomo", 1, "ceviche", 1), caja.obtenerCuenta("23"),
                "La segunda solicitud reemplaza la cuenta con lo servido");
    }
}
