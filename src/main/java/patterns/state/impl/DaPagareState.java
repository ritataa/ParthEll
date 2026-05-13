package patterns.state.impl;

/*
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * @author / @version: Tracciano paternita e manutenzione della classe.
 * @return: Definisce l'output garantito del metodo per il Client.
 */
/**
 * Rappresenta lo stato iniziale di un pagamento ancora da saldare.
 * Espone il comportamento coerente con un pagamento non confermato.
 * Usa il pattern State per isolare la logica di transizione dal modello principale.
 *
 * DaPagareState è uno stato concreto. 
 * Implementa PagamentoState. Comportamento principale: getNome() → "Da pagare", canBePaid() → true, pay() → restituisce una nuova istanza di
 * PagamentoConfermatoState (cioè la transizione di stato).
 * 
 * @author ParthEll Team
 * @version 1.0
 */
public class DaPagareState implements PagamentoState {

    /**
     * Restituisce il nome leggibile dello stato attuale.
     *
     * @return etichetta testuale "Da pagare".
     */
    // Sicurezza: @Override obbliga Java a verificare che sto davvero rispettando il contratto dell'interfaccia PagamentoState.
    @Override
    public String getNome() {
        return "Da pagare";
    }

    /**
     * Indica che questo stato ammette ancora la conferma del pagamento.
     *
     * @return true, perche il saldo non e ancora stato completato.
     */
    // Sicurezza: @Override conferma che sto implementando il metodo canBePaid() promesso da PagamentoState.
    @Override
    public boolean canBePaid() {
        return true;
    }

    /**
     * La conferma di pagamento porta allo stato confermato.
     */
    /**
     * Converte questo stato nello stato confermato dopo il pagamento.
     *
     * @return un nuovo stato PagamentoConfermatoState, mai null.
     */
    // Sicurezza: @Override verifica che la transizione pay() arrivi davvero dal contratto di PagamentoState.
    @Override
    public PagamentoState pay() {
        return new PagamentoConfermatoState();
    }
}
