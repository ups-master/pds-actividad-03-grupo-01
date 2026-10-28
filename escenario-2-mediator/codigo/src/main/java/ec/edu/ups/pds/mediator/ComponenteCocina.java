package ec.edu.ups.pds.mediator;

public abstract class ComponenteCocina {

    protected MediadorCocina mediador;

    protected ComponenteCocina(MediadorCocina mediador) {
        this.mediador = mediador;
    }

    public void informar(EventoCocina evento) {
        mediador.notificar(this, evento);
    }

    public abstract void recibir(EventoCocina evento);

    public abstract boolean atiende(TipoEvento tipo);
}
