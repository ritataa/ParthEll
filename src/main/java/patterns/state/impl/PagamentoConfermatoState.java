package patterns.state.impl;

/*
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * @author / @version: Tracciano paternita e manutenzione della classe.
 * @return: Definisce l'output garantito del metodo per il Client.
 */
/**
 * Rappresenta lo stato finale di un pagamento gia confermato.
 * Mantiene stabile il comportamento quando il saldo e gia concluso.
 * Usa il pattern State per bloccare transizioni non piu valide nel ciclo di vita.
 *
 * PagamentoConfermatoState è lo stato finale concreto. 
 * Implementa PagamentoState. Comportamento: getNome() → "Pagamento confermato", canBePaid() → false, pay() → ritorna this (nessuna transizione ulteriore).
 * 
 * @author ParthEll Team
 * @version 1.0
 */
/**
 * Stato terminale di un pagamento gia confermato.
 */
public class PagamentoConfermatoState implements PagamentoState {

    /**
     * Restituisce il nome leggibile dello stato terminale.
     *
     * @return etichetta testuale "Pagamento confermato".
     */
    // Sicurezza: @Override obbliga Java a verificare che sto implementando getNome() promesso da PagamentoState.
    @Override
    public String getNome() {
        return "Pagamento confermato";
    }

    /**
     * Indica che un pagamento gia confermato non e piu saldabile.
     *
     * @return false, perche lo stato e terminale.
     */
    // Sicurezza: @Override conferma che sto rispettando il metodo canBePaid() definito da PagamentoState.
    @Override
    public boolean canBePaid() {
        return false;
    }

    /**
     * Stato immutabile: una nuova conferma non produce transizioni.
     */
    /**
     * Mantiene invariato lo stato perche il pagamento e gia stato confermato.
     *
     * @return this, mai null, per preservare l'immutabilita dello stato finale.
     */
    // Sicurezza: @Override verifica che la transizione pay() arrivi davvero dal contratto dell'interfaccia PagamentoState.
    @Override
    public PagamentoState pay() {
        // Nessun cambio: lo stato finale resta fermo per evitare transizioni incoerenti.
        return this;
    }
}
