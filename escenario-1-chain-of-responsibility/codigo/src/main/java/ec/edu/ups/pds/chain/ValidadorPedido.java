package ec.edu.ups.pds.chain;

public abstract class ValidadorPedido {

    private ValidadorPedido siguiente;

    /**
     * Encadena el siguiente validador y lo devuelve para permitir encadenamiento fluido.
     * Rechaza null, autoenlaces y ciclos para evitar NPE y bucles infinitos. Una cadena termina
     * simplemente al no enlazar mas; una cola ya armada puede compartirse entre cadenas.
     */
    public ValidadorPedido setSiguiente(ValidadorPedido siguiente) {
        if (siguiente == null) {
            throw new IllegalArgumentException("El siguiente validador no puede ser nulo");
        }
        if (siguiente == this) {
            throw new IllegalArgumentException("Un validador no puede ser su propio siguiente");
        }
        ValidadorPedido cursor = siguiente;
        while (cursor != null) {
            if (cursor == this) {
                throw new IllegalArgumentException("Enlace ciclo detectado en la cadena de validacion");
            }
            cursor = cursor.siguiente;
        }
        this.siguiente = siguiente;
        return siguiente;
    }

    /**
     * Aplica la validacion propia; si el pedido se aprueba, delega al siguiente eslabon.
     * Si no hay siguiente, devuelve aprobado (fin de cadena exitoso).
     * Si el pedido se rechaza, detiene la cadena y retorna el motivo inmediatamente.
     */
    public ResultadoValidacion validar(Pedido pedido) {
        ResultadoValidacion resultado = validarPropio(pedido);
        if (!resultado.isAprobado()) {
            return resultado;
        }
        if (siguiente != null) {
            return siguiente.validar(pedido);
        }
        return ResultadoValidacion.aprobado();
    }

    protected abstract ResultadoValidacion validarPropio(Pedido pedido);
}
