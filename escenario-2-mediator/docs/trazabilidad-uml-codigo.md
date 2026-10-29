# Trazabilidad UML-código: escenario 2 (Mediator)

Dirección del diseño: **recomendación de la IA aceptada (informe, sección 8) → diagrama mejorado → código**. El
diagrama mejorado es el contrato del código y está verificado. `CorrespondenciaUmlCodigoTest` lee
[`mediatorE2_mejorado.puml`](../uml/mediatorE2_mejorado.puml) y, por reflexión, comprueba que cada clase,
atributo, operación y constante de enum del diagrama existe en el código con su visibilidad y tipos, y que
ningún atributo ni operación propios del código falta en el diagrama. Un cambio de diseño empieza en el `.puml`;
después se ajusta el código, la imagen y este documento. Quedan fuera del contrato los detalles de
implementación: constructores, las fábricas estáticas de `EventoCocina`, los métodos privados no declarados,
`Main` y las pruebas.

| Diagrama | Fuente | Imagen | Función |
|---|---|---|---|
| Original | [`mediatorE2.puml`](../uml/mediatorE2.puml) | [`diagrama-clases-original.png`](../uml/diagrama-clases-original.png) | Propuesta con referencias concretas fijas; se conserva para mostrar el cambio. |
| Mejorado | [`mediatorE2_mejorado.puml`](../uml/mediatorE2_mejorado.puml) | [`diagrama-clases-mejorado.png`](../uml/diagrama-clases-mejorado.png) | Contrato implementado. |
| Mejorado (archify) | Mismo diseño | [`diagrama-clases-mejorado.html`](../uml/diagrama-clases-mejorado.html) (interactivo) y [`diagrama-clases-mejorado-archify.png`](../uml/diagrama-clases-mejorado-archify.png) | Vistas derivadas generadas con archify; no son el contrato, lo es el `.puml`. Si el diseño cambia hay que regenerarlas. |
| Secuencia | [`mediatorE2_secuencia.puml`](../uml/mediatorE2_secuencia.puml) | No solicitada | Faltante de insumo, mesa lista y cuenta con lo servido. |

## Cómo leer el diagrama mejorado

Cada rol GoF tiene un estereotipo y un color (ver la leyenda del diagrama) y las clases se agrupan en paquetes
con los mismos nombres que la tabla de participantes del informe:

| Paquete del diagrama | Contenido |
|---|---|
| Roles del patrón (GoF) | `MediadorCocina` (Mediator), `JefeDeCocina` (ConcreteMediator), `ComponenteCocina` (Colleague), `Mesero`, `EstacionCaliente`, `EstacionFria` y `Caja` (ConcreteColleague) y `Main` (Client) |
| Datos de colaboración (no son roles GoF) | `EventoCocina`, `TipoEvento`, `PedidoMesa`, `EstadoMesa` |

## Mejora aceptada → diagrama mejorado → código

| # | Mejora que propuso la IA y se aceptó | En el diagrama mejorado | En el código |
|---|---|---|---|
| 1 | Registro por abstracción | `componentes : List<ComponenteCocina>`, `registrar`, `recibir`, `atiende` | `JefeDeCocina.componentes`, `MediadorCocina.registrar`, `ComponenteCocina.recibir` / `atiende` |
| 2 | Reparto por estación | `prepara(plato)`, `menu` en las estaciones, `validarPlatos`, `repartirPedido` | `ComponenteCocina.prepara`, `EstacionCaliente.menu`, `EstacionFria.menu`, `JefeDeCocina.repartirPedido` |
| 3 | Eventos tipados y con datos | Enum `TipoEvento`; `EventoCocina.pedido` y `consumo`; `PedidoMesa` | `TipoEvento`, `EventoCocina`, `PedidoMesa` |
| 4 | Estado de la mesa | `EstadoMesa` (cuatro mapas, `servida`, `agregarPlatos`, `estaLista`, `estaServida`); `estados`, `obtenerEstado`, `mesaLista` | `EstadoMesa`, `JefeDeCocina.estados` / `obtenerEstado` / `mesaLista` |
| 5 | Cuenta con lo servido | `Mesero.solicitarCuenta`, `Caja.emitirCuenta(mesaId, consumo)`, `obtenerCuenta`, `cuentas` | `Mesero`, `Caja` |
| 6 | Reentrada | `despachar` y la nota de `JefeDeCocina` | `JefeDeCocina.despachar` recorre una copia de `componentes` |

## Elemento del UML, clase Java y prueba

Todas las clases están en `codigo/src/main/java/ec/edu/ups/pds/mediator/`. Las pruebas están en
`codigo/src/test/java/ec/edu/ups/pds/mediator/`: `CorrespondenciaUmlCodigoTest` (C) y `JefeDeCocinaTest` (J).

| Rol / elemento del UML | Clase Java | Qué verifica |
|---|---|---|
| Mediator `MediadorCocina` | `MediadorCocina` (interfaz) | C: interfaz con `registrar` y `notificar`. |
| ConcreteMediator `JefeDeCocina` | `JefeDeCocina` | C: campos privados `componentes` y `estados`, `obtenerEstado`. J: reparto filtrado por estación, plato sin estación, faltante, mesa lista, segundo pedido, reentrada. |
| Colleague `ComponenteCocina` | `ComponenteCocina` (abstracta) | C: campo protegido `mediador`, `informar`, `recibir` y `atiende` abstractos, `prepara`, y ningún colega tiene campos de otro colega. |
| ConcreteColleague `Mesero` | `Mesero` | C: `registrarPedido`, `solicitarCuenta`. J: informa al cliente ante un faltante, sirve ante `MESA_LISTA`. |
| ConcreteColleague `EstacionCaliente` | `EstacionCaliente` | C: `menu`, `reportarFaltante`, `prepararPlato`. J: recibe solo sus platos. |
| ConcreteColleague `EstacionFria` | `EstacionFria` | C: `menu`, `prepararPlato`. J: recibe solo sus platos. |
| ConcreteColleague `Caja` | `Caja` | C: `emitirCuenta(mesaId, consumo)`, `obtenerCuenta`. J: la cuenta contiene solo lo servido. |
| `EventoCocina`, `TipoEvento`, `PedidoMesa` | las mismas clases | C: siete valores del enum y getters (incluido `getConsumo`). J: enrutamiento por tipo y mesa. P (`PedidoMesaTest`): validaciones y lista inmutable. |
| `EstadoMesa` | `EstadoMesa` | C: cuatro mapas y `servida` privados; operaciones. J: platos repetidos, mesa lista, servida una sola vez, mesa sin nada que servir. E (`EstadoMesaTest`): getters de solo lectura, copia independiente, cierre de la mesa reabierta. |
| Client `Main` | `Main` | C: existe en el paquete. Se ejecuta con `mvn exec:java`. |

## Reglas de coordinación implementadas

- `PEDIDO_REGISTRADO` crea el estado de la mesa (o suma platos si ya existía y la reabre) y entrega a cada
  estación solo los platos que `prepara`. Si ninguna estación prepara un plato, el pedido se rechaza sin crear
  estado. Es atómico: si una estación falla al recibir su parte, se restaura el estado anterior (o se quita la
  mesa nueva) y la excepción se propaga. Un `PedidoMesa` exige mesa y al menos un plato, sin nulos ni vacíos,
  y guarda una copia inmutable de la lista.
- `PLATO_LISTO` mueve el plato de pendiente a listo; un plato que no estaba pendiente se ignora.
- `INSUMO_AGOTADO` retira el plato, informa al mesero, ordena a caja retirar el ítem y recalcula la mesa; un
  faltante de un plato ajeno al pedido se ignora.
- Una mesa está lista cuando no tiene pendientes, tiene algo listo y aún no se sirvió; así `MESA_LISTA` se
  reparte una sola vez y una mesa con todos los platos retirados no se sirve. Si un pedido posterior se agota por
  completo y la mesa ya había servido platos, queda servida y no se repite el aviso. Los getters de `EstadoMesa`
  devuelven vistas de solo lectura.
- Tras `MESA_LISTA`, el mesero emite `PLATOS_SERVIDOS` (reentrada) y los platos listos pasan a servidos.
- `CUENTA_SOLICITADA` lo emite el mesero; el mediador adjunta lo servido hasta ese momento y la caja emite la
  cuenta con ello. Se puede volver a pedir: la nueva cuenta reemplaza a la anterior con lo servido después.
- El reparto itera una copia de los componentes; ningún colega guarda una referencia a otro.

## Verificación

```text
mvn test -pl escenario-2-mediator/codigo
```

Resultado: 76 pruebas, 0 fallos (45 de correspondencia UML-código y 31 funcionales).
