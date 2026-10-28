package ec.edu.ups.pds.mediator;

public class Caja extends ComponenteCocina {

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
                emitirCuenta(evento.getMesaId());
                break;
            default:
                break;
        }
    }

    public void emitirCuenta(String mesaId) {
        System.out.println("[Caja] Cuenta emitida para mesa " + mesaId);
    }

    @Override
    public boolean atiende(TipoEvento tipo) {
        return tipo == TipoEvento.RETIRAR_PLATO || tipo == TipoEvento.CUENTA_SOLICITADA;
    }
}
