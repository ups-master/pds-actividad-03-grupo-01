package ec.edu.ups.pds.chain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas funcionales del patron Chain of Responsibility para la validacion de pedidos
 * de Andes Market. Cada prueba cubre un escenario de negocio independiente.
 */
class ProcesadorPedidosTest {

    // -----------------------------------------------------------------------
    // Stubs de infraestructura
    // -----------------------------------------------------------------------

    static class InventarioConStock implements Inventario {
        @Override public boolean hayStock(Pedido pedido) { return true; }
    }

    static class InventarioSinStock implements Inventario {
        @Override public boolean hayStock(Pedido pedido) { return false; }
    }

    static class AntifraudioLimpio implements ServicioAntifraude {
        @Override public boolean esSospechoso(Pedido pedido) { return false; }
    }

    static class AntifraudioSospechoso implements ServicioAntifraude {
        @Override public boolean esSospechoso(Pedido pedido) { return true; }
    }

    static class CoberturaTotal implements CoberturaEntrega {
        @Override public boolean atiende(String direccion) { return true; }
    }

    static class CoberturaNula implements CoberturaEntrega {
        @Override public boolean atiende(String direccion) { return false; }
    }

    static class CuponesValidos implements ServicioCupones {
        @Override public boolean esValido(Pedido pedido) { return true; }
    }

    static class CuponesInvalidos implements ServicioCupones {
        @Override public boolean esValido(Pedido pedido) { return false; }
    }

    // -----------------------------------------------------------------------
    // Cadena estandar (Stock -> Antifraude -> Credito -> Direccion)
    // -----------------------------------------------------------------------

    private ProcesadorPedidos procesadorEstandar(
            Inventario inv, ServicioAntifraude anti, CoberturaEntrega cob) {

        ValidadorStock stock         = new ValidadorStock(inv);
        ValidadorAntifraude fraude   = new ValidadorAntifraude(anti);
        ValidadorCredito credito     = new ValidadorCredito();
        ValidadorDireccion direccion = new ValidadorDireccion(cob);

        stock.setSiguiente(fraude).setSiguiente(credito).setSiguiente(direccion);
        return new ProcesadorPedidos(stock);
    }

    private Pedido pedidoBase() {
        return new Pedido.Builder()
                .productos(List.of(new LineaPedido("TECH-001", 1)))
                .total(new BigDecimal("200.00"))
                .direccion("Av. 10 de Agosto N22-40, Quito")
                .build();
    }

    // -----------------------------------------------------------------------
    // Escenario 1: pedido aprobado (pasa todas las validaciones)
    // -----------------------------------------------------------------------

    @Test
    void pedidoAprobadoCuandoTodasLasValidacionesPasan() {
        ProcesadorPedidos p = procesadorEstandar(
                new InventarioConStock(), new AntifraudioLimpio(), new CoberturaTotal());

        ResultadoValidacion r = p.procesar(pedidoBase());

        assertTrue(r.isAprobado(), "Pedido valido debe ser aprobado");
        assertNull(r.getMotivo(), "Pedido aprobado no tiene motivo de rechazo");
    }

    // -----------------------------------------------------------------------
    // Escenario 2: rechazado por falta de stock
    // -----------------------------------------------------------------------

    @Test
    void pedidoRechazadoCuandoNoHayStock() {
        ProcesadorPedidos p = procesadorEstandar(
                new InventarioSinStock(), new AntifraudioLimpio(), new CoberturaTotal());

        ResultadoValidacion r = p.procesar(pedidoBase());

        assertFalse(r.isAprobado());
        assertTrue(r.getMotivo().toLowerCase().contains("stock"),
                "El motivo debe mencionar el stock");
    }

    // -----------------------------------------------------------------------
    // Escenario 3: rechazado por antifraude
    // -----------------------------------------------------------------------

    @Test
    void pedidoRechazadoCuandoEsSospechoso() {
        ProcesadorPedidos p = procesadorEstandar(
                new InventarioConStock(), new AntifraudioSospechoso(), new CoberturaTotal());

        ResultadoValidacion r = p.procesar(pedidoBase());

        assertFalse(r.isAprobado());
        assertTrue(r.getMotivo().toLowerCase().contains("fraude"),
                "El motivo debe mencionar fraude");
    }

    // -----------------------------------------------------------------------
    // Escenario 4: rechazado por superar cupo de credito
    // -----------------------------------------------------------------------

    @Test
    void pedidoRechazadoCuandoSuperaCupoDeCredito() {
        Pedido pedidoCredito = new Pedido.Builder()
                .productos(List.of(new LineaPedido("LAPTOP-X", 2)))
                .total(new BigDecimal("3000.00"))
                .pagaConCredito(true)
                .cupoDisponible(new BigDecimal("500.00"))
                .direccion("Av. Amazonas, Quito")
                .build();

        ProcesadorPedidos p = procesadorEstandar(
                new InventarioConStock(), new AntifraudioLimpio(), new CoberturaTotal());

        ResultadoValidacion r = p.procesar(pedidoCredito);

        assertFalse(r.isAprobado());
        assertTrue(r.getMotivo().toLowerCase().contains("cupo"),
                "El motivo debe mencionar el cupo");
    }

    // -----------------------------------------------------------------------
    // Escenario 5: pedido con credito aprobado si total <= cupo
    // -----------------------------------------------------------------------

    @Test
    void pedidoCreditoAprobadoCuandoTotalDentroDeCupo() {
        Pedido pedidoCredito = new Pedido.Builder()
                .productos(List.of(new LineaPedido("TV-55", 1)))
                .total(new BigDecimal("400.00"))
                .pagaConCredito(true)
                .cupoDisponible(new BigDecimal("500.00"))
                .direccion("Av. Republica, Quito")
                .build();

        ProcesadorPedidos p = procesadorEstandar(
                new InventarioConStock(), new AntifraudioLimpio(), new CoberturaTotal());

        assertTrue(p.procesar(pedidoCredito).isAprobado());
    }

    // -----------------------------------------------------------------------
    // Escenario 6: rechazado por direccion sin cobertura
    // -----------------------------------------------------------------------

    @Test
    void pedidoRechazadoCuandoDireccionSinCobertura() {
        ProcesadorPedidos p = procesadorEstandar(
                new InventarioConStock(), new AntifraudioLimpio(), new CoberturaNula());

        ResultadoValidacion r = p.procesar(pedidoBase());

        assertFalse(r.isAprobado());
        assertTrue(r.getMotivo().toLowerCase().contains("cobertura"),
                "El motivo debe mencionar cobertura");
    }

    // -----------------------------------------------------------------------
    // Escenario 7: cliente corporativo salta la validacion antifraude
    // -----------------------------------------------------------------------

    @Test
    void clienteCorporativoSaltaValidacionAntifraude() {
        Pedido pedidoCorp = new Pedido.Builder()
                .productos(List.of(new LineaPedido("SERV-001", 50)))
                .total(new BigDecimal("25000.00"))
                .clienteCorporativo(true)
                .direccion("Parque Empresarial Quito Norte, Quito")
                .build();

        ProcesadorPedidos p = procesadorEstandar(
                new InventarioConStock(), new AntifraudioSospechoso(), new CoberturaTotal());

        assertTrue(p.procesar(pedidoCorp).isAprobado(),
                "Cliente corporativo debe ser aprobado incluso si el antifraude marca sospecha");
    }

    // -----------------------------------------------------------------------
    // Escenario 8: cadena se detiene en el primer fallo (no sigue evaluando)
    // -----------------------------------------------------------------------

    @Test
    void cadenaSeDtieneEnPrimerFallo() {
        boolean[] antifraudioConsultado = {false};
        ServicioAntifraude espiaAntifraude = pedido -> {
            antifraudioConsultado[0] = true;
            return false;
        };

        ProcesadorPedidos p = procesadorEstandar(
                new InventarioSinStock(), espiaAntifraude, new CoberturaTotal());

        ResultadoValidacion r = p.procesar(pedidoBase());

        assertFalse(r.isAprobado());
        assertFalse(antifraudioConsultado[0],
                "El servicio antifraude no debe consultarse si el stock falla primero");
    }

    // -----------------------------------------------------------------------
    // Escenario 9: ValidadorCupon como eslabon opcional (Black Friday)
    // -----------------------------------------------------------------------

    @Test
    void cuponValidoApruebaPedido() {
        ValidadorStock stock         = new ValidadorStock(new InventarioConStock());
        ValidadorCupon cupon         = new ValidadorCupon(new CuponesValidos());
        ValidadorDireccion direccion = new ValidadorDireccion(new CoberturaTotal());

        stock.setSiguiente(cupon).setSiguiente(direccion);
        ProcesadorPedidos p = new ProcesadorPedidos(stock);

        Pedido pedidoConCupon = new Pedido.Builder()
                .productos(List.of(new LineaPedido("PROD-BF", 3)))
                .total(new BigDecimal("120.00"))
                .cupon("BLACKFRIDAY2024")
                .direccion("Calle Olmedo 123, Guayaquil")
                .build();

        assertTrue(p.procesar(pedidoConCupon).isAprobado());
    }

    @Test
    void cuponInvalidoRechazaPedido() {
        ValidadorCupon cupon = new ValidadorCupon(new CuponesInvalidos());
        ProcesadorPedidos p  = new ProcesadorPedidos(cupon);

        Pedido pedidoCuponMalo = new Pedido.Builder()
                .productos(List.of(new LineaPedido("PROD-X", 1)))
                .total(new BigDecimal("50.00"))
                .cupon("CUPON-VENCIDO")
                .direccion("Av. 9 de Octubre, Guayaquil")
                .build();

        ResultadoValidacion r = p.procesar(pedidoCuponMalo);
        assertFalse(r.isAprobado());
        assertTrue(r.getMotivo().toLowerCase().contains("cupon"));
    }

    @Test
    void sinCodigoCuponValidadorCuponAprueba() {
        ValidadorCupon cupon = new ValidadorCupon(new CuponesInvalidos());
        ProcesadorPedidos p  = new ProcesadorPedidos(cupon);

        Pedido pedidoSinCupon = new Pedido.Builder()
                .productos(List.of(new LineaPedido("PROD-Y", 1)))
                .total(new BigDecimal("80.00"))
                .direccion("Av. Colon, Quito")
                .build();

        assertTrue(p.procesar(pedidoSinCupon).isAprobado(),
                "Sin codigo de cupon, ValidadorCupon debe dejar pasar el pedido");
    }

    // -----------------------------------------------------------------------
    // Escenario 10: setSiguiente detecta autoenlace y ciclos
    // -----------------------------------------------------------------------

    @Test
    void setSiguienteRechazaAutoenlace() {
        ValidadorStock stock = new ValidadorStock(new InventarioConStock());
        assertThrows(IllegalArgumentException.class, () -> stock.setSiguiente(stock),
                "setSiguiente debe rechazar que un validador sea su propio siguiente");
    }

    @Test
    void setSiguienteRechazaCiclo() {
        ValidadorStock   stock   = new ValidadorStock(new InventarioConStock());
        ValidadorCredito credito = new ValidadorCredito();

        stock.setSiguiente(credito);

        assertThrows(IllegalArgumentException.class, () -> credito.setSiguiente(stock),
                "setSiguiente debe detectar y rechazar ciclos en la cadena");
    }

    // -----------------------------------------------------------------------
    // Escenario 11: encadenamiento fluido con setSiguiente
    // -----------------------------------------------------------------------

    @Test
    void setSiguienteRetornaElSiguienteParaEncadenamientoFluido() {
        ValidadorStock   stock   = new ValidadorStock(new InventarioConStock());
        ValidadorCredito credito = new ValidadorCredito();

        ValidadorPedido retornado = stock.setSiguiente(credito);

        assertSame(credito, retornado,
                "setSiguiente debe retornar el validador recibido para permitir encadenamiento fluido");
    }

    // -----------------------------------------------------------------------
    // Escenario 12: reordenamiento sin modificar validadores
    // -----------------------------------------------------------------------

    @Test
    void ordenDireccionStockAntesDeCostosos() {
        ValidadorDireccion direccion = new ValidadorDireccion(new CoberturaNula());
        ValidadorStock     stock     = new ValidadorStock(new InventarioConStock());
        ValidadorAntifraude fraude   = new ValidadorAntifraude(new AntifraudioSospechoso());

        boolean[] fraudeConsultado = {false};
        ServicioAntifraude espiaFraude = p -> {
            fraudeConsultado[0] = true;
            return true;
        };
        ValidadorAntifraude frauConEspia = new ValidadorAntifraude(espiaFraude);

        direccion.setSiguiente(stock).setSiguiente(frauConEspia);
        ProcesadorPedidos p = new ProcesadorPedidos(direccion);

        ResultadoValidacion r = p.procesar(pedidoBase());

        assertFalse(r.isAprobado());
        assertFalse(fraudeConsultado[0],
                "Con direccion primero, antifraude no debe consultarse si la direccion falla");
    }
}
