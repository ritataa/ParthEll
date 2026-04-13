package controller.command;

import controller.ClienteController;

public class CashPaymentCommand implements PaymentCommand {

    private final ClienteController receiver;
    private final double totale;

    public CashPaymentCommand(ClienteController receiver, double totale) {
        this.receiver = receiver;
        this.totale = totale;
    }

    @Override
    public void execute() {
        receiver.apriSchermataPagamentoContanti(totale);
    }
}
