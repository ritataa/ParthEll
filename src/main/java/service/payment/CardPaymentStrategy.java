package service.payment;

public class CardPaymentStrategy implements PaymentStrategy {

    @Override
    public String pay(double amount) {
        return String.format("Pagamento con carta registrato: %.2f EUR", amount);
    }
}
