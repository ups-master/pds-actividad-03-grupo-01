# Trazabilidad prevista: UML y futura implementación del escenario 1

El código Java todavía no existe. Esta tabla fija el contrato que deberá implementarse después; no afirma una correspondencia ya verificada.

| Diagrama | Fuente | Imagen | Función |
|---|---|---|---|
| Original | [`chainE1.puml`](../uml/chainE1.puml) | [`diagrama-clases-original.png`](../uml/diagrama-clases-original.png) | Propuesta inicial, no será el contrato de código. |
| Mejorado | [`chainE1_mejorado.puml`](../uml/chainE1_mejorado.puml) | [`diagrama-clases-mejorado.png`](../uml/diagrama-clases-mejorado.png) | Contrato previsto para la implementación. |
| Secuencia | [`chainE1_secuencia.puml`](../uml/chainE1_secuencia.puml) | No solicitada | Ejemplo de pedido corporativo rechazado por cupón. |

La mejora agrega un resultado terminal definido, prevención de ciclos, regla propia separada por validador, extensión de cupones y dependencias explícitas de servicios. La versión original se conserva para mostrar el cambio de diseño.

| Elemento del UML mejorado | Correspondencia Java prevista | Verificación futura |
|---|---|---|
| `ValidadorPedido` | Clase abstracta con `siguiente`, `setSiguiente`, `validar` y `validarPropio` | Reflexión y prueba de aprobación final. |
| Cinco validadores concretos | Una clase por regla; `ValidadorCupon` opcional en la cadena | Pruebas unitarias por rechazo y delegación. |
| `Pedido`, `LineaPedido` y `ResultadoValidacion` | Productos y datos de la solicitud; decisión con motivo de rechazo | Casos de aprobación, rechazo y motivo. |
| `ProcesadorPedidos` | Referencia únicamente al primer validador | Prueba de configuración y procesamiento. |
| `Inventario`, `ServicioAntifraude`, `CoberturaEntrega`, `ServicioCupones` | Interfaces para las dependencias de reglas | Dobles de prueba para aislar cada validador. |
| `siguiente` (`0..1` a `0..1`) | Enlace al siguiente eslabón, nunca a sí mismo ni a un predecesor | Pruebas de enlaces válidos y ciclos rechazados. |

En la implementación, `validar` deberá detenerse al primer rechazo, delegar ante aprobación y aprobar si no hay siguiente. El validador antifraude deberá omitir la consulta externa para clientes corporativos. La validación de crédito solo aplica si se paga con crédito; la de cupones solo si se presenta cupón. El orden de los eslabones podrá modificarse al configurar la cadena.

Cuando exista Java, crear `CorrespondenciaUmlCodigoTest` contra `chainE1_mejorado.puml` y ejecutar `mvn test` desde `codigo/`. Hasta entonces no corresponde reportar pruebas aprobadas.
