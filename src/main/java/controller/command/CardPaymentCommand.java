package controller.command;

import controller.ClienteController;

public class CardPaymentCommand implements PaymentCommand {

    private final ClienteController receiver;
    private final double totale;

    public CardPaymentCommand(ClienteController receiver, double totale) {
        this.receiver = receiver;
        this.totale = totale;
    }

    @Override
    public void execute() {
        receiver.apriSchermataPagamentoCarta(totale);
    }
}
