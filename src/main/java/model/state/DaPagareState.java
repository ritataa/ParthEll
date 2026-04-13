package model.state;

/**
 * Stato iniziale di un pagamento non ancora saldato.
 */
public class DaPagareState implements PagamentoState {

    @Override
    public String getNome() {
        return "Da pagare";
    }

    @Override
    public boolean canBePaid() {
        return true;
    }

    /**
     * La conferma di pagamento porta allo stato confermato.
     */
    @Override
    public PagamentoState pay() {
        return new PagamentoConfermatoState();
    }
}
