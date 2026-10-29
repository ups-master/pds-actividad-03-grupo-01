/**
 * Escenario 1 - Chain of Responsibility: validacion de pedidos de Andes Market.
 *
 * <p>Roles del patron:
 * <ul>
 *   <li><b>Handler abstracto:</b> {@link ec.edu.ups.pds.chain.ValidadorPedido} — guarda el
 *       siguiente eslabon, implementa {@code validar} con la logica de detencion/delegacion
 *       y declara {@code validarPropio} como metodo plantilla para las subclases.</li>
 *   <li><b>ConcreteHandlers:</b> {@link ec.edu.ups.pds.chain.ValidadorStock},
 *       {@link ec.edu.ups.pds.chain.ValidadorAntifraude},
 *       {@link ec.edu.ups.pds.chain.ValidadorCredito},
 *       {@link ec.edu.ups.pds.chain.ValidadorDireccion} y, como eslabon opcional de
 *       temporada, {@link ec.edu.ups.pds.chain.ValidadorCupon}.</li>
 *   <li><b>Client:</b> {@link ec.edu.ups.pds.chain.ProcesadorPedidos} — recibe solo el primer
 *       eslabon y le envia el pedido; quien configura el sistema (aqui {@code Demo}) enlaza los
 *       eslabones con {@code setSiguiente}.</li>
 *   <li><b>Solicitud:</b> {@link ec.edu.ups.pds.chain.Pedido} (con sus
 *       {@link ec.edu.ups.pds.chain.LineaPedido}).</li>
 * </ul>
 *
 * <p>Comportamiento de la cadena: si un validador rechaza el pedido, la cadena se detiene
 * y devuelve el motivo; si lo aprueba, delega al siguiente; al agotar la cadena sin
 * rechazos, el pedido queda aprobado y pasa a despacho.
 */
package ec.edu.ups.pds.chain;
