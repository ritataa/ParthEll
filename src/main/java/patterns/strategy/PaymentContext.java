package patterns.strategy;

/*
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * @author / @version: Tracciano paternita' e manutenzione della classe nel tempo.
 * @param: Definisce i vincoli di input che il chiamante deve rispettare.
 * @return: Esplicita l'output garantito dal metodo per il Client.
 * @throws: Dichiara le eccezioni gestibili dal chiamante in modo trasparente.
 */

/**
 * Contesto del pattern Strategy per gestire il pagamento selezionando una strategia.
 * Inoltra l'esecuzione alla strategia impostata, separando il flusso dal dettaglio di pagamento.
 * Il pattern permette di sostituire l'algoritmo senza modificare il client che usa questo contesto.
 *
 * @author ParthEll Team
 * @version 1.0
 */
public class PaymentContext {

    private PaymentStrategy strategy; // dichiariamo un puntatore a PaymentStrategy, ovvero l'interfaccia che tutte le strategie concrete devono implementare. 
    // In questo puntatore 'strategy' verranno inseriti solo oggetti che hanno firmato il contratto dell'interfaccia PaymentStrategy, garantendo che abbiano un metodo pay() con la stessa firma.
    // Grazie a questa variabile di tipo interfaccia, a runtime si può cambiare il contenuto del contenitore. (POLIMORFISMO)

    /**
     * Imposta la strategia concreta da usare per il pagamento.
     * Il chiamante deve passare una implementazione compatibile con PaymentStrategy.
     *
     * @param strategy strategia di pagamento da associare al contesto; puo' essere null, ma il pagamento fallira' se non viene impostata prima dell'uso.
     */
    public void setStrategy(PaymentStrategy strategy) {
        // Memorizza la strategia scelta dal client per usarla in seguito.
        this.strategy = strategy;   // per risolvere problema dello stesso nome 'strategy', prende il valore che è appena arrivato dall'esterno (il parametro strategy) 
        // e lo salva dentro la variabile interna che appartiene a questo specifico oggetto (this.strategy)
    }

    /**
     * Esegue il pagamento delegando alla strategia configurata.
     * Se la strategia non e' stata impostata, interrompe l'operazione in modo esplicito.
     *
     * @param amount importo da pagare; deve essere un valore valido per la strategia selezionata.
     * @return stringa prodotta dalla strategia di pagamento.
     * @throws IllegalStateException se non e' stata impostata alcuna strategia di pagamento.
     */
    public String executePayment(double amount) {
        if (strategy == null) {
            // Blocca l'uso del contesto senza una strategia valida.
            throw new IllegalStateException("Strategia di pagamento non impostata");
        }
        // Passa il controllo alla strategia concreta configurata.
        return strategy.pay(amount);
    }
}
