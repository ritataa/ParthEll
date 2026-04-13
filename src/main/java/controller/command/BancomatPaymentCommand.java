package controller.command;

import controller.ClienteController;

/**
 * Comando concreto per avviare il flusso di pagamento bancomat/POS.
 */
public class BancomatPaymentCommand extends AbstractPaymentCommand {

    private final ClienteController receiver;
    private final double totale;

    public BancomatPaymentCommand(ClienteController receiver, double totale) {
        this.receiver = receiver;
        this.totale = totale;
    }

    /**
     * Delega al controller la visualizzazione del dialog bancomat.
     */
    @Override
    protected void doExecute() {
        receiver.apriSchermataPagamentoBancomat(totale);
    }
}
