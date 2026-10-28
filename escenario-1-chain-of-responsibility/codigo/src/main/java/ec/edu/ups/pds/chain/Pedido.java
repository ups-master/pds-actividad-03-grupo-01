package ec.edu.ups.pds.chain;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class Pedido {

    private final List<LineaPedido> productos;
    private final BigDecimal total;
    private final boolean pagaConCredito;
    private final boolean clienteCorporativo;
    private final BigDecimal cupoDisponible;
    private final String direccion;
    private final String cupon;

    private Pedido(Builder b) {
        this.productos         = Collections.unmodifiableList(b.productos);
        this.total             = b.total;
        this.pagaConCredito    = b.pagaConCredito;
        this.clienteCorporativo = b.clienteCorporativo;
        this.cupoDisponible    = b.cupoDisponible;
        this.direccion         = b.direccion;
        this.cupon             = b.cupon;
    }

    public List<LineaPedido> getProductos()      { return productos; }
    public BigDecimal getTotal()                  { return total; }
    public boolean isPagaConCredito()             { return pagaConCredito; }
    public boolean isClienteCorporativo()         { return clienteCorporativo; }
    public BigDecimal getCupoDisponible()         { return cupoDisponible; }
    public String getDireccion()                  { return direccion; }
    public String getCupon()                      { return cupon; }

    @Override
    public String toString() {
        return "Pedido{total=" + total + ", productos=" + productos + "}";
    }

    public static class Builder {
        private List<LineaPedido> productos  = Collections.emptyList();
        private BigDecimal total             = BigDecimal.ZERO;
        private boolean pagaConCredito       = false;
        private boolean clienteCorporativo   = false;
        private BigDecimal cupoDisponible    = BigDecimal.ZERO;
        private String direccion             = "";
        private String cupon                 = null;

        public Builder productos(List<LineaPedido> productos) {
            this.productos = productos;
            return this;
        }

        public Builder total(BigDecimal total) {
            this.total = total;
            return this;
        }

        public Builder pagaConCredito(boolean pagaConCredito) {
            this.pagaConCredito = pagaConCredito;
            return this;
        }

        public Builder clienteCorporativo(boolean clienteCorporativo) {
            this.clienteCorporativo = clienteCorporativo;
            return this;
        }

        public Builder cupoDisponible(BigDecimal cupoDisponible) {
            this.cupoDisponible = cupoDisponible;
            return this;
        }

        public Builder direccion(String direccion) {
            this.direccion = direccion;
            return this;
        }

        public Builder cupon(String cupon) {
            this.cupon = cupon;
            return this;
        }

        public Pedido build() {
            if (productos.isEmpty()) {
                throw new IllegalStateException("El pedido debe tener al menos un producto");
            }
            return new Pedido(this);
        }
    }
}
