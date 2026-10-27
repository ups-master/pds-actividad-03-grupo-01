# Escenario 1: validación de pedidos de Andes Market

## Problema y patrón

Antes del despacho, un pedido debe superar stock, antifraude, crédito y dirección. El orden debe poder cambiar y una nueva regla de cupones debe poder insertarse sin reescribir las demás. **Chain of Responsibility**, en su variante de cadena de filtros, permite que cada validador apruebe y delegue, o rechace y detenga el recorrido. La aprobación final ocurre solo si todos los eslabones atravesados aprobaron.

## Punto 3: diagramas UML

| Versión | Fuente PlantUML | Imagen |
|---|---|---|
| Original | [chainE1.puml](uml/chainE1.puml) | [Diagrama original](uml/diagrama-clases-original.png) |
| Mejorada | [chainE1_mejorado.puml](uml/chainE1_mejorado.puml) | [Diagrama mejorado](uml/diagrama-clases-mejorado.png) |
| Secuencia | [chainE1_secuencia.puml](uml/chainE1_secuencia.puml) | Fuente editable; no se solicitó PNG |

![Clases de la propuesta original](uml/diagrama-clases-original.png)

![Clases del diseño mejorado](uml/diagrama-clases-mejorado.png)

La versión original identifica cuatro validadores, el pedido y el cliente, pero deja ambiguo qué ocurre al terminar la cadena y no impide enlaces circulares. La mejorada define el resultado aprobado al llegar al final, separa la regla de cada eslabón en `validarPropio`, prohíbe ciclos al enlazar e incorpora el validador opcional de cupones. Inventario, antifraude, cobertura y cupones aparecen como dependencias mediante interfaces. La exclusión de clientes corporativos se decide dentro de `ValidadorAntifraude`.

El [diagrama de secuencia](uml/chainE1_secuencia.puml) muestra un pedido corporativo que supera stock, omite la consulta antifraude, supera crédito y dirección, y se rechaza por cupón inválido. El rechazo vuelve al procesador con su motivo.

## Punto 4: participantes

| Rol | Elemento UML | Responsabilidad |
|---|---|---|
| Handler | `ValidadorPedido` | Conserva `siguiente`, aplica la regla propia y decide si delega o detiene. |
| ConcreteHandler | `ValidadorStock`, `ValidadorAntifraude`, `ValidadorCredito`, `ValidadorDireccion`, `ValidadorCupon` | Encapsulan una validación cada uno; cupones es opcional. |
| Client | `ProcesadorPedidos` | Conoce solo el primer validador y envía el pedido. |
| Solicitud | `Pedido` | Aporta los datos que recorren la cadena. |
| Resultado | `ResultadoValidacion` | Expresa aprobación o rechazo con motivo. |

`ProcesadorPedidos` mantiene una asociación navegable con un primer `ValidadorPedido`; cada validador tiene como máximo un siguiente. El orden se configura al armar la cadena. Un rechazo corta el recorrido; si no queda siguiente, el resultado es aprobado. Para el diseño futuro, `setSiguiente` debe rechazar autoenlaces y ciclos.

## Uso crítico de IA

**Prompt propuesto para revisar el diseño:** «POR COMPLETAR».

**Recomendación aceptada:** explicitar en la clase base el resultado al final de la cadena y la prevención de ciclos. La propuesta original no definía ambos casos y dejaba abierta una recursión infinita.

**Recomendación rechazada:** hacer que el procesador mantenga una lista de todos los validadores y los ejecute con un bucle. Aunque sería simple, el cliente volvería a conocer la secuencia completa y la decisión de continuar dejaría de estar en cada eslabón. Se conserva el enlace `siguiente`.

Estos textos son una propuesta de análisis para esta entrega; no se presentan como decisiones históricas previas del grupo.

## Implementación posterior

El Java sigue pendiente. Cuando se implemente, deberá respetar [la trazabilidad prevista](docs/trazabilidad-uml-codigo.md), probar rechazos en cada eslabón, aprobación final, omisión de antifraude corporativo, inserción de cupones y rechazo de ciclos, además de comparar las clases con `chainE1_mejorado.puml` mediante `CorrespondenciaUmlCodigoTest`.
