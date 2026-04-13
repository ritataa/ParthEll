package controller.command;

import controller.ClienteController;

public class BancomatPaymentCommand implements PaymentCommand {

    private final ClienteController receiver;
    private final double totale;

    public BancomatPaymentCommand(ClienteController receiver, double totale) {
        this.receiver = receiver;
        this.totale = totale;
    }

    @Override
    public void execute() {
        receiver.apriSchermataPagamentoBancomat(totale);
    }
}
