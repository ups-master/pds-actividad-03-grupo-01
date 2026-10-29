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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    void pumlEstereotipaLosRolesDelPatron() throws IOException {
        String c = Files.readString(PUML);
        Map<String, String> roles = new java.util.LinkedHashMap<>();
        roles.put("MediadorCocina", "Mediator");
        roles.put("JefeDeCocina", "ConcreteMediator");
        roles.put("ComponenteCocina", "Colleague");
        roles.put("Mesero", "ConcreteColleague");
        roles.put("EstacionCaliente", "ConcreteColleague");
        roles.put("EstacionFria", "ConcreteColleague");
        roles.put("Caja", "ConcreteColleague");
        roles.put("Main", "Client");
        roles.forEach((tipo, rol) -> assertTrue(
                Pattern.compile("^\\s*(abstract class|class|interface)\\s+" + tipo + "\\s+<<" + rol + ">>",
                        Pattern.MULTILINE).matcher(c).find(),
                tipo + " debe tener el estereotipo <<" + rol + ">> en el PUML"));
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

    /** Toda clase del codigo debe estar en el diagrama (Main incluida: es el Client del patron). */
    @Test
    void todasLasClasesDelCodigoEstanEnElPuml() throws IOException {
        String puml = Files.readString(PUML);
        Path fuentes = Paths.get("src/main/java/" + PAQUETE.replace('.', '/'));
        try (var archivos = Files.list(fuentes)) {
            List<String> ausentes = archivos
                    .map(p -> p.getFileName().toString())
                    .filter(n -> n.endsWith(".java") && !n.equals("package-info.java"))
                    .map(n -> n.substring(0, n.length() - ".java".length()))
                    .filter(n -> !Pattern.compile("^\\s*(abstract class|class|interface|enum)\\s+" + n + "\\b",
                            Pattern.MULTILINE).matcher(puml).find())
                    .toList();
            assertTrue(ausentes.isEmpty(), "Clases del codigo ausentes del PUML: " + ausentes);
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
    void estadoMesa_CampoPrivado_servida() throws Exception {
        Field f = cargar("EstadoMesa").getDeclaredField("servida");
        assertTrue(Modifier.isPrivate(f.getModifiers()), "servida debe ser private (UML -)");
        assertEquals(boolean.class, f.getType());
    }

    @Test
    void estadoMesa_agregarPlatos_publicoDevuelveVoid() throws Exception {
        Method m = cargar("EstadoMesa").getMethod("agregarPlatos", List.class);
        assertTrue(Modifier.isPublic(m.getModifiers()), "agregarPlatos debe ser public");
        assertEquals(void.class, m.getReturnType());
    }

    @Test
    void estadoMesa_estaServida_publicoDevuelveBoolean() throws Exception {
        Method m = cargar("EstadoMesa").getMethod("estaServida");
        assertTrue(Modifier.isPublic(m.getModifiers()), "estaServida debe ser public");
        assertEquals(boolean.class, m.getReturnType());
    }

    @Test
    void estadoMesa_marcarListo_publicoDevuelveBoolean() throws Exception {
        Method m = cargar("EstadoMesa").getMethod("marcarListo", String.class);
        assertTrue(Modifier.isPublic(m.getModifiers()), "marcarListo debe ser public");
        assertEquals(boolean.class, m.getReturnType());
    }

    @Test
    void estadoMesa_marcarServidos_publicoDevuelveVoid() throws Exception {
        Method m = cargar("EstadoMesa").getMethod("marcarServidos");
        assertTrue(Modifier.isPublic(m.getModifiers()), "marcarServidos debe ser public");
        assertEquals(void.class, m.getReturnType());
    }

    @Test
    void estadoMesa_retirar_publicoDevuelveBoolean() throws Exception {
        Method m = cargar("EstadoMesa").getMethod("retirar", String.class);
        assertTrue(Modifier.isPublic(m.getModifiers()), "retirar debe ser public");
        assertEquals(boolean.class, m.getReturnType());
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
    void mesero_solicitarCuenta_publicoDevuelveVoid() throws Exception {
        Method m = cargar("Mesero").getMethod("solicitarCuenta", String.class);
        assertTrue(Modifier.isPublic(m.getModifiers()), "solicitarCuenta debe ser public");
        assertEquals(void.class, m.getReturnType());
    }

    @Test
    void estaciones_prepararPlato_publicoDevuelveVoid() throws Exception {
        for (String nombre : List.of("EstacionCaliente", "EstacionFria")) {
            Method m = cargar(nombre).getMethod("prepararPlato", String.class, String.class);
            assertTrue(Modifier.isPublic(m.getModifiers()), nombre + ".prepararPlato debe ser public");
            assertEquals(void.class, m.getReturnType());
        }
    }

    @Test
    void estaciones_CampoPrivado_menu() throws Exception {
        for (String nombre : List.of("EstacionCaliente", "EstacionFria")) {
            Field f = cargar(nombre).getDeclaredField("menu");
            assertTrue(Modifier.isPrivate(f.getModifiers()), nombre + ".menu debe ser private (UML -)");
            assertTrue(java.util.Set.class.isAssignableFrom(f.getType()), "menu debe ser Set");
        }
    }

    @Test
    void componenteCocina_prepara_publicoDevuelveBoolean() throws Exception {
        Method m = cargar("ComponenteCocina").getMethod("prepara", String.class);
        assertTrue(Modifier.isPublic(m.getModifiers()), "prepara debe ser public (UML +)");
        assertEquals(boolean.class, m.getReturnType());
    }

    @Test
    void caja_emitirCuenta_recibeMesaYConsumoDevuelveVoid() throws Exception {
        Method m = cargar("Caja").getMethod("emitirCuenta", String.class, Map.class);
        assertTrue(Modifier.isPublic(m.getModifiers()), "emitirCuenta debe ser public");
        assertEquals(void.class, m.getReturnType());
    }

    @Test
    void caja_obtenerCuenta_publicoDevuelveMapa() throws Exception {
        Method m = cargar("Caja").getMethod("obtenerCuenta", String.class);
        assertTrue(Modifier.isPublic(m.getModifiers()), "obtenerCuenta debe ser public");
        assertEquals(Map.class, m.getReturnType());
    }

    @Test
    void jefeDeCocina_obtenerEstado_publicoDevuelveEstadoMesa() throws Exception {
        Method m = cargar("JefeDeCocina").getMethod("obtenerEstado", String.class);
        assertTrue(Modifier.isPublic(m.getModifiers()), "obtenerEstado debe ser public");
        assertEquals(cargar("EstadoMesa"), m.getReturnType());
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
    void eventoCocina_GettersTipo_MesaId_Plato_Pedido_Consumo() throws Exception {
        Class<?> c = cargar("EventoCocina");
        assertNotNull(c.getMethod("getTipo"));
        assertNotNull(c.getMethod("getMesaId"));
        assertNotNull(c.getMethod("getPlato"));
        assertNotNull(c.getMethod("getPedido"));
        assertEquals(Map.class, c.getMethod("getConsumo").getReturnType());
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
    // 12. Comparacion generica: cada miembro del PUML contra el codigo
    // ═══════════════════════════════════════════════════════════════════════

    private static final Pattern TIPO = Pattern.compile("^(abstract class|class|interface|enum)\\s+(\\w+)(?:\\s+<<\\w+>>)?\\s*(\\{)?\\s*$");
    private static final Pattern OPERACION = Pattern.compile("^([+\\-#])\\s*(\\w+)\\((.*)\\)\\s*:\\s*(.+)$");
    private static final Pattern ATRIBUTO = Pattern.compile("^([+\\-#])\\s*(\\w+)\\s*:\\s*(.+)$");

    /** Miembros declarados en el PUML por tipo, en el orden en que aparecen. */
    private Map<String, List<String>> miembrosDelPuml() throws IOException {
        Map<String, List<String>> tipos = new java.util.LinkedHashMap<>();
        List<String> actual = null;
        for (String linea : Files.readAllLines(PUML)) {
            String l = linea.strip();
            Matcher t = TIPO.matcher(l);
            if (t.matches()) {
                actual = t.group(3) != null ? new java.util.ArrayList<>() : null;
                tipos.put(t.group(2), actual != null ? actual : new java.util.ArrayList<>());
            } else if (l.equals("}")) {
                actual = null;
            } else if (actual != null && !l.isEmpty()) {
                actual.add(l);
            }
        }
        return tipos;
    }

    private static String sinGenericos(String tipo) {
        int i = tipo.indexOf('<');
        return (i < 0 ? tipo : tipo.substring(0, i)).strip();
    }

    /** Divide los parametros por comas de primer nivel (ignora las de Map&lt;K, V&gt;). */
    private static List<String> parametros(String texto) {
        List<String> res = new java.util.ArrayList<>();
        if (texto.isBlank()) return res;
        int nivel = 0, inicio = 0;
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if (c == '<') nivel++;
            else if (c == '>') nivel--;
            else if (c == ',' && nivel == 0) {
                res.add(texto.substring(inicio, i).strip());
                inicio = i + 1;
            }
        }
        res.add(texto.substring(inicio).strip());
        return res;
    }

    private static Method buscar(Class<?> clase, String nombre, List<String> tiposParametros) {
        for (Method m : clase.getDeclaredMethods()) {
            if (!m.getName().equals(nombre) || m.getParameterCount() != tiposParametros.size()) continue;
            boolean coincide = true;
            for (int i = 0; i < tiposParametros.size(); i++) {
                coincide &= m.getParameterTypes()[i].getSimpleName().equals(tiposParametros.get(i));
            }
            if (coincide) return m;
        }
        return null;
    }

    @Test
    void cadaMiembroDelPumlExisteEnElCodigoConSuVisibilidadYTipos() throws Exception {
        for (Map.Entry<String, List<String>> tipo : miembrosDelPuml().entrySet()) {
            Class<?> clase = cargar(tipo.getKey());
            for (String miembro : tipo.getValue()) {
                String donde = tipo.getKey() + ": " + miembro;
                Matcher op = OPERACION.matcher(miembro);
                Matcher at = ATRIBUTO.matcher(miembro);
                if (clase.isEnum() && !miembro.matches("^[+\\-#].*")) {
                    final String constante = miembro;
                    assertTrue(java.util.Arrays.stream(clase.getEnumConstants())
                            .anyMatch(c -> c.toString().equals(constante)), "Falta la constante " + donde);
                } else if (op.matches()) {
                    List<String> tipos = new java.util.ArrayList<>();
                    for (String p : parametros(op.group(3))) {
                        tipos.add(sinGenericos(p.substring(p.indexOf(':') + 1)));
                    }
                    Method m = buscar(clase, op.group(2), tipos);
                    assertNotNull(m, "Falta la operacion " + donde);
                    assertEquals(sinGenericos(op.group(4)), m.getReturnType().getSimpleName(),
                            "Tipo de retorno distinto en " + donde);
                    assertVisibilidad(op.group(1), m.getModifiers(), donde);
                } else if (at.matches()) {
                    Field f = clase.getDeclaredField(at.group(2));
                    assertEquals(sinGenericos(at.group(3)), f.getType().getSimpleName(),
                            "Tipo de atributo distinto en " + donde);
                    if (at.group(1).equals("+")) {
                        // Convencion del proyecto: UML "+" = campo privado con getter/is publico.
                        assertTrue(Modifier.isPrivate(f.getModifiers()), donde + " debe ser private con getter");
                        String sufijo = Character.toUpperCase(f.getName().charAt(0)) + f.getName().substring(1);
                        boolean hayGetter = false;
                        for (String prefijo : List.of("get", "is")) {
                            try {
                                hayGetter |= Modifier.isPublic(clase.getMethod(prefijo + sufijo).getModifiers());
                            } catch (NoSuchMethodException ignorada) {
                                // se prueba el siguiente prefijo
                            }
                        }
                        assertTrue(hayGetter, "Falta el getter publico de " + donde);
                    } else {
                        assertVisibilidad(at.group(1), f.getModifiers(), donde);
                    }
                }
            }
        }
    }

    @Test
    void elCodigoNoTieneOperacionesPropiasAusentesDelPuml() throws Exception {
        Map<String, List<String>> uml = miembrosDelPuml();
        for (String nombre : uml.keySet()) {
            if (nombre.equals("Main")) continue;
            Class<?> clase = cargar(nombre);
            for (Method m : clase.getDeclaredMethods()) {
                int mod = m.getModifiers();
                boolean exenta = m.isSynthetic() || Modifier.isPrivate(mod) || Modifier.isStatic(mod)
                        || (m.getParameterCount() == 0 && m.getName().matches("^(get|is)[A-Z].*"))
                        || clase.isEnum() || sobrescribe(m);
                if (exenta) continue;
                boolean declarada = uml.get(nombre).stream().anyMatch(l -> {
                    Matcher op = OPERACION.matcher(l);
                    return op.matches() && op.group(2).equals(m.getName())
                            && parametros(op.group(3)).size() == m.getParameterCount();
                });
                assertTrue(declarada, nombre + "." + m.getName() + " existe en el codigo pero no en el PUML");
            }
        }
    }

    @Test
    void elCodigoNoTieneAtributosAusentesDelPuml() throws Exception {
        Map<String, List<String>> uml = miembrosDelPuml();
        for (String nombre : uml.keySet()) {
            if (nombre.equals("Main")) continue;
            Class<?> clase = cargar(nombre);
            if (clase.isEnum()) continue;
            for (Field f : clase.getDeclaredFields()) {
                if (f.isSynthetic() || Modifier.isStatic(f.getModifiers())) continue;
                boolean declarado = uml.get(nombre).stream().anyMatch(l -> {
                    Matcher at = ATRIBUTO.matcher(l);
                    return at.matches() && at.group(2).equals(f.getName());
                });
                assertTrue(declarado, nombre + "." + f.getName() + " existe en el codigo pero no en el PUML");
            }
        }
    }

    private static void assertVisibilidad(String simbolo, int modificadores, String donde) {
        switch (simbolo) {
            case "-" -> assertTrue(Modifier.isPrivate(modificadores), donde + " debe ser private");
            case "#" -> assertTrue(Modifier.isProtected(modificadores), donde + " debe ser protected");
            default  -> assertTrue(Modifier.isPublic(modificadores), donde + " debe ser public");
        }
    }

    /** Una operacion que redefine la de un supertipo ya esta documentada en el supertipo. */
    private static boolean sobrescribe(Method m) {
        java.util.Deque<Class<?>> pendientes = new java.util.ArrayDeque<>();
        pendientes.add(m.getDeclaringClass());
        while (!pendientes.isEmpty()) {
            Class<?> c = pendientes.poll();
            if (c != m.getDeclaringClass()) {
                for (Method otro : c.getDeclaredMethods()) {
                    if (otro.getName().equals(m.getName())
                            && java.util.Arrays.equals(otro.getParameterTypes(), m.getParameterTypes())
                            && !Modifier.isPrivate(otro.getModifiers()) && !Modifier.isStatic(otro.getModifiers())) {
                        return true;
                    }
                }
            }
            if (c.getSuperclass() != null) pendientes.add(c.getSuperclass());
            pendientes.addAll(List.of(c.getInterfaces()));
        }
        return false;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Utilidad
    // ═══════════════════════════════════════════════════════════════════════

    private Class<?> cargar(String nombreSimple) throws ClassNotFoundException {
        return Class.forName(PAQUETE + "." + nombreSimple);
    }
}
