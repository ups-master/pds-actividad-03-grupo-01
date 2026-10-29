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

    static class AntifraudeSimulado implements ServicioAntifraude {
        private final boolean sospechoso;
        AntifraudeSimulado(boolean sospechoso) { this.sospechoso = sospechoso; }

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
    // Armado de cadenas (lo hace quien configura, no el ProcesadorPedidos)
    // -----------------------------------------------------------------------

    /** Cadena estandar: Stock -> Antifraude -> Credito -> Direccion. */
    static ProcesadorPedidos cadenaEstandar(
            boolean hayStock, boolean esSospechoso, boolean tieneCob) {

        ValidadorStock      stock   = new ValidadorStock(new InventarioSimulado(hayStock));
        ValidadorAntifraude fraude  = new ValidadorAntifraude(new AntifraudeSimulado(esSospechoso));
        ValidadorCredito    credito = new ValidadorCredito() {
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
        ValidadorDireccion  dir     = new ValidadorDireccion(new CoberturaSimulada(tieneCob));

        stock.setSiguiente(fraude).setSiguiente(credito).setSiguiente(dir);
        return new ProcesadorPedidos(stock);
    }

    /** Cadena de cinco eslabones, con el cupon al final: la del diagrama de secuencia. */
    static ProcesadorPedidos cadenaConCupon(boolean cuponValido) {
        ValidadorStock      stock  = new ValidadorStock(new InventarioSimulado(true));
        ValidadorAntifraude fraude = new ValidadorAntifraude(new AntifraudeSimulado(true));
        ValidadorCredito    cred   = new ValidadorCredito();
        ValidadorDireccion  dir    = new ValidadorDireccion(new CoberturaSimulada(true));
        ValidadorCupon      cupon  = new ValidadorCupon(new CuponesSimulados(cuponValido));

        stock.setSiguiente(fraude).setSiguiente(cred).setSiguiente(dir).setSiguiente(cupon);
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

        // 7. Cliente corporativo: ValidadorAntifraude omite la consulta (el servicio marcaria sospecha)
        imprimir(7, "Cliente corporativo - no aparece la linea [Antifraude]: no se consulta",
                new Pedido.Builder()
                        .productos(List.of(new LineaPedido("SERV-CORP", 100)))
                        .total(new BigDecimal("45000.00"))
                        .clienteCorporativo(true)
                        .direccion("Parque Empresarial Quito Norte")
                        .build(),
                cadenaEstandar(true, true, true));

        // 8. Diagrama de secuencia: corporativo, cadena de cinco eslabones, cupon invalido al final
        imprimir(8, "Corporativo con cupon invalido - cadena de 5 eslabones (secuencia UML)",
                new Pedido.Builder()
                        .productos(List.of(new LineaPedido("SERV-CORP", 10)))
                        .total(new BigDecimal("4500.00"))
                        .clienteCorporativo(true)
                        .cupon("PROMO-2019")
                        .direccion("Parque Empresarial Quito Norte")
                        .build(),
                cadenaConCupon(false));

        // 9. Black Friday: cadena reordenada con ValidadorCupon insertado entre Stock y Direccion
        ValidadorStock     stock = new ValidadorStock(new InventarioSimulado(true));
        ValidadorCupon     cup   = new ValidadorCupon(new CuponesSimulados(true));
        ValidadorDireccion dir   = new ValidadorDireccion(new CoberturaSimulada(true));
        stock.setSiguiente(cup).setSiguiente(dir);
        imprimir(9, "BLACK FRIDAY - Stock -> Cupon -> Direccion (cupon valido)",
                new Pedido.Builder()
                        .productos(List.of(new LineaPedido("AUDIFONOS-BF", 3)))
                        .total(new BigDecimal("210.00"))
                        .cupon("BLACKFRIDAY2024")
                        .direccion("Av. 9 de Octubre, Guayaquil")
                        .build(),
                new ProcesadorPedidos(stock));

        // 10. Eslabon unico: la cadena puede tener un solo validador
        imprimir(10, "BLACK FRIDAY - cupon expirado (cadena de un solo eslabon)",
                new Pedido.Builder()
                        .productos(List.of(new LineaPedido("TABLET-X", 1)))
                        .total(new BigDecimal("350.00"))
                        .cupon("PROMO-2019")
                        .direccion("Av. 6 de Diciembre, Quito")
                        .build(),
                new ProcesadorPedidos(new ValidadorCupon(new CuponesSimulados(false))));

        System.out.println();
        System.out.println("=".repeat(60));
        System.out.println("  Fin de la demo");
        System.out.println("=".repeat(60));
    }
}
