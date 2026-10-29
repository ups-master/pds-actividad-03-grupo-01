package ec.edu.ups.pds.mediator;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Caja extends ComponenteCocina {

    private final Map<String, Map<String, Integer>> cuentas = new HashMap<>();

    public Caja(MediadorCocina mediador) {
        super(mediador);
    }

    @Override
    public void recibir(EventoCocina evento) {
        switch (evento.getTipo()) {
            case RETIRAR_PLATO:
                System.out.println("[Caja] Retirando " + evento.getPlato()
                        + " de la cuenta de mesa " + evento.getMesaId());
                break;
            case CUENTA_SOLICITADA:
                emitirCuenta(evento.getMesaId(), evento.getConsumo());
                break;
            default:
                break;
        }
    }

    /** Emite la cuenta con exactamente lo que se sirvio en la mesa. */
    public void emitirCuenta(String mesaId, Map<String, Integer> consumo) {
        cuentas.put(mesaId, new LinkedHashMap<>(consumo));
        System.out.println("[Caja] Cuenta emitida para mesa " + mesaId + ": " + consumo);
    }

    public Map<String, Integer> obtenerCuenta(String mesaId) {
        return cuentas.get(mesaId);
    }

    @Override
    public boolean atiende(TipoEvento tipo) {
        return tipo == TipoEvento.RETIRAR_PLATO || tipo == TipoEvento.CUENTA_SOLICITADA;
    }
}
