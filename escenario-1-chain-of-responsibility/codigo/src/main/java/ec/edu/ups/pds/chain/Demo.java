package ec.edu.ups.pds.chain;

import java.math.BigDecimal;
import java.util.List;

/**
 * Punto de entrada para demostrar el patron Chain of Responsibility
 * en el proceso de validacion de pedidos de Andes Market.
 *
 * Ejecucion:
 *   mvn exec:java -pl escenario-1-chain-of-responsibility/codigo
 */
public class Demo {

    // -----------------------------------------------------------------------
    // Stubs con trazabilidad: imprimen cada decision para ver la cadena viva
    // -----------------------------------------------------------------------

    static class InventarioSimulado implements Inventario {
        private final boolean hayStock;
        InventarioSimulado(boolean hayStock) { this.hayStock = hayStock; }

        @Override
        public boolean hayStock(Pedido pedido) {
            String estado = hayStock ? "OK  - hay existencias" : "FALLA - sin existencias";
            System.out.printf("    %-14s %s%n", "[Stock]", estado);
            return hayStock;
        }
    }

    static class AntifraudioSimulado implements ServicioAntifraude {
        private final boolean sospechoso;
        AntifraudioSimulado(boolean sospechoso) { this.sospechoso = sospechoso; }

        @Override
        public boolean esSospechoso(Pedido pedido) {
            String estado = sospechoso ? "FALLA - patron sospechoso detectado" : "OK  - sin senales de fraude";
            System.out.printf("    %-14s %s%n", "[Antifraude]", estado);
            return sospechoso;
        }
    }

    static class CoberturaSimulada implements CoberturaEntrega {
        private final boolean cubre;
        CoberturaSimulada(boolean cubre) { this.cubre = cubre; }

        @Override
        public boolean atiende(String direccion) {
            String estado = cubre ? "OK  - zona con cobertura" : "FALLA - zona sin cobertura";
            System.out.printf("    %-14s %s%n", "[Direccion]", estado);
            return cubre;
        }
    }

    static class CuponesSimulados implements ServicioCupones {
        private final boolean valido;
        CuponesSimulados(boolean valido) { this.valido = valido; }

        @Override
        public boolean esValido(Pedido pedido) {
            String estado = valido ? "OK  - cupon vigente y aplicable" : "FALLA - cupon invalido o expirado";
            System.out.printf("    %-14s %s%n", "[Cupon]", estado);
            return valido;
        }
    }

    // -----------------------------------------------------------------------
    // Fabrica de cadena estandar: Stock -> Antifraude -> Credito -> Direccion
    // -----------------------------------------------------------------------

    static ProcesadorPedidos cadenaEstandar(
            boolean hayStock, boolean esSospechoso, boolean tieneCob) {

        ValidadorStock     stock     = new ValidadorStock(new InventarioSimulado(hayStock));
        ValidadorAntifraude fraude   = new ValidadorAntifraude(new AntifraudioSimulado(esSospechoso));
        ValidadorCredito   credito   = new ValidadorCredito() {
            @Override
            protected ResultadoValidacion validarPropio(Pedido pedido) {
                ResultadoValidacion r = super.validarPropio(pedido);
                if (pedido.isPagaConCredito()) {
                    String estado = r.isAprobado()
                            ? "OK  - dentro del cupo ($" + pedido.getCupoDisponible() + ")"
                            : "FALLA - " + r.getMotivo();
                    System.out.printf("    %-14s %s%n", "[Credito]", estado);
                } else {
                    System.out.printf("    %-14s %s%n", "[Credito]", "OK  - no usa credito de tienda");
                }
                return r;
            }
        };
        ValidadorDireccion dir = new ValidadorDireccion(new CoberturaSimulada(tieneCob));

        stock.setSiguiente(fraude).setSiguiente(credito).setSiguiente(dir);
        return new ProcesadorPedidos(stock);
    }

    // -----------------------------------------------------------------------
    // Impresion del resultado
    // -----------------------------------------------------------------------

    static void imprimir(int n, String descripcion, Pedido pedido, ProcesadorPedidos p) {
        System.out.println();
        System.out.println("  Pedido " + n + ": " + descripcion);
        System.out.println("  " + "-".repeat(55));
        ResultadoValidacion r = p.procesar(pedido);
        System.out.println("  " + "-".repeat(55));
        System.out.println("  Resultado: " + r);
    }

    // -----------------------------------------------------------------------
    // Main
    // -----------------------------------------------------------------------

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  Validacion de pedidos");
        System.out.println("  Patron: Chain of Responsibility");
        System.out.println("=".repeat(60));

        List<LineaPedido> items = List.of(
                new LineaPedido("LAPTOP-001", 1),
                new LineaPedido("MOUSE-007",  2)
        );

        // 1. Pedido valido: pasa toda la cadena
        imprimir(1, "Pedido normal - pasa todas las validaciones",
                new Pedido.Builder()
                        .productos(items)
                        .total(new BigDecimal("850.00"))
                        .direccion("Av. 10 de Agosto N22-40, Quito")
                        .build(),
                cadenaEstandar(true, false, true));

        // 2. Rechazado por stock
        imprimir(2, "Sin existencias - cadena se detiene en Stock",
                new Pedido.Builder()
                        .productos(items)
                        .total(new BigDecimal("850.00"))
                        .direccion("Av. Amazonas, Quito")
                        .build(),
                cadenaEstandar(false, false, true));

        // 3. Rechazado por antifraude
        imprimir(3, "Patron de fraude detectado - cadena se detiene en Antifraude",
                new Pedido.Builder()
                        .productos(items)
                        .total(new BigDecimal("9999.00"))
                        .direccion("Av. Republica, Quito")
                        .build(),
                cadenaEstandar(true, true, true));

        // 4. Rechazado por credito insuficiente
        imprimir(4, "Pago con credito - supera el cupo disponible",
                new Pedido.Builder()
                        .productos(items)
                        .total(new BigDecimal("1200.00"))
                        .pagaConCredito(true)
                        .cupoDisponible(new BigDecimal("500.00"))
                        .direccion("Calle Olmedo 123, Guayaquil")
                        .build(),
                cadenaEstandar(true, false, true));

        // 5. Credito aprobado dentro del cupo
        imprimir(5, "Pago con credito - dentro del cupo",
                new Pedido.Builder()
                        .productos(List.of(new LineaPedido("TV-55", 1)))
                        .total(new BigDecimal("400.00"))
                        .pagaConCredito(true)
                        .cupoDisponible(new BigDecimal("600.00"))
                        .direccion("Av. Colon, Quito")
                        .build(),
                cadenaEstandar(true, false, true));

        // 6. Rechazado por direccion sin cobertura
        imprimir(6, "Direccion fuera de zona de entrega",
                new Pedido.Builder()
                        .productos(items)
                        .total(new BigDecimal("300.00"))
                        .direccion("Parroquia Chugchilan, Cotopaxi")
                        .build(),
                cadenaEstandar(true, false, false));

        // 7. Cliente corporativo: salta antifraude
        System.out.println();
        System.out.println("  Pedido 7: Cliente corporativo - antifraude omitido por regla de negocio");
        System.out.println("  " + "-".repeat(55));
        ValidadorStock     sc7 = new ValidadorStock(new InventarioSimulado(true));
        ValidadorAntifraude af7 = new ValidadorAntifraude(new AntifraudioSimulado(true)) {
            @Override
            protected ResultadoValidacion validarPropio(Pedido pedido) {
                if (pedido.isClienteCorporativo()) {
                    System.out.printf("    %-14s %s%n", "[Antifraude]", "OMITIDO - cliente corporativo");
                    return ResultadoValidacion.aprobado();
                }
                return super.validarPropio(pedido);
            }
        };
        ValidadorDireccion dir7 = new ValidadorDireccion(new CoberturaSimulada(true));
        sc7.setSiguiente(af7).setSiguiente(new ValidadorCredito()).setSiguiente(dir7);
        ProcesadorPedidos p7 = new ProcesadorPedidos(sc7);
        Pedido pedidoCorp = new Pedido.Builder()
                .productos(List.of(new LineaPedido("SERV-CORP", 100)))
                .total(new BigDecimal("45000.00"))
                .clienteCorporativo(true)
                .direccion("Parque Empresarial Quito Norte")
                .build();
        ResultadoValidacion r7 = p7.procesar(pedidoCorp);
        System.out.println("  " + "-".repeat(55));
        System.out.println("  Resultado: " + r7);

        // 8. Black Friday: cadena con ValidadorCupon insertado
        System.out.println();
        System.out.println("  Pedido 8: BLACK FRIDAY - cadena con ValidadorCupon insertado (cupon valido)");
        System.out.println("  " + "-".repeat(55));
        ValidadorStock     sc8  = new ValidadorStock(new InventarioSimulado(true));
        ValidadorCupon     cup8 = new ValidadorCupon(new CuponesSimulados(true));
        ValidadorDireccion dir8 = new ValidadorDireccion(new CoberturaSimulada(true));
        sc8.setSiguiente(cup8).setSiguiente(dir8);
        ProcesadorPedidos p8 = new ProcesadorPedidos(sc8);
        Pedido pedidoBF = new Pedido.Builder()
                .productos(List.of(new LineaPedido("AUDIFONOS-BF", 3)))
                .total(new BigDecimal("210.00"))
                .cupon("BLACKFRIDAY2024")
                .direccion("Av. 9 de Octubre, Guayaquil")
                .build();
        ResultadoValidacion r8 = p8.procesar(pedidoBF);
        System.out.println("  " + "-".repeat(55));
        System.out.println("  Resultado: " + r8);

        System.out.println();
        System.out.println("  Pedido 9: BLACK FRIDAY - cupon expirado");
        System.out.println("  " + "-".repeat(55));
        ValidadorCupon cup9 = new ValidadorCupon(new CuponesSimulados(false));
        ProcesadorPedidos p9 = new ProcesadorPedidos(cup9);
        Pedido pedidoBF2 = new Pedido.Builder()
                .productos(List.of(new LineaPedido("TABLET-X", 1)))
                .total(new BigDecimal("350.00"))
                .cupon("PROMO-2019")
                .direccion("Av. 6 de Diciembre, Quito")
                .build();
        ResultadoValidacion r9 = p9.procesar(pedidoBF2);
        System.out.println("  " + "-".repeat(55));
        System.out.println("  Resultado: " + r9);

        System.out.println();
        System.out.println("=".repeat(60));
        System.out.println("  Fin de la demo");
        System.out.println("=".repeat(60));
    }
}
