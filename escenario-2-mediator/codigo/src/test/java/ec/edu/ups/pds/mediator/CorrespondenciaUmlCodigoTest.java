package ec.edu.ups.pds.mediator;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica que la estructura del codigo Java corresponde al diagrama de clases mejorado
 * (mediatorE2_mejorado.puml / diagrama-clases-mejorado.png).
 *
 * Convencion de visibilidad UML a Java aplicada:
 *   UML "-" (privado)   -> campo private en Java.
 *   UML "+" (publico)   -> campo private + metodo getter/is publico en Java.
 *   UML "#" (protegido) -> campo o metodo protected en Java.
 *
 * Ante cualquier cambio de nombre en el UML o en el codigo esta prueba detecta
 * la divergencia y obliga a sincronizar ambos artefactos.
 */
class CorrespondenciaUmlCodigoTest {

    private static final String PAQUETE = "ec.edu.ups.pds.mediator";
    private static final Path   PUML    = Paths.get("../uml/mediatorE2_mejorado.puml");

    // ═══════════════════════════════════════════════════════════════════════
    // 1. El archivo PUML existe y contiene todos los tipos del dominio
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void pumlExisteYEsLegible() throws IOException {
        assertTrue(Files.exists(PUML), "No se encuentra el archivo UML en: " + PUML.toAbsolutePath());
        assertTrue(Files.readString(PUML).contains("JefeDeCocina"), "El PUML no contiene JefeDeCocina");
    }

    @Test
    void pumlContieneTodasLasClasesEInterfaces() throws IOException {
        String c = Files.readString(PUML);
        for (String nombre : List.of(
                "MediadorCocina", "JefeDeCocina", "ComponenteCocina",
                "Mesero", "EstacionCaliente", "EstacionFria", "Caja",
                "EventoCocina", "TipoEvento", "PedidoMesa", "EstadoMesa")) {
            assertTrue(c.contains(nombre), "El PUML no contiene el tipo: " + nombre);
        }
    }

    @Test
    void pumlDeclaraMediadorCocinaComoInterface() throws IOException {
        assertTrue(Files.readString(PUML).contains("interface MediadorCocina"),
                "MediadorCocina debe estar declarada como 'interface' en el PUML");
    }

    @Test
    void pumlDeclaraComponenteCocinaComoAbstracta() throws IOException {
        assertTrue(Files.readString(PUML).contains("abstract class ComponenteCocina"),
                "ComponenteCocina debe estar declarada como 'abstract class' en el PUML");
    }

    @Test
    void pumlDeclaraTipoEventoComoEnum() throws IOException {
        assertTrue(Files.readString(PUML).contains("enum TipoEvento"),
                "TipoEvento debe estar declarado como 'enum' en el PUML");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 2. Las clases e interfaces existen en el paquete correcto
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void todasLasClasesExistenEnElPaquete() {
        for (String nombre : List.of(
                "MediadorCocina", "JefeDeCocina", "ComponenteCocina",
                "Mesero", "EstacionCaliente", "EstacionFria", "Caja",
                "EventoCocina", "TipoEvento", "PedidoMesa", "EstadoMesa", "Main")) {
            assertDoesNotThrow(() -> cargar(nombre), "Tipo no encontrado en el paquete: " + nombre);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 3. Modificadores y jerarquia
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void mediadorCocinaEsInterface() throws ClassNotFoundException {
        assertTrue(cargar("MediadorCocina").isInterface(), "MediadorCocina debe ser interface");
    }

    @Test
    void componenteCocinaEsAbstracta() throws ClassNotFoundException {
        assertTrue(Modifier.isAbstract(cargar("ComponenteCocina").getModifiers()),
                "ComponenteCocina debe ser abstracta");
    }

    @Test
    void tipoEventoEsEnum() throws ClassNotFoundException {
        assertTrue(cargar("TipoEvento").isEnum(), "TipoEvento debe ser enum");
    }

    @Test
    void jefeDeCocinaImplementaMediadorCocina() throws ClassNotFoundException {
        assertTrue(cargar("MediadorCocina").isAssignableFrom(cargar("JefeDeCocina")),
                "JefeDeCocina debe implementar MediadorCocina");
    }

    @Test
    void colegasConcretos_ExtienenComponenteCocina() throws ClassNotFoundException {
        Class<?> colega = cargar("ComponenteCocina");
        for (String nombre : List.of("Mesero", "EstacionCaliente", "EstacionFria", "Caja")) {
            assertTrue(colega.isAssignableFrom(cargar(nombre)),
                    nombre + " debe extender ComponenteCocina");
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 4. MediadorCocina: firmas exactas del UML
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void mediadorCocina_registrar_recibeComponenteCocinaDevuelveVoid() throws Exception {
        Method m = cargar("MediadorCocina").getMethod("registrar", cargar("ComponenteCocina"));
        assertEquals(void.class, m.getReturnType());
    }

    @Test
    void mediadorCocina_notificar_recibeComponenteYEventoDevuelveVoid() throws Exception {
        Method m = cargar("MediadorCocina").getMethod("notificar", cargar("ComponenteCocina"), cargar("EventoCocina"));
        assertEquals(void.class, m.getReturnType());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 5. ComponenteCocina (Colleague): campo protegido y metodos del UML
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void componenteCocina_CampoProtegido_mediador() throws Exception {
        Field f = cargar("ComponenteCocina").getDeclaredField("mediador");
        assertTrue(Modifier.isProtected(f.getModifiers()), "mediador debe ser protected (UML #)");
        assertEquals(cargar("MediadorCocina"), f.getType());
    }

    @Test
    void componenteCocina_informar_publicoDevuelveVoid() throws Exception {
        Method m = cargar("ComponenteCocina").getMethod("informar", cargar("EventoCocina"));
        assertTrue(Modifier.isPublic(m.getModifiers()), "informar debe ser public (UML +)");
        assertEquals(void.class, m.getReturnType());
    }

    @Test
    void componenteCocina_recibir_publicoAbstracto() throws Exception {
        Method m = cargar("ComponenteCocina").getMethod("recibir", cargar("EventoCocina"));
        assertTrue(Modifier.isAbstract(m.getModifiers()), "recibir debe ser abstracto");
        assertTrue(Modifier.isPublic(m.getModifiers()), "recibir debe ser public (UML +)");
        assertEquals(void.class, m.getReturnType());
    }

    @Test
    void componenteCocina_atiende_publicoAbstractoDevuelveBoolean() throws Exception {
        Method m = cargar("ComponenteCocina").getMethod("atiende", cargar("TipoEvento"));
        assertTrue(Modifier.isAbstract(m.getModifiers()), "atiende debe ser abstracto");
        assertTrue(Modifier.isPublic(m.getModifiers()), "atiende debe ser public (UML +)");
        assertEquals(boolean.class, m.getReturnType());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 6. JefeDeCocina: campos privados y metodos del UML
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void jefeDeCocina_CampoPrivado_componentes() throws Exception {
        Field f = cargar("JefeDeCocina").getDeclaredField("componentes");
        assertTrue(Modifier.isPrivate(f.getModifiers()), "componentes debe ser private (UML -)");
        assertTrue(List.class.isAssignableFrom(f.getType()), "componentes debe ser List");
    }

    @Test
    void jefeDeCocina_CampoPrivado_estados() throws Exception {
        Field f = cargar("JefeDeCocina").getDeclaredField("estados");
        assertTrue(Modifier.isPrivate(f.getModifiers()), "estados debe ser private (UML -)");
        assertTrue(Map.class.isAssignableFrom(f.getType()), "estados debe ser Map");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 7. EstadoMesa: campos privados del UML
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void estadoMesa_CamposPrivados_pendientesListosServidosRetirados() throws Exception {
        Class<?> c = cargar("EstadoMesa");
        for (String nombre : List.of("pendientes", "listos", "servidos", "retirados")) {
            Field f = c.getDeclaredField(nombre);
            assertTrue(Modifier.isPrivate(f.getModifiers()), nombre + " debe ser private (UML -)");
            assertTrue(Map.class.isAssignableFrom(f.getType()), nombre + " debe ser Map");
        }
    }

    @Test
    void estadoMesa_marcarListo_publicoDevuelveVoid() throws Exception {
        Method m = cargar("EstadoMesa").getMethod("marcarListo", String.class);
        assertTrue(Modifier.isPublic(m.getModifiers()), "marcarListo debe ser public");
        assertEquals(void.class, m.getReturnType());
    }

    @Test
    void estadoMesa_marcarServidos_publicoDevuelveVoid() throws Exception {
        Method m = cargar("EstadoMesa").getMethod("marcarServidos");
        assertTrue(Modifier.isPublic(m.getModifiers()), "marcarServidos debe ser public");
        assertEquals(void.class, m.getReturnType());
    }

    @Test
    void estadoMesa_retirar_publicoDevuelveVoid() throws Exception {
        Method m = cargar("EstadoMesa").getMethod("retirar", String.class);
        assertTrue(Modifier.isPublic(m.getModifiers()), "retirar debe ser public");
        assertEquals(void.class, m.getReturnType());
    }

    @Test
    void estadoMesa_estaLista_publicoDevuelveBoolean() throws Exception {
        Method m = cargar("EstadoMesa").getMethod("estaLista");
        assertTrue(Modifier.isPublic(m.getModifiers()), "estaLista debe ser public");
        assertEquals(boolean.class, m.getReturnType());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 8. Colegas concretos: metodos especificos del UML
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void mesero_registrarPedido_publicoDevuelveVoid() throws Exception {
        Method m = cargar("Mesero").getMethod("registrarPedido", cargar("PedidoMesa"));
        assertTrue(Modifier.isPublic(m.getModifiers()), "registrarPedido debe ser public");
        assertEquals(void.class, m.getReturnType());
    }

    @Test
    void estacionCaliente_reportarFaltante_publicoDevuelveVoid() throws Exception {
        Method m = cargar("EstacionCaliente").getMethod("reportarFaltante", String.class, String.class);
        assertTrue(Modifier.isPublic(m.getModifiers()), "reportarFaltante debe ser public");
        assertEquals(void.class, m.getReturnType());
    }

    @Test
    void caja_emitirCuenta_publicoDevuelveVoid() throws Exception {
        Method m = cargar("Caja").getMethod("emitirCuenta", String.class);
        assertTrue(Modifier.isPublic(m.getModifiers()), "emitirCuenta debe ser public");
        assertEquals(void.class, m.getReturnType());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 9. TipoEvento: contiene todos los valores del UML
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void tipoEvento_ContieneLosSieteValores() throws ClassNotFoundException {
        Class<?> c = cargar("TipoEvento");
        Object[] constantes = c.getEnumConstants();
        assertEquals(7, constantes.length, "TipoEvento debe tener exactamente 7 valores");
        List<String> nombres = List.of(
                "PEDIDO_REGISTRADO", "PLATO_LISTO", "PLATOS_SERVIDOS",
                "INSUMO_AGOTADO", "MESA_LISTA", "RETIRAR_PLATO", "CUENTA_SOLICITADA");
        for (Object cte : constantes) {
            assertTrue(nombres.contains(cte.toString()), "Valor inesperado en TipoEvento: " + cte);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 10. EventoCocina y PedidoMesa: getters de los atributos publicos del UML
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void eventoCocina_GettersTipo_MesaId_Plato_Pedido() throws Exception {
        Class<?> c = cargar("EventoCocina");
        assertNotNull(c.getMethod("getTipo"));
        assertNotNull(c.getMethod("getMesaId"));
        assertNotNull(c.getMethod("getPlato"));
        assertNotNull(c.getMethod("getPedido"));
    }

    @Test
    void pedidoMesa_GettersMesaId_Platos() throws Exception {
        Class<?> c = cargar("PedidoMesa");
        assertEquals(String.class,   c.getMethod("getMesaId").getReturnType());
        assertEquals(List.class,     c.getMethod("getPlatos").getReturnType());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 11. Aislamiento: ningun colega concreto tiene campo de otro colega
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void colegasConcretos_NingunoCampoDeOtroColega() throws ClassNotFoundException {
        List<Class<?>> concretos = List.of(
                cargar("Mesero"), cargar("EstacionCaliente"),
                cargar("EstacionFria"), cargar("Caja"));
        for (Class<?> colega : concretos) {
            for (Field campo : colega.getDeclaredFields()) {
                for (Class<?> otro : concretos) {
                    if (otro != colega) {
                        assertFalse(campo.getType() == otro,
                                colega.getSimpleName() + " no debe tener campo de tipo " + otro.getSimpleName());
                    }
                }
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Utilidad
    // ═══════════════════════════════════════════════════════════════════════

    private Class<?> cargar(String nombreSimple) throws ClassNotFoundException {
        return Class.forName(PAQUETE + "." + nombreSimple);
    }
}
