package controller.command;

import controller.ClienteController;

/**
 * Comando concreto per avviare il flusso di pagamento in contanti.
 */
public class CashPaymentCommand extends AbstractPaymentCommand {

    private final ClienteController receiver;
    private final double totale;

    public CashPaymentCommand(ClienteController receiver, double totale) {
        this.receiver = receiver;
        this.totale = totale;
    }

    /**
     * Delega al controller la visualizzazione del dialog contanti.
     */
    @Override
    protected void doExecute() {
        receiver.apriSchermataPagamentoContanti(totale);
    }
}
