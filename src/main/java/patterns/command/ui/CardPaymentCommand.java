package patterns.command.ui;

import controller.ClienteController;

/**
 * Comando concreto per avviare il flusso di pagamento con carta.
 */
public class CardPaymentCommand extends AbstractPaymentCommand {

    private final ClienteController receiver;
    private final double totale;

    public CardPaymentCommand(ClienteController receiver, double totale) {
        this.receiver = receiver;
        this.totale = totale;
    }

    /**
     * Delega al controller la visualizzazione del dialog carta.
     */
    @Override
    protected void doExecute() {
        receiver.apriSchermataPagamentoCarta(totale);
    }
}
