# Manual de uso - Escenario 2 (Mediator)

Cómo compilar, probar y ejecutar el código del escenario 2. Todos los comandos se lanzan desde esta
carpeta (`escenario-2-mediator/codigo`), salvo que se indique la raíz del repositorio.

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
    ├── main/java/ec/edu/ups/pds/mediator/
    │   ├── MediadorCocina.java       Mediator: registrar() y notificar()
    │   ├── JefeDeCocina.java         ConcreteMediator: reparto por estación, estado de mesas, reglas
    │   ├── ComponenteCocina.java     Colleague: conoce solo al mediador
    │   ├── Mesero.java               ConcreteColleague: registra pedidos, sirve y pide la cuenta
    │   ├── EstacionCaliente.java     ConcreteColleague: prepara los platos de su menú
    │   ├── EstacionFria.java         ConcreteColleague: prepara los platos de su menú
    │   ├── Caja.java                 ConcreteColleague: retira platos y emite la cuenta
    │   ├── EventoCocina.java, TipoEvento.java   Mensaje que viaja al mediador
    │   ├── PedidoMesa.java           Pedido de una mesa
    │   ├── EstadoMesa.java           Platos pendientes, listos, servidos y retirados
    │   ├── Main.java                 Client: arma el mediador y muestra tres escenarios
    │   └── package-info.java         Roles del patrón
    └── test/java/ec/edu/ups/pds/mediator/
        ├── CorrespondenciaUmlCodigoTest.java
        ├── JefeDeCocinaTest.java
        ├── EstadoMesaTest.java
        └── PedidoMesaTest.java
```

## 3. Pruebas

```text
mvn test
```

Desde la raíz del repositorio: `mvn test -pl escenario-2-mediator/codigo`. Resultado esperado:
**76 pruebas, 0 fallos**.

| Clase de prueba | Pruebas | Qué verifica |
|---|---|---|
| `CorrespondenciaUmlCodigoTest` | 45 | Lee `../uml/mediatorE2_mejorado.puml` y lo compara con las clases por reflexión: tipos, jerarquía, campos, visibilidad, firmas y valores del enum; además, cada miembro del `.puml` existe en el código y ninguna clase, operación ni atributo propios del código falta en el diagrama; ningún colega tiene campos de otro colega. |
| `JefeDeCocinaTest` | 21 | Estado de la mesa al registrar un pedido; cada estación recibe solo sus platos; plato sin estación rechazado; mesa lista y servida una sola vez; faltante que retira el plato y avisa a mesero y caja; mesa que no se sirve si quedan pendientes o no queda nada que servir; platos repetidos; cuenta solo con lo servido; segundo pedido de la misma mesa (también cuando se agota por completo); pedido atómico (una estación que falla no deja estado ni altera el anterior); la cuenta se puede volver a pedir; reentrada real. |
| `EstadoMesaTest` | 5 | Los getters son de solo lectura; la copia es independiente; retirar lo nuevo de una mesa ya servida la deja servida; retirar sin haber servido nada no la marca; retirar un plato ajeno no cambia nada. |
| `PedidoMesaTest` | 5 | Rechaza mesa nula o vacía, platos nulos o vacíos y platos nulos o en blanco; la lista de platos es inmutable y copia la original. |

## 4. Ejecución

```text
mvn -q compile exec:java
```

Desde la raíz: `mvn -q compile exec:java -pl escenario-2-mediator/codigo`. También:
`java -cp target/classes ec.edu.ups.pds.mediator.Main` tras compilar.

`Main` muestra tres escenarios: un pedido normal en el que cada estación recibe solo sus platos y la caja cobra lo
servido; el faltante de fritada de la mesa 7, que retira el plato, avisa al mesero y a la caja, sirve el resto y
cobra solo la ensalada (el caso del diagrama de secuencia); y un faltante con otro plato aún pendiente, que no
sirve la mesa hasta que termina.

## 5. Cómo agregar un colega

Ejemplo: una estación de parrilla.

1. Crear una clase que extienda `ComponenteCocina`, con el menú que prepara: sobrescribir `prepara(plato)`,
   `atiende(tipo)` (`PEDIDO_REGISTRADO`) y `recibir(evento)`. Para avisar, usar `informar(evento)`.
2. Registrarla en el punto de arranque: `jefe.registrar(parrilla)`.
3. Ni `JefeDeCocina` ni los demás colegas cambian: el reparto usa `prepara` y `atiende`, no clases concretas. Si
   la parrilla necesita reglas nuevas (por ejemplo, un tipo de evento propio), se agregan en `TipoEvento` y en
   `JefeDeCocina`.
4. Añadir la clase al `mediatorE2_mejorado.puml` y a la trazabilidad, o `CorrespondenciaUmlCodigoTest` fallará.

Los repartidores de *delivery* se agregan igual: un colega que atiende `MESA_LISTA`.
