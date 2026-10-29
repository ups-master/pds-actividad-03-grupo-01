# Trazabilidad UML-código: escenario 1 (Chain of Responsibility)

Dirección del diseño: **recomendación de la IA aceptada (informe, sección 8) → diagrama mejorado → código**. El
diagrama mejorado es el contrato del código y está verificado. `CorrespondenciaUmlCodigoTest` lee
[`chainE1_mejorado.puml`](../uml/chainE1_mejorado.puml) y, por reflexión, comprueba que cada clase, atributo y
operación del diagrama existe en el código con su visibilidad y tipos, y que ningún atributo ni operación propios
del código falta en el diagrama. Un cambio de diseño empieza en el `.puml`; después se ajusta el código, la
imagen y este documento. Quedan fuera del contrato los detalles de implementación: constructores, el
`Pedido.Builder`, las fábricas estáticas, los métodos privados no declarados, `Demo` y las pruebas.

| Diagrama | Fuente | Imagen | Función |
|---|---|---|---|
| Original | [`chainE1.puml`](../uml/chainE1.puml) | [`diagrama-clases-original.png`](../uml/diagrama-clases-original.png) | Propuesta inicial; se conserva para mostrar el cambio de diseño. |
| Mejorado | [`chainE1_mejorado.puml`](../uml/chainE1_mejorado.puml) | [`diagrama-clases-mejorado.png`](../uml/diagrama-clases-mejorado.png) | Contrato implementado. |
| Mejorado (archify) | Mismo diseño | [`diagrama-clases-mejorado.html`](../uml/diagrama-clases-mejorado.html) (interactivo) y [`diagrama-clases-mejorado-archify.png`](../uml/diagrama-clases-mejorado-archify.png) | Vistas derivadas generadas con archify; no son el contrato, lo es el `.puml`. Si el diseño cambia hay que regenerarlas. |
| Secuencia | [`chainE1_secuencia.puml`](../uml/chainE1_secuencia.puml) | No solicitada | Pedido corporativo rechazado por cupón; se ejecuta en `Demo` (pedido 8) y en `ProcesadorPedidosTest`. |

## Cómo leer el diagrama mejorado

Cada rol GoF tiene un estereotipo y un color (ver la leyenda del diagrama) y las clases se agrupan en paquetes
con los mismos nombres que la tabla de participantes del informe:

| Paquete del diagrama | Contenido |
|---|---|
| Roles del patrón (GoF) | `ProcesadorPedidos` (Client), `ValidadorPedido` (Handler), los cinco `Validador*` (ConcreteHandler) y `Pedido` (Solicitud) |
| Datos de la solicitud y del resultado (no son roles GoF) | `LineaPedido`, `ResultadoValidacion` |
| Dependencias de las reglas (no son roles GoF) | `Inventario`, `ServicioAntifraude`, `CoberturaEntrega`, `ServicioCupones` |

## Mejora aceptada → diagrama mejorado → código

| # | Mejora que propuso la IA y se aceptó | En el diagrama mejorado | En el código |
|---|---|---|---|
| 1 | Regla propia por eslabón | `# validarPropio(pedido)` en `ValidadorPedido` y en cada validador concreto | `ValidadorPedido.validarPropio` (abstracto) y su redefinición en cada validador |
| 2 | Resultado al final y sin ciclos | Nota de `ValidadorPedido`; `setSiguiente` | `validar` aprueba si no hay siguiente; `setSiguiente` lanza `IllegalArgumentException` |
| 3 | Servicios externos como interfaces | `Inventario`, `ServicioAntifraude`, `CoberturaEntrega`, `ServicioCupones` y sus atributos privados | Las cuatro interfaces y los campos `inventario`, `antifraude`, `cobertura`, `cupones` |
| 4 | Eslabón opcional de cupones | `ValidadorCupon` y su nota | `ValidadorCupon` |
| 5 | Solicitud y resultado completos | `Pedido` con siete atributos, `LineaPedido` (composición `1` a `1..*`), fábricas de `ResultadoValidacion` | `Pedido`, `LineaPedido`, `ResultadoValidacion.aprobado()` / `rechazado(motivo)` |
| 6 | Excepción corporativa dentro del antifraude | Nota de `ValidadorAntifraude` | `ValidadorAntifraude.validarPropio` |

## Elemento del UML, clase Java y prueba

Todas las clases están en `codigo/src/main/java/ec/edu/ups/pds/chain/`. Las pruebas están en
`codigo/src/test/java/ec/edu/ups/pds/chain/`: `CorrespondenciaUmlCodigoTest` (C) y `ProcesadorPedidosTest` (P).

| Rol / elemento del UML | Clase Java | Qué verifica |
|---|---|---|
| Handler `ValidadorPedido` | `ValidadorPedido` (abstracta) | C: abstracta, `siguiente` privado, `setSiguiente`, `validar`, `validarPropio` protegido y abstracto. P: detención en el primer fallo, aprobación al final, encadenamiento fluido, autoenlace y ciclo rechazados, reemplazo del enlace. |
| ConcreteHandler `ValidadorStock` | `ValidadorStock` | C: campo `inventario`. P: rechazo sin stock. |
| ConcreteHandler `ValidadorAntifraude` | `ValidadorAntifraude` | C: campo `antifraude`. P: rechazo por sospecha; el corporativo no consulta el servicio. |
| ConcreteHandler `ValidadorCredito` | `ValidadorCredito` | C: no depende de un servicio externo. P: rechazo por cupo y aprobación dentro del cupo. |
| ConcreteHandler `ValidadorDireccion` | `ValidadorDireccion` | C: campo `cobertura`. P: rechazo sin cobertura. |
| ConcreteHandler `ValidadorCupon` (opcional) | `ValidadorCupon` | C: campo `cupones`. P: cupón válido, inválido y pedido sin cupón. |
| Client `ProcesadorPedidos` | `ProcesadorPedidos` | C: campo `primerValidador` y `procesar`. P: toda la cadena se ejecuta a través del procesador; cadena de un solo eslabón. |
| Solicitud `Pedido`, `LineaPedido` | `Pedido` (con `Builder`), `LineaPedido` | C: atributos privados con getter público. P: validaciones del armado y copia defensiva de los productos. |
| `ResultadoValidacion` | `ResultadoValidacion` | C: atributos y fábricas `aprobado()` / `rechazado(motivo)`. P: motivo del rechazo. |
| Interfaces `Inventario`, `ServicioAntifraude`, `CoberturaEntrega`, `ServicioCupones` | las mismas interfaces | C: firma de cada operación. P: dobles de prueba. |
| Asociación `siguiente` (0..1 a 0..1) | campo privado `ValidadorPedido.siguiente` | P: orden configurable y recorrido de la secuencia (corporativo con cupón inválido). |

## Reglas de comportamiento implementadas

- `validar` se detiene al primer rechazo, delega ante aprobación y aprueba si no hay siguiente.
- `ValidadorAntifraude` omite la consulta externa para clientes corporativos.
- `ValidadorCredito` solo aplica si se paga con crédito; `ValidadorCupon` solo si el pedido trae cupón.
- El orden de los eslabones lo define quien arma la cadena; el procesador no cambia.
- `setSiguiente` rechaza `null` (devolverlo rompía el encadenado fluido), autoenlaces y ciclos; una cadena termina
  al no enlazar más y una cola ya armada puede compartirse entre cadenas.
- `Pedido.Builder` rechaza productos nulos y total o cupo negativos, además de los datos obligatorios;
  `ProcesadorPedidos` rechaza un primer validador o un pedido nulos con un mensaje claro.

## Verificación

```text
mvn test -pl escenario-1-chain-of-responsibility/codigo
```

Resultado: 69 pruebas, 0 fallos (42 de correspondencia UML-código y 27 funcionales).
