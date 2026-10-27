# Trazabilidad prevista: UML y futura implementación del escenario 2

El código Java todavía no existe. Esta tabla fija el contrato que deberá implementarse después; no afirma una correspondencia ya verificada.

| Diagrama | Fuente | Imagen | Función |
|---|---|---|---|
| Original | [`mediatorE2.puml`](../uml/mediatorE2.puml) | [`diagrama-clases-original.png`](../uml/diagrama-clases-original.png) | Propuesta con referencias concretas fijas. |
| Mejorado | [`mediatorE2_mejorado.puml`](../uml/mediatorE2_mejorado.puml) | [`diagrama-clases-mejorado.png`](../uml/diagrama-clases-mejorado.png) | Contrato previsto para la implementación. |
| Secuencia | [`mediatorE2_secuencia.puml`](../uml/mediatorE2_secuencia.puml) | No solicitada | Faltante de insumo y nueva evaluación de la mesa. |

La mejora sustituye los campos de colegas concretos por un registro de `ComponenteCocina`, tipa los eventos y modela el estado de cada mesa. El reparto debe permitir reentrada sin alterar la iteración en curso.

| Elemento del UML mejorado | Correspondencia Java prevista | Verificación futura |
|---|---|---|
| `MediadorCocina` | Interfaz con `registrar` y `notificar` | Reflexión y prueba de registro. |
| `JefeDeCocina` | Mediador concreto con lista de componentes y mapa de estados | Pruebas de reparto y coordinación. |
| `ComponenteCocina` | Clase abstracta con referencia a `MediadorCocina`, `informar`, `recibir` y `atiende` | Reflexión y prueba de aislamiento de colegas. |
| `Mesero`, `EstacionCaliente`, `EstacionFria`, `Caja` | Colegas concretos que solo conocen al mediador | Pruebas de reacción a eventos. |
| `EventoCocina`, `TipoEvento`, `PedidoMesa` | Datos del mensaje y del pedido; el evento de registro transporta el pedido | Pruebas de enrutamiento por tipo y mesa. |
| `EstadoMesa` | Conteos de platos pendientes, listos, servidos y retirados por mesa | Pruebas de mesa lista, servicio confirmado, platos repetidos y plato retirado. |
| `Main` | Configuración y registro inicial | Prueba de armado. |

Un pedido registrado crea el estado de la mesa y distribuye sus platos. `PLATO_LISTO` mueve el plato de pendiente a listo. `INSUMO_AGOTADO` retira el plato pendiente, informa al mesero, ordena a caja retirar el ítem y recalcula si la mesa está lista. Tras `MESA_LISTA`, el mesero emite `PLATOS_SERVIDOS`, que mueve los platos listos a servidos. `CUENTA_SOLICITADA` debe usar solo los platos servidos. Para reentrada, el mediador recorrerá una copia de los componentes registrados; ningún colega guardará una referencia a otro colega.

Cuando exista Java, crear `CorrespondenciaUmlCodigoTest` contra `mediatorE2_mejorado.puml` y ejecutar `mvn test` desde `codigo/`. Hasta entonces no corresponde reportar pruebas aprobadas.
