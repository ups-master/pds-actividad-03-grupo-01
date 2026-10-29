package ec.edu.ups.pds.chain;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
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
 * (chainE1_mejorado.puml / diagrama-clases-mejorado.png).
 *
 * Convencion de visibilidad UML → Java aplicada:
 *   - UML "-" (privado)    → campo private en Java.
 *   - UML "+" (publico)    → campo private + metodo getter/is publico en Java (encapsulacion).
 *   - UML "#" (protegido)  → metodo protected en Java.
 *
 * Ante cualquier cambio de nombre en el UML o en el codigo esta prueba detecta
 * la divergencia y obliga a sincronizar ambos artefactos.
 */
class CorrespondenciaUmlCodigoTest {

    private static final String PAQUETE = "ec.edu.ups.pds.chain";
    private static final Path   PUML    = Paths.get("../uml/chainE1_mejorado.puml");

    // ═══════════════════════════════════════════════════════════════════════
    // 1. El archivo PUML existe y contiene todos los tipos del dominio
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void pumlExisteYEsLegible() throws IOException {
        assertTrue(Files.exists(PUML),
                "No se encuentra el archivo UML en: " + PUML.toAbsolutePath());
        assertTrue(Files.readString(PUML).contains("ValidadorPedido"),
                "El PUML no contiene la clase abstracta ValidadorPedido");
    }

    @Test
    void pumlContieneTodasLasClasesEInterfaces() throws IOException {
        String c = Files.readString(PUML);
        for (String nombre : List.of(
                "ValidadorPedido", "ValidadorStock", "ValidadorAntifraude",
                "ValidadorCredito", "ValidadorDireccion", "ValidadorCupon",
                "Pedido", "LineaPedido", "ResultadoValidacion", "ProcesadorPedidos",
                "Inventario", "ServicioAntifraude", "CoberturaEntrega", "ServicioCupones")) {
            assertTrue(c.contains(nombre), "El PUML no contiene el tipo: " + nombre);
        }
    }

    @Test
    void pumlDeclara5ValidadoresConcretos() throws IOException {
        String c = Files.readString(PUML);
        Matcher m = Pattern.compile("^\\s*class\\s+Validador\\w+\\s+<<ConcreteHandler>>", Pattern.MULTILINE).matcher(c);
        assertEquals(5, m.results().count(),
                "Se esperan 5 ConcreteHandlers: Stock, Antifraude, Credito, Direccion, Cupon");
    }

    @Test
    void pumlEstereotipaLosRolesDelPatron() throws IOException {
        String c = Files.readString(PUML);
        assertTrue(Pattern.compile("^\\s*abstract class ValidadorPedido\\s+<<Handler>>", Pattern.MULTILINE).matcher(c).find(),
                "ValidadorPedido debe tener el estereotipo <<Handler>>");
        assertTrue(Pattern.compile("^\\s*class ProcesadorPedidos\\s+<<Client>>", Pattern.MULTILINE).matcher(c).find(),
                "ProcesadorPedidos debe tener el estereotipo <<Client>>");
        assertTrue(Pattern.compile("^\\s*class Pedido\\s+<<Solicitud>>", Pattern.MULTILINE).matcher(c).find(),
                "Pedido debe tener el estereotipo <<Solicitud>>");
    }

    @Test
    void pumlDeclaraValidadorPedidoAbstracto() throws IOException {
        assertTrue(Files.readString(PUML).contains("abstract class ValidadorPedido"),
                "ValidadorPedido debe estar declarado como 'abstract class' en el PUML");
    }

    @Test
    void pumlDeclaraInterfacesDeInfraestructura() throws IOException {
        String c = Files.readString(PUML);
        for (String i : List.of("Inventario", "ServicioAntifraude", "CoberturaEntrega", "ServicioCupones"))
            assertTrue(c.contains("interface " + i), "El PUML no declara '" + i + "' como interface");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 2. Las clases/interfaces existen en el paquete correcto
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void todasLasClasesExistenEnElPaquete() {
        for (String nombre : List.of(
                "ValidadorPedido", "ValidadorStock", "ValidadorAntifraude",
                "ValidadorCredito", "ValidadorDireccion", "ValidadorCupon",
                "Pedido", "LineaPedido", "ResultadoValidacion", "ProcesadorPedidos",
                "Inventario", "ServicioAntifraude", "CoberturaEntrega", "ServicioCupones"))
            assertDoesNotThrow(() -> cargar(nombre), "Tipo no encontrado en el paquete: " + nombre);
    }

    /** Toda clase del codigo debe estar en el diagrama; Demo es el arranque y no es parte del diseno. */
    @Test
    void todasLasClasesDelCodigoEstanEnElPuml() throws IOException {
        String puml = Files.readString(PUML);
        Path fuentes = Paths.get("src/main/java/" + PAQUETE.replace('.', '/'));
        try (var archivos = Files.list(fuentes)) {
            List<String> ausentes = archivos
                    .map(p -> p.getFileName().toString())
                    .filter(n -> n.endsWith(".java") && !n.equals("package-info.java") && !n.equals("Demo.java"))
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
    void validadorPedidoEsAbstracto() throws ClassNotFoundException {
        assertTrue(Modifier.isAbstract(cargar("ValidadorPedido").getModifiers()),
                "ValidadorPedido debe ser abstracta");
    }

    @Test
    void interfacesDeInfraestructuraSonInterfaces() throws ClassNotFoundException {
        for (String i : List.of("Inventario", "ServicioAntifraude", "CoberturaEntrega", "ServicioCupones"))
            assertTrue(cargar(i).isInterface(), i + " debe ser interface");
    }

    @Test
    void validadoresConcretos_ExtienenValidadorPedido() throws ClassNotFoundException {
        Class<?> handler = cargar("ValidadorPedido");
        for (String nombre : List.of(
                "ValidadorStock", "ValidadorAntifraude", "ValidadorCredito",
                "ValidadorDireccion", "ValidadorCupon"))
            assertTrue(handler.isAssignableFrom(cargar(nombre)),
                    nombre + " debe extender ValidadorPedido");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 4. ValidadorPedido (Handler): campo -siguiente y metodos +/# del UML
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void validadorPedido_CampoPrivado_siguiente() throws Exception {
        Field f = cargar("ValidadorPedido").getDeclaredField("siguiente");
        assertTrue(Modifier.isPrivate(f.getModifiers()), "siguiente debe ser private (UML -)");
        assertEquals(cargar("ValidadorPedido"), f.getType());
    }

    @Test
    void validadorPedido_setSiguiente_publicoDevuelveValidadorPedido() throws Exception {
        Method m = cargar("ValidadorPedido").getMethod("setSiguiente", cargar("ValidadorPedido"));
        assertTrue(Modifier.isPublic(m.getModifiers()), "setSiguiente debe ser public (UML +)");
        assertEquals(cargar("ValidadorPedido"), m.getReturnType());
    }

    @Test
    void validadorPedido_validar_publicoDevuelveResultadoValidacion() throws Exception {
        Method m = cargar("ValidadorPedido").getMethod("validar", cargar("Pedido"));
        assertTrue(Modifier.isPublic(m.getModifiers()), "validar debe ser public (UML +)");
        assertEquals(cargar("ResultadoValidacion"), m.getReturnType());
    }

    @Test
    void validadorPedido_validarPropio_protegidoAbstracto() throws Exception {
        Method m = cargar("ValidadorPedido").getDeclaredMethod("validarPropio", cargar("Pedido"));
        assertTrue(Modifier.isAbstract(m.getModifiers()),  "validarPropio debe ser abstract");
        assertTrue(Modifier.isProtected(m.getModifiers()), "validarPropio debe ser protected (UML #)");
        assertEquals(cargar("ResultadoValidacion"), m.getReturnType());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 5. Handlers concretos: campo de dependencia privado del UML
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void validadorStock_CampoPrivado_inventario() throws Exception {
        Field f = cargar("ValidadorStock").getDeclaredField("inventario");
        assertTrue(Modifier.isPrivate(f.getModifiers()), "inventario debe ser private (UML -)");
        assertEquals(cargar("Inventario"), f.getType());
    }

    @Test
    void validadorAntifraude_CampoPrivado_antifraude() throws Exception {
        Field f = cargar("ValidadorAntifraude").getDeclaredField("antifraude");
        assertTrue(Modifier.isPrivate(f.getModifiers()), "antifraude debe ser private (UML -)");
        assertEquals(cargar("ServicioAntifraude"), f.getType());
    }

    @Test
    void validadorDireccion_CampoPrivado_cobertura() throws Exception {
        Field f = cargar("ValidadorDireccion").getDeclaredField("cobertura");
        assertTrue(Modifier.isPrivate(f.getModifiers()), "cobertura debe ser private (UML -)");
        assertEquals(cargar("CoberturaEntrega"), f.getType());
    }

    @Test
    void validadorCupon_CampoPrivado_cupones() throws Exception {
        Field f = cargar("ValidadorCupon").getDeclaredField("cupones");
        assertTrue(Modifier.isPrivate(f.getModifiers()), "cupones debe ser private (UML -)");
        assertEquals(cargar("ServicioCupones"), f.getType());
    }

    @Test
    void validadorCredito_NoDependeDeServicioExterno() throws ClassNotFoundException {
        Field[] campos = cargar("ValidadorCredito").getDeclaredFields();
        assertEquals(0, campos.length,
                "ValidadorCredito no debe tener campos propios: solo usa datos del Pedido (sin dependencias externas)");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 6. ProcesadorPedidos (Client): campo privado y metodo publico del UML
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void procesadorPedidos_CampoPrivado_primerValidador() throws Exception {
        Field f = cargar("ProcesadorPedidos").getDeclaredField("primerValidador");
        assertTrue(Modifier.isPrivate(f.getModifiers()), "primerValidador debe ser private (UML -)");
        assertEquals(cargar("ValidadorPedido"), f.getType());
    }

    @Test
    void procesadorPedidos_procesar_publicoDevuelveResultadoValidacion() throws Exception {
        Method m = cargar("ProcesadorPedidos").getMethod("procesar", cargar("Pedido"));
        assertTrue(Modifier.isPublic(m.getModifiers()), "procesar debe ser public (UML +)");
        assertEquals(cargar("ResultadoValidacion"), m.getReturnType());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 7. Pedido (Solicitud): 7 atributos del UML con visibilidad publica
    //    UML "+" → Java: campo private + getter publico (convencion Java)
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void pedido_AtributoProductos_campoYGetterPublico() throws Exception {
        Class<?> c = cargar("Pedido");
        Field f = c.getDeclaredField("productos");
        assertTrue(Modifier.isPrivate(f.getModifiers()));
        Method g = c.getMethod("getProductos");
        assertEquals(List.class, g.getReturnType());
        assertTrue(Modifier.isPublic(g.getModifiers()), "getProductos debe ser public (UML +productos)");
    }

    @Test
    void pedido_AtributoTotal_campoYGetterPublico() throws Exception {
        Class<?> c = cargar("Pedido");
        c.getDeclaredField("total");
        Method g = c.getMethod("getTotal");
        assertEquals(BigDecimal.class, g.getReturnType());
        assertTrue(Modifier.isPublic(g.getModifiers()), "getTotal debe ser public (UML +total)");
    }

    @Test
    void pedido_AtributoPagaConCredito_campoYGetterPublico() throws Exception {
        Class<?> c = cargar("Pedido");
        c.getDeclaredField("pagaConCredito");
        Method g = c.getMethod("isPagaConCredito");
        assertEquals(boolean.class, g.getReturnType());
        assertTrue(Modifier.isPublic(g.getModifiers()), "isPagaConCredito debe ser public (UML +pagaConCredito)");
    }

    @Test
    void pedido_AtributoClienteCorporativo_campoYGetterPublico() throws Exception {
        Class<?> c = cargar("Pedido");
        c.getDeclaredField("clienteCorporativo");
        Method g = c.getMethod("isClienteCorporativo");
        assertEquals(boolean.class, g.getReturnType());
        assertTrue(Modifier.isPublic(g.getModifiers()), "isClienteCorporativo debe ser public (UML +clienteCorporativo)");
    }

    @Test
    void pedido_AtributoCupoDisponible_campoYGetterPublico() throws Exception {
        Class<?> c = cargar("Pedido");
        c.getDeclaredField("cupoDisponible");
        Method g = c.getMethod("getCupoDisponible");
        assertEquals(BigDecimal.class, g.getReturnType());
        assertTrue(Modifier.isPublic(g.getModifiers()), "getCupoDisponible debe ser public (UML +cupoDisponible)");
    }

    @Test
    void pedido_AtributoDireccion_campoYGetterPublico() throws Exception {
        Class<?> c = cargar("Pedido");
        c.getDeclaredField("direccion");
        Method g = c.getMethod("getDireccion");
        assertEquals(String.class, g.getReturnType());
        assertTrue(Modifier.isPublic(g.getModifiers()), "getDireccion debe ser public (UML +direccion)");
    }

    @Test
    void pedido_AtributoCupon_campoYGetterPublico() throws Exception {
        Class<?> c = cargar("Pedido");
        c.getDeclaredField("cupon");
        Method g = c.getMethod("getCupon");
        assertEquals(String.class, g.getReturnType());
        assertTrue(Modifier.isPublic(g.getModifiers()), "getCupon debe ser public (UML +cupon)");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 8. LineaPedido: 2 atributos del UML con visibilidad publica
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void lineaPedido_AtributoProductoId_campoYGetterPublico() throws Exception {
        Class<?> c = cargar("LineaPedido");
        c.getDeclaredField("productoId");
        Method g = c.getMethod("getProductoId");
        assertEquals(String.class, g.getReturnType());
        assertTrue(Modifier.isPublic(g.getModifiers()), "getProductoId debe ser public (UML +productoId)");
    }

    @Test
    void lineaPedido_AtributoCantidad_campoYGetterPublico() throws Exception {
        Class<?> c = cargar("LineaPedido");
        c.getDeclaredField("cantidad");
        Method g = c.getMethod("getCantidad");
        assertEquals(int.class, g.getReturnType());
        assertTrue(Modifier.isPublic(g.getModifiers()), "getCantidad debe ser public (UML +cantidad)");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 9. ResultadoValidacion: 2 atributos + 2 fabricas estaticas del UML
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void resultadoValidacion_AtributoAprobado_campoYGetterPublico() throws Exception {
        Class<?> c = cargar("ResultadoValidacion");
        c.getDeclaredField("aprobado");
        Method g = c.getMethod("isAprobado");
        assertEquals(boolean.class, g.getReturnType());
        assertTrue(Modifier.isPublic(g.getModifiers()), "isAprobado debe ser public (UML +aprobado)");
    }

    @Test
    void resultadoValidacion_AtributoMotivo_campoYGetterPublico() throws Exception {
        Class<?> c = cargar("ResultadoValidacion");
        c.getDeclaredField("motivo");
        Method g = c.getMethod("getMotivo");
        assertEquals(String.class, g.getReturnType());
        assertTrue(Modifier.isPublic(g.getModifiers()), "getMotivo debe ser public (UML +motivo)");
    }

    @Test
    void resultadoValidacion_FabricaAprobado_estaticaPublica() throws Exception {
        Class<?> c = cargar("ResultadoValidacion");
        Method m = c.getMethod("aprobado");
        assertTrue(Modifier.isStatic(m.getModifiers()), "aprobado() debe ser static (UML +aprobado())");
        assertTrue(Modifier.isPublic(m.getModifiers()));
        assertEquals(c, m.getReturnType());
    }

    @Test
    void resultadoValidacion_FabricaRechazado_estaticaPublica() throws Exception {
        Class<?> c = cargar("ResultadoValidacion");
        Method m = c.getMethod("rechazado", String.class);
        assertTrue(Modifier.isStatic(m.getModifiers()), "rechazado(String) debe ser static (UML +rechazado())");
        assertTrue(Modifier.isPublic(m.getModifiers()));
        assertEquals(c, m.getReturnType());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 10. Interfaces de infraestructura: firmas exactas del UML
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void inventario_hayStock_recibePedidoDevuelveBoolean() throws Exception {
        Method m = cargar("Inventario").getMethod("hayStock", cargar("Pedido"));
        assertEquals(boolean.class, m.getReturnType());
    }

    @Test
    void servicioAntifraude_esSospechoso_recibePedidoDevuelveBoolean() throws Exception {
        Method m = cargar("ServicioAntifraude").getMethod("esSospechoso", cargar("Pedido"));
        assertEquals(boolean.class, m.getReturnType());
    }

    @Test
    void coberturaEntrega_atiende_recibeDireccionDevuelveBoolean() throws Exception {
        Method m = cargar("CoberturaEntrega").getMethod("atiende", String.class);
        assertEquals(boolean.class, m.getReturnType());
    }

    @Test
    void servicioCupones_esValido_recibePedidoDevuelveBoolean() throws Exception {
        Method m = cargar("ServicioCupones").getMethod("esValido", cargar("Pedido"));
        assertEquals(boolean.class, m.getReturnType());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Comparacion generica: cada miembro del PUML contra el codigo
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
                if (op.matches()) {
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
            Class<?> clase = cargar(nombre);
            for (Method m : clase.getDeclaredMethods()) {
                int mod = m.getModifiers();
                boolean exenta = m.isSynthetic() || Modifier.isPrivate(mod) || Modifier.isStatic(mod)
                        || (m.getParameterCount() == 0 && m.getName().matches("^(get|is)[A-Z].*"))
                        || sobrescribe(m);
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
            Class<?> clase = cargar(nombre);
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
