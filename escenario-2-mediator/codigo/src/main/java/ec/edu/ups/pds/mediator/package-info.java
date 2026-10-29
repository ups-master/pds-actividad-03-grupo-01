/**
 * Escenario 2 - Mediator: coordinacion de pedidos en la cocina de El Fogon Andino.
 *
 * <p>El mesero, las estaciones caliente y fria y la caja se afectan entre si: cada plato debe
 * llegar a la estacion que lo prepara, los platos de una mesa se sirven juntos y un faltante de
 * insumo cambia lo que ve el cliente, lo que cobra la caja y cuando se puede servir. Ningun
 * participante conoce a otro; toda la coordinacion pasa por el jefe de cocina.
 *
 * <p>Roles del patron:
 * <ul>
 *   <li><b>Mediator:</b> {@link ec.edu.ups.pds.mediator.MediadorCocina} - registrar y notificar.</li>
 *   <li><b>ConcreteMediator:</b> {@link ec.edu.ups.pds.mediator.JefeDeCocina} - conoce a los
 *       componentes solo por su abstraccion, reparte cada plato a su estacion, lleva el
 *       {@link ec.edu.ups.pds.mediator.EstadoMesa} de cada mesa y recorre una copia de la lista
 *       para tolerar la reentrada.</li>
 *   <li><b>Colleague:</b> {@link ec.edu.ups.pds.mediator.ComponenteCocina} - conoce solo a su
 *       mediador.</li>
 *   <li><b>ConcreteColleagues:</b> {@link ec.edu.ups.pds.mediator.Mesero},
 *       {@link ec.edu.ups.pds.mediator.EstacionCaliente},
 *       {@link ec.edu.ups.pds.mediator.EstacionFria} y
 *       {@link ec.edu.ups.pds.mediator.Caja}.</li>
 *   <li><b>Client:</b> {@link ec.edu.ups.pds.mediator.Main} - arma el mediador y registra a los
 *       componentes.</li>
 *   <li><b>Datos de colaboracion (no son roles GoF):</b>
 *       {@link ec.edu.ups.pds.mediator.EventoCocina}, {@link ec.edu.ups.pds.mediator.TipoEvento},
 *       {@link ec.edu.ups.pds.mediator.PedidoMesa} y {@link ec.edu.ups.pds.mediator.EstadoMesa}.</li>
 * </ul>
 *
 * <p>Reglas de coordinacion: un faltante retira el plato, avisa al mesero y a la caja y vuelve a
 * evaluar la mesa; una mesa se sirve una sola vez y solo si queda algo que servir; la cuenta se
 * emite cuando el mesero la pide y contiene unicamente lo servido.
 */
package ec.edu.ups.pds.mediator;
