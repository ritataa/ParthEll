package controller.command;

import controller.ClienteController;

public class CashPaymentCommand extends AbstractPaymentCommand {

    private final ClienteController receiver;
    private final double totale;

    public CashPaymentCommand(ClienteController receiver, double totale) {
        this.receiver = receiver;
        this.totale = totale;
    }

    @Override
    protected void doExecute() {
        receiver.apriSchermataPagamentoContanti(totale);
    }
}
