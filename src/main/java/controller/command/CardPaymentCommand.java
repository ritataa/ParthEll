package controller.command;

import controller.ClienteController;

public class CardPaymentCommand extends AbstractPaymentCommand {

    private final ClienteController receiver;
    private final double totale;

    public CardPaymentCommand(ClienteController receiver, double totale) {
        this.receiver = receiver;
        this.totale = totale;
    }

    @Override
    protected void doExecute() {
        receiver.apriSchermataPagamentoCarta(totale);
    }
}
