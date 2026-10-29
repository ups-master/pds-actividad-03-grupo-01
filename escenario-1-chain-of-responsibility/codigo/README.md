# Manual de uso - Escenario 1 (Chain of Responsibility)

Cómo compilar, probar y ejecutar el código del escenario 1. Todos los comandos se lanzan desde esta
carpeta (`escenario-1-chain-of-responsibility/codigo`), salvo que se indique la raíz del repositorio.

## 1. Requisitos

| Herramienta | Versión | Para qué se usa |
|---|---|---|
| JDK | 21 o superior | Compilar y ejecutar |
| Maven | 3.9 o posterior | Ejecutar las pruebas con `mvn test` |

## 2. Estructura del código

```text
codigo/
├── pom.xml
└── src/
    ├── main/java/ec/edu/ups/pds/chain/
    │   ├── ValidadorPedido.java          Handler: guarda siguiente, validar() y validarPropio()
    │   ├── ValidadorStock.java           ConcreteHandler
    │   ├── ValidadorAntifraude.java      ConcreteHandler (omite a clientes corporativos)
    │   ├── ValidadorCredito.java         ConcreteHandler (solo si paga con crédito)
    │   ├── ValidadorDireccion.java       ConcreteHandler
    │   ├── ValidadorCupon.java           ConcreteHandler opcional (solo si hay cupón)
    │   ├── ProcesadorPedidos.java        Client: conoce solo el primer validador
    │   ├── Pedido.java, LineaPedido.java Solicitud
    │   ├── ResultadoValidacion.java      Aprobado o rechazado con motivo
    │   ├── Inventario.java, ServicioAntifraude.java,
    │   │   CoberturaEntrega.java, ServicioCupones.java   Dependencias de las reglas
    │   ├── Demo.java                     Arma las cadenas y muestra diez pedidos
    │   └── package-info.java             Roles del patrón
    └── test/java/ec/edu/ups/pds/chain/
        ├── CorrespondenciaUmlCodigoTest.java
        └── ProcesadorPedidosTest.java
```

## 3. Pruebas

```text
mvn test
```

Desde la raíz del repositorio: `mvn test -pl escenario-1-chain-of-responsibility/codigo`. Resultado esperado:
**69 pruebas, 0 fallos**.

| Clase de prueba | Pruebas | Qué verifica |
|---|---|---|
| `CorrespondenciaUmlCodigoTest` | 42 | Lee `../uml/chainE1_mejorado.puml` y lo compara con las clases por reflexión: tipos, jerarquía, campos, visibilidad y firmas; además, cada miembro del `.puml` existe en el código y ninguna clase, operación ni atributo propios del código falta en el diagrama. |
| `ProcesadorPedidosTest` | 27 | Aprobación completa; rechazo en cada eslabón (stock, antifraude, crédito, dirección, cupón); el corporativo no consulta antifraude; la cadena se detiene en el primer fallo; reordenar sin modificar validadores; insertar cupones; la cadena de la secuencia UML; cadena de un eslabón; autoenlace, ciclo y `null` rechazados; validaciones del pedido (productos nulos, total y cupo negativos) y del procesador (validador o pedido nulos). |

## 4. Ejecución

```text
mvn -q compile exec:java
```

Desde la raíz: `mvn -q compile exec:java -pl escenario-1-chain-of-responsibility/codigo`. También:
`java -cp target/classes ec.edu.ups.pds.chain.Demo` tras compilar.

La demo (`Demo`) arma las cadenas y procesa diez pedidos, mostrando una línea por validador consultado:
aprobado completo, rechazos por stock, antifraude, crédito y dirección, crédito dentro del cupo, un cliente
corporativo (no aparece la línea `[Antifraude]`), el caso del diagrama de secuencia (cadena de cinco eslabones,
rechazo por cupón) y dos casos de *Black Friday* con el cupón insertado.

## 5. Cómo agregar un eslabón

1. Crear una clase que extienda `ValidadorPedido` e implemente `validarPropio(Pedido)`, devolviendo
   `ResultadoValidacion.aprobado()` o `ResultadoValidacion.rechazado("motivo")`. Si depende de un servicio
   externo, definirlo como interfaz y recibirlo en el constructor.
2. Enlazarlo donde se arma la cadena: `stock.setSiguiente(nuevo).setSiguiente(direccion)`.
3. Ningún otro validador ni `ProcesadorPedidos` cambia. Añadir la clase y sus operaciones al
   `chainE1_mejorado.puml` y a la trazabilidad, o `CorrespondenciaUmlCodigoTest` fallará.

El orden se define solo al enlazar. `setSiguiente` lanza `IllegalArgumentException` si el enlace crearía un
autoenlace o un ciclo.
