package model.state;

/**
 * Contratto del pattern State per il ciclo di vita di un pagamento.
 */
public interface PagamentoState {

    /**
     * Restituisce il nome leggibile dello stato per la UI.
     *
     * @return nome stato
     */
    String getNome();

    /**
     * Indica se il pagamento puo essere ancora saldato.
     *
     * @return true se pagabile, false altrimenti
     */
    boolean canBePaid();

    /**
     * Esegue la transizione di stato dopo una conferma pagamento.
     *
     * @return nuovo stato risultante
     */
    PagamentoState pay();
}
