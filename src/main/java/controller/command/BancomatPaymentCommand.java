package controller.command;

import controller.ClienteController;

public class BancomatPaymentCommand extends AbstractPaymentCommand {

    private final ClienteController receiver;
    private final double totale;

    public BancomatPaymentCommand(ClienteController receiver, double totale) {
        this.receiver = receiver;
        this.totale = totale;
    }

    @Override
    protected void doExecute() {
        receiver.apriSchermataPagamentoBancomat(totale);
    }
}
