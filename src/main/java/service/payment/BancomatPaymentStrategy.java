package service.payment;

public class BancomatPaymentStrategy implements PaymentStrategy {

    @Override
    public String pay(double amount) {
        return String.format("Pagamento con bancomat registrato: %.2f EUR", amount);
    }
}
