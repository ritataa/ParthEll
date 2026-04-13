package controller.command;

/**
 * Base astratta dei comandi di pagamento.
 * Implementa un Template Method con fasi before/do/after.
 */
public abstract class AbstractPaymentCommand implements PaymentCommand {

    /**
     * Flusso standard di esecuzione del comando.
     */
    @Override
    public final void execute() {
        beforeExecute();
        doExecute();
        afterExecute();
    }

    /**
     * Hook opzionale eseguito prima della logica principale.
     */
    protected void beforeExecute() {
        // Hook opzionale
    }

    /**
     * Passo obbligatorio da implementare nelle sottoclassi.
     */
    protected abstract void doExecute();

    /**
     * Hook opzionale eseguito dopo la logica principale.
     */
    protected void afterExecute() {
        // Hook opzionale
    }
}
