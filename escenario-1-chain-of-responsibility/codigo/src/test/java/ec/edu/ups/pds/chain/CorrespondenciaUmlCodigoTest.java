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
        Matcher m = Pattern.compile("^class\\s+Validador\\w+", Pattern.MULTILINE).matcher(c);
        assertEquals(5, m.results().count(),
                "Se esperan 5 ConcreteHandlers: Stock, Antifraude, Credito, Direccion, Cupon");
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
    // Utilidad
    // ═══════════════════════════════════════════════════════════════════════

    private Class<?> cargar(String nombreSimple) throws ClassNotFoundException {
        return Class.forName(PAQUETE + "." + nombreSimple);
    }
}
