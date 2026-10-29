package ec.edu.ups.pds.chain;

public class LineaPedido {

    private final String productoId;
    private final int cantidad;

    public LineaPedido(String productoId, int cantidad) {
        if (productoId == null || productoId.isBlank()) {
            throw new IllegalArgumentException("productoId no puede ser nulo ni vacio");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("cantidad debe ser mayor a cero");
        }
        this.productoId = productoId;
        this.cantidad = cantidad;
    }

    public String getProductoId() { return productoId; }
    public int getCantidad()      { return cantidad; }
}
