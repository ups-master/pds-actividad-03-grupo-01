# Enunciado de la actividad 03 - Patrones de comportamiento

## Datos de la actividad

| | |
|---|---|
| **Asignatura** | Patrones de Diseño de Software |
| **Actividad de aprendizaje #** | 03 - Patrones de comportamiento |
| **Docente** | Mauricio Ortiz Ochoa |
| **Fecha** | 2026/10/29 |
| **Grupo base** | Grupo 01 |
| **Resultado de aprendizaje** | RA3. Implementa soluciones aplicando patrones de comportamiento para organizar responsabilidades e interacciones entre objetos. |

**Entregables:** archivo PDF `PDS_Actividad_03_Grupo01.pdf`; aplicación de software almacenada en un
repositorio de código. Aunque la actividad se realiza en grupo, la carga al AVAC es individual.

## Escenario 1 (Chain of Responsibility): validación de pedidos en una tienda en línea

### 1. Escenario / Motivación

*Andes Market* es una tienda en línea ficticia que vende productos de tecnología y hogar en Ecuador y
procesa cientos de pedidos al día. Antes de confirmar un pedido y enviarlo a bodega para su despacho, la
tienda debe asegurarse de que el pedido sea viable. Para ello lo somete a una serie de validaciones, en
este orden:

1. **Stock:** todos los productos del pedido tienen existencias suficientes.
2. **Antifraude:** el pedido no presenta señales de fraude (monto inusualmente alto para el cliente,
   demasiados intentos de pago seguidos, país de la tarjeta distinto al de envío).
3. **Límite de crédito:** si el cliente paga con el crédito de la tienda, el total no supera su cupo
   disponible.
4. **Dirección de envío:** la dirección está completa y pertenece a una zona con cobertura de entrega.

Si una validación falla, el pedido se rechaza indicando el motivo y las validaciones restantes ya no se
ejecutan: por ejemplo, si no hay stock no tiene sentido consultar el servicio antifraude. Si el pedido
llega al final sin que ninguna validación lo rechace, se considera aprobado y pasa a despacho.

Las reglas del negocio cambian con frecuencia: en temporadas como *Black Friday* el área comercial quiere
agregar una nueva validación de **cupones y promociones** (que el cupón exista, esté vigente y aplique a
los productos del pedido); el área de riesgos pide que los clientes corporativos
no pasen por antifraude; y el equipo de operaciones quiere reordenar las validaciones para que las más
baratas (stock, dirección) se ejecuten antes que las costosas (antifraude, que consulta un servicio
externo). La tienda necesita poder hacer estos cambios sin reescribir el proceso de validación cada vez.

### 2. Problema de diseño identificado

La solución inmediata es un único método que concentra todas las validaciones en una secuencia de
condicionales:

```java
public Resultado procesar(Pedido pedido) {
    if (!inventario.hayStock(pedido)) {
        return Resultado.rechazado("Sin stock");
    }
    if (!pedido.getCliente().esCorporativo() && servicioAntifraude.esSospechoso(pedido)) {
        return Resultado.rechazado("Sospecha de fraude");
    }
    if (pedido.pagaConCredito() && pedido.getTotal() > pedido.getCliente().getCupoDisponible()) {
        return Resultado.rechazado("Supera el cupo de credito");
    }
    if (!cobertura.atiende(pedido.getDireccion())) {
        return Resultado.rechazado("Direccion sin cobertura");
    }
    return Resultado.aprobado();
}
```

Este diseño presenta los siguientes problemas:

- **Acoplamiento del cliente con todas las validaciones.** Quien procesa el pedido conoce el inventario, el
  servicio antifraude, el crédito y la cobertura, además del orden en que deben consultarse.
- **Violación del principio abierto/cerrado.** Agregar la validación de cupones obliga a modificar un
  método que ya funciona y está probado; lo mismo ocurre al quitar una validación.
- **Orden fijo en el código.** Reordenar las validaciones (las baratas antes que las costosas) implica
  mover bloques de código y volver a compilar, en lugar de recombinar piezas.
- **Responsabilidades mezcladas.** Una sola clase decide sobre stock, fraude, crédito y logística; las
  excepciones de negocio (clientes corporativos sin antifraude) se suman como condiciones adicionales y el
  método crece con cada regla nueva.
- **Pruebas difíciles.** No se puede probar una validación de forma aislada: cada prueba debe preparar un
  pedido que supere todas las validaciones anteriores.

En síntesis, el problema es que **el emisor de la solicitud (el pedido) está acoplado a todos sus posibles
receptores (las validaciones) y a la secuencia en que intervienen**, cuando lo que se necesita es poder
agregar, quitar y reordenar validaciones sin tocar a quien procesa el pedido.

### 3. Justificación del patrón

Se elige **Chain of Responsibility**, cuya intención, según el libro de patrones de la *Gang of Four*, es
*evitar acoplar el emisor de una solicitud con su receptor, encadenando a varios objetos que pueden
atenderla*. Encaja con el
escenario porque varias reglas actúan sobre el mismo pedido, quien lo procesa no debería conocerlas y el
conjunto de reglas debe poder cambiarse.

| Rol del patrón | Elemento del escenario |
|---|---|
| Solicitud | `Pedido` |
| Manejador (Handler) | `ValidadorPedido`: clase abstracta que guarda su `siguiente` y decide si continúa |
| Manejadores concretos (ConcreteHandler) | `ValidadorStock`, `ValidadorAntifraude`, `ValidadorCredito`, `ValidadorDireccion` y, como extensión, `ValidadorCupon` |
| Cliente (Client) | `ProcesadorPedidos`: recibe solo el primer eslabón (la cadena la arma quien configura) y le envía el pedido |

**Cómo resuelve los problemas identificados:** el procesador conoce solo el primer eslabón y cada
validador solo a su `siguiente`; agregar cupones es crear `ValidadorCupon` e insertarlo, sin modificar las
validaciones existentes; el orden se define al armar la cadena; cada validador encapsula una sola regla
(la excepción de clientes corporativos queda dentro de `ValidadorAntifraude`), y cada uno puede probarse de
forma aislada.

**Comportamiento de la cadena:** si un validador rechaza el pedido, la cadena se detiene y devuelve el
motivo; si lo acepta, delega en el siguiente; si el pedido llega al final sin rechazos, queda aprobado.

**Análisis crítico.** A diferencia de la forma clásica, donde la cadena termina cuando *un* eslabón atiende
la solicitud, aquí se usa la variante de **cadena de filtros** (como los filtros de servlets en Java):
cualquier eslabón puede detenerla y el resultado por defecto es la aprobación. Se descartó *Strategy*
porque elige un solo algoritmo, no varias reglas en secuencia, y *Decorator* porque añade comportamiento y
siempre delega. Un bucle sobre una lista de validadores sería más simple, pero centraliza la decisión de
continuar, que en la cadena queda en cada eslabón. Como costo, el flujo queda repartido en varias clases,
lo que dificulta la depuración, y una cadena mal armada solo se detecta en ejecución, por lo que su armado
debe cubrirse con pruebas.

## Escenario 2 (Mediator): coordinación de pedidos en la cocina de un restaurante

### 1. Escenario / Motivación

*El Fogón Andino* es un restaurante ficticio de comida ecuatoriana que en horas pico atiende varias mesas
a la vez. Cada pedido de una mesa involucra a varios participantes que dependen unos de otros:

- **Mesero:** toma el pedido de la mesa, sirve los platos cuando están listos y solicita la cuenta.
- **Estación caliente:** prepara los platos fuertes y las sopas.
- **Estación fría:** prepara las entradas, ensaladas y postres.
- **Caja:** registra lo consumido por cada mesa y emite la cuenta.

Las acciones de un participante afectan a los demás. Cuando el mesero registra un pedido, cada ítem debe
llegar a la estación que lo prepara. Los platos de una misma mesa deben servirse juntos, así que el mesero
solo debe ser avisado cuando todas las estaciones terminaron su parte. Cuando el mesero pide la cuenta, la
caja necesita saber exactamente qué se sirvió.

El caso más delicado es cuando **una estación se queda sin un insumo**, porque un solo evento afecta a
varios participantes a la vez. Por ejemplo, si la estación caliente se queda sin cerdo para la fritada:

- el **mesero** debe enterarse para informar al cliente y ofrecerle otro plato;
- la **caja** debe retirar la fritada de la cuenta de esa mesa;
- la **estación fría** no debe quedarse esperando a que la fritada esté lista: si el resto de la mesa ya
  está preparado, los platos deben poder servirse sin ella.

El restaurante planea crecer: abrirá una estación de parrilla y empezará a atender pedidos por
*delivery*, con repartidores que también deben enterarse de cuándo un pedido está listo. La operación
debe poder ampliarse sin que cada participante tenga que conocer y comunicarse con todos los demás.

### 2. Problema de diseño identificado

La solución inmediata es que cada participante tenga referencias directas a los demás y les avise por su
cuenta. Por ejemplo, la estación caliente:

```java
public class EstacionCaliente {
    private Mesero mesero;
    private EstacionFria estacionFria;
    private Caja caja;

    public void terminarPlato(Mesa mesa) {
        mesa.marcarListoCaliente();
        if (estacionFria.terminoPlatosDe(mesa)) {
            mesero.servir(mesa);
        }
    }

    public void reportarFaltante(Mesa mesa, String plato) {
        mesero.informarFaltante(mesa, plato);
        caja.retirarItem(mesa, plato);
        if (estacionFria.terminoPlatosDe(mesa)) {
            mesero.servir(mesa);
        }
    }
}
```

La estación fría repite la misma lógica pero preguntando a la estación caliente, y el mesero a su vez
conoce a las dos estaciones y a la caja. Este diseño presenta los siguientes problemas:

- **Acoplamiento de muchos a muchos.** Cada participante conoce a casi todos los demás: con cuatro
  participantes ya hay una red de referencias cruzadas, y cada uno nuevo agrega dependencias en todos.
- **Lógica de coordinación dispersa y duplicada.** La regla "servir la mesa cuando todas las estaciones
  terminaron" vive repetida en cada estación; si cambia, hay que modificarla en varios lugares.
- **Un evento con efectos en cadena difíciles de seguir.** Un faltante obliga a la estación a avisar al
  mesero, a la caja y a revisar a la otra estación; nadie tiene la visión completa de qué ocurre.
- **Extensión costosa.** Agregar la estación de parrilla obliga a modificar a la estación caliente, a la
  fría y al mesero para que la consideren; agregar *delivery* obliga a tocar todas las estaciones.
- **Participantes no reutilizables ni fáciles de probar.** Una estación no puede usarse ni probarse sin
  crear también al mesero, a la otra estación y a la caja.

En síntesis, el problema es que **la comunicación entre los participantes está repartida entre ellos
mismos**: cada uno conoce a los demás y contiene parte de las reglas de coordinación, cuando lo que se
necesita es que cada participante haga solo su trabajo y que la coordinación esté en un único lugar.

### 3. Justificación del patrón

Se elige **Mediator**, cuya intención, según el libro de patrones de la *Gang of Four*, es *encapsular en
un objeto cómo interactúa un conjunto de objetos, evitando que se refieran unos a otros*. Encaja con el
escenario porque los participantes se comunican de forma compleja pero bien definida, y esa comunicación es
lo que cambia cuando el restaurante crece. En las cocinas reales este rol existe: el **jefe de cocina**
reparte los pedidos a las estaciones y decide cuándo una mesa está lista para servirse.

| Rol del patrón | Elemento del escenario |
|---|---|
| Componentes (Colleague y ConcreteColleague) | `ComponenteCocina` y sus subclases `Mesero`, `EstacionCaliente`, `EstacionFria` y `Caja`: cada uno guarda su referencia `mediador` y le notifica sus eventos |
| Mediador (Mediator) | `MediadorCocina`: interfaz con las operaciones `registrar(componente)` y `notificar(emisor, evento)` |
| Mediador concreto (ConcreteMediator) | `JefeDeCocina`: conoce a todos los componentes, lleva el estado de cada mesa y aplica las reglas de coordinación |
| Cliente (Client) | `Main`: arma el mediador y registra los componentes |

**Cómo resuelve los problemas identificados:** cada componente conoce solo al mediador; la regla "servir cuando
todas las estaciones terminaron" vive una sola vez en `JefeDeCocina`; un faltante es un único aviso al
mediador, que decide a quién afecta; agregar la parrilla o el *delivery* es crear el componente nuevo y
modificar solo el mediador concreto, sin tocar a los demás componentes, y cada componente puede probarse
con un mediador simulado.

**Análisis crítico.** Se descartó *Observer* porque dejaría la regla de coordinación repartida entre los
suscriptores, y *Facade* porque su comunicación es en un solo sentido, mientras que aquí los componentes la
inician. Como costo, el mediador concentra la coordinación y conoce a todos los componentes registrados (solo
por su abstracción), por lo que puede crecer demasiado; para evitarlo, `JefeDeCocina` solo coordina y cada componente sigue haciendo su
propio trabajo. Además, debe manejar la reentrada: un componente puede responder a un aviso mientras el
mediador aún reparte el anterior.

## Plantilla original de la actividad

Transcrita de la plantilla oficial `PDS_Actividad_03_GrupoXX.docx` (Maestría en Software, asignatura Patrones
de Diseño de Software, docente Mauricio Ortiz Ochoa).

### Tareas específicas

- Participar en las clases de la Unidad 03, secciones 01 y 02.
- Leer detenidamente el material de apoyo relacionado con los patrones de comportamiento.
- Crear un repositorio de código para almacenar la aplicación solicitada.
- Analizar los escenarios seleccionados e identificar el problema de comportamiento que debe resolverse.
- Seleccionar y justificar los patrones de diseño correspondientes.
- Elaborar los diagramas de clases UML de las soluciones propuestas.
- Implementar las soluciones manteniendo correspondencia entre el modelo UML y el código.
- Utilizar inteligencia artificial como apoyo para revisar, cuestionar y mejorar las decisiones de diseño.
- Documentar al menos una recomendación de la IA que haya sido aceptada, modificada o rechazada,
  justificando la decisión tomada.

### Instrucciones

Cada grupo selecciona **2 patrones de comportamiento** entre Chain of Responsibility, Mediator, Command,
Interpreter, Iterator, Memento y Visitor. No deben ser los patrones desarrollados directamente en clase.

1. **Definición del problema.** Para cada patrón: plantear un escenario realista, describir el contexto,
   identificar las responsabilidades e interacciones que generan el problema de diseño y explicar por qué una
   solución directa (condicionales, dependencias o llamadas explícitas) dificulta la evolución del sistema.
2. **Selección y justificación.** Justificar por qué el patrón es adecuado, identificar sus participantes en el
   dominio, explicar la responsabilidad de cada uno y describir cómo colaboran los objetos.
3. **Modelado UML.** Un diagrama de clases por solución con clases e interfaces, atributos y operaciones
   relevantes, relaciones, navegabilidad cuando corresponda, multiplicidades cuando aporten información y los
   participantes del patrón claramente identificados.
4. **Implementación.** En un lenguaje orientado a objetos, con correspondencia con los diagramas UML, que
   evidencie el patrón, evite dependencias innecesarias y mantenga responsabilidades coherentes.
5. **Validación de la solución.** Para cada patrón explicar qué problema resolvió, qué mejoró respecto de una
   solución directa, qué costos o complejidad adicional introdujo y en qué situación no se recomendaría.
6. **Uso crítico de inteligencia artificial.** La IA puede apoyar el análisis del escenario, el cuestionamiento
   de decisiones, la revisión del UML, la generación o revisión de código y la propuesta de pruebas o
   alternativas. Se documenta, como mínimo, una recomendación aceptada, modificada o rechazada con su decisión.

### Estructura del informe (por patrón)

Escenario / Motivación · Problema de diseño identificado · Justificación del patrón (con alternativas
consideradas) · Participantes del patrón (participante → elemento del dominio) · Diagrama de clases UML ·
Código fuente (repositorio / enlace) · Uso crítico de IA (prompt principal, recomendación aceptada y
justificación, recomendación modificada o rechazada y justificación). Al final, **Conclusiones (200 palabras)**.

### Rúbrica de evaluación

Cada criterio se califica como No presenta (0), Nivel bajo (2), Nivel medio (4) o Nivel alto (6).

| Criterio | 0 | 2 | 4 | 6 |
|---|---|---|---|---|
| Selección, justificación y diagrama UML del Patrón 01 | 0 | 2 | 4 | 6 |
| Implementación funcional y correspondencia UML-código del Patrón 01 | 0 | 2 | 4 | 6 |
| Selección, justificación y diagrama UML del Patrón 02 | 0 | 2 | 4 | 6 |
| Implementación funcional y correspondencia UML-código del Patrón 02 | 0 | 2 | 4 | 6 |
| Uso crítico de IA, síntesis comparativa, justificación y conclusiones | 0 | 2 | 4 | 6 |

### Documentos relacionados

`Unidad_03_01_PDS.pdf` y `Unidad_03_02_PDS.pdf`.
