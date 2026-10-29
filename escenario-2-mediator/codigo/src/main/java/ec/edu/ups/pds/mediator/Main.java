package ec.edu.ups.pds.mediator;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Demuestra el patron Mediator en la cocina de El Fogon Andino.
 *
 * Tres escenarios cubren los flujos del diagrama de secuencia:
 *   1. Pedido normal: cada plato llega a su estacion, la mesa se sirve y la caja
 *      cobra lo servido cuando el mesero pide la cuenta.
 *   2. Faltante de insumo (fritada): JefeDeCocina retira el plato, avisa al
 *      mesero y a caja, y evalua si los platos restantes ya permiten servir.
 *   3. Faltante con platos aun pendientes: el faltante no dispara MESA_LISTA
 *      porque todavia hay platos sin preparar.
 *
 * Ninguna clase concreta (Mesero, EstacionCaliente, EstacionFria, Caja)
 * referencia a otra: toda coordinacion pasa por JefeDeCocina.
 */
public class Main {

    public static void main(String[] args) {
        JefeDeCocina jefe = new JefeDeCocina();

        Mesero           mesero   = new Mesero("Carlos", jefe);
        EstacionCaliente caliente = new EstacionCaliente(jefe,
                Set.of("fritada", "lomo saltado", "caldo", "seco de pollo"));
        EstacionFria     fria     = new EstacionFria(jefe,
                Set.of("ceviche", "ensalada", "limonada"));
        Caja             caja     = new Caja(jefe);

        jefe.registrar(mesero);
        jefe.registrar(caliente);
        jefe.registrar(fria);
        jefe.registrar(caja);

        separador("ESCENARIO 1 - Pedido normal (mesa 3)");
        System.out.println("Platos pedidos: ceviche (estacion fria) + lomo saltado (estacion caliente)");
        System.out.println();

        mesero.registrarPedido(new PedidoMesa("3", List.of("ceviche", "lomo saltado")));
        System.out.println();

        System.out.println(">> La estacion fria termina el ceviche:");
        fria.prepararPlato("3", "ceviche");
        imprimirEstado(jefe, "3");
        System.out.println();

        System.out.println(">> La estacion caliente termina el lomo saltado:");
        caliente.prepararPlato("3", "lomo saltado");
        imprimirEstado(jefe, "3");
        System.out.println();

        System.out.println(">> El mesero pide la cuenta:");
        mesero.solicitarCuenta("3");

        separador("ESCENARIO 2 - Faltante de insumo: fritada (mesa 7) [diagrama de secuencia UML]");
        System.out.println("Platos pedidos: fritada (estacion caliente) + ensalada (estacion fria)");
        System.out.println();

        mesero.registrarPedido(new PedidoMesa("7", List.of("fritada", "ensalada")));
        System.out.println();

        System.out.println(">> La estacion fria termina la ensalada:");
        fria.prepararPlato("7", "ensalada");
        imprimirEstado(jefe, "7");
        System.out.println();

        System.out.println(">> La estacion caliente reporta faltante de fritada:");
        caliente.reportarFaltante("7", "fritada");
        imprimirEstado(jefe, "7");
        System.out.println();

        System.out.println(">> El mesero pide la cuenta (la fritada no se cobra):");
        mesero.solicitarCuenta("7");

        separador("ESCENARIO 3 - Faltante con platos aun pendientes (mesa 12)");
        System.out.println("Platos pedidos: seco de pollo + limonada + fritada");
        System.out.println("La fritada falta pero el seco de pollo sigue pendiente: mesa NO queda lista aun.");
        System.out.println();

        mesero.registrarPedido(new PedidoMesa("12", List.of("seco de pollo", "limonada", "fritada")));
        System.out.println();

        System.out.println(">> La estacion fria termina la limonada:");
        fria.prepararPlato("12", "limonada");
        imprimirEstado(jefe, "12");
        System.out.println();

        System.out.println(">> La estacion caliente reporta faltante de fritada:");
        caliente.reportarFaltante("12", "fritada");
        imprimirEstado(jefe, "12");
        System.out.println();

        System.out.println(">> La estacion caliente termina el seco de pollo:");
        caliente.prepararPlato("12", "seco de pollo");
        imprimirEstado(jefe, "12");
    }

    private static void imprimirEstado(JefeDeCocina jefe, String mesaId) {
        EstadoMesa estado = jefe.obtenerEstado(mesaId);
        if (estado == null) return;
        System.out.println("   Estado mesa " + mesaId + ":");
        imprimirMapa("pendientes", estado.getPendientes());
        imprimirMapa("listos    ", estado.getListos());
        imprimirMapa("servidos  ", estado.getServidos());
        imprimirMapa("retirados ", estado.getRetirados());
    }

    private static void imprimirMapa(String etiqueta, Map<String, Integer> mapa) {
        if (!mapa.isEmpty()) {
            System.out.println("     " + etiqueta + ": " + mapa);
        }
    }

    private static void separador(String titulo) {
        System.out.println();
        System.out.println("=".repeat(60));
        System.out.println("  " + titulo);
        System.out.println("=".repeat(60));
    }
}
