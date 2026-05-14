package patterns.command.ui_TemplateM;

/*
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * - @author / @version: Tracciano paternita e manutenzione del componente nel tempo.
 * - @throws: Esplicita le eccezioni propagabili e quindi gestibili dal chiamante.
 */

/**
 * Classe base per comandare il flusso di pagamento con una sequenza unica e riusabile.
 * Definisce i passaggi comuni prima, durante e dopo l'esecuzione concreta.
 *
 * E' stato scelto il Template Method per centralizzare l'ordine dei passi e ridurre duplicazioni.
 * Le sottoclassi personalizzano solo la parte variabile, mantenendo invariata la struttura generale.
 *
 * @author ParthEll Team
 * @version 1.0
 */
public abstract class AbstractPaymentCommand implements PaymentCommand {

    /**
     * Esegue il comando rispettando il contratto del flusso standard before/do/after.
     * Propaga eventuali eccezioni runtime generate dagli step concreti delle sottoclassi.
     *
     * @throws RuntimeException se uno degli step concreti fallisce durante l'esecuzione
     */
    // Sicurezza: obbliga Java a verificare che sto davvero implementando execute()
    // promesso all'interfaccia PaymentCommand, evitando metodi con firma errata.
    @Override
    public final void execute() {
        System.out.println("[ATTO 4 - 12. TEMPLATE METHOD COMMAND] Esecuzione comando pagamento: before/do/after.");
        // 1) Preparo il contesto comune prima della logica specifica.
        beforeExecute();
        // 2) Eseguo il passo obbligatorio definito dalla sottoclasse.
        doExecute();
        // 3) Completo eventuali azioni finali comuni di chiusura.
        afterExecute();
    }

    /**
     * Hook opzionale eseguito prima della logica principale.
     */
    protected void beforeExecute() {
        // Hook opzionale: la sottoclasse puo inserirsi qui senza alterare il flusso base.
    }

    /**
     * Passo obbligatorio da implementare nelle sottoclassi.
     */
    protected abstract void doExecute();

    /**
     * Hook opzionale eseguito dopo la logica principale.
     */
    protected void afterExecute() {
        // Hook opzionale: usato per pulizia/log dopo il passo principale.
    }
}
