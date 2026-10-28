package ec.edu.ups.pds.mediator;

public interface MediadorCocina {

    void registrar(ComponenteCocina componente);

    void notificar(ComponenteCocina emisor, EventoCocina evento);
}
