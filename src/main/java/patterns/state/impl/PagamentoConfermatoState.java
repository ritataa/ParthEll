package patterns.state.impl;

/**
 * Stato terminale di un pagamento gia confermato.
 */
public class PagamentoConfermatoState implements PagamentoState {

    @Override
    public String getNome() {
        return "Pagamento confermato";
    }

    @Override
    public boolean canBePaid() {
        return false;
    }

    /**
     * Stato immutabile: una nuova conferma non produce transizioni.
     */
    @Override
    public PagamentoState pay() {
        return this;
    }
}
