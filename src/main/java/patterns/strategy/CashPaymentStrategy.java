package patterns.strategy;

public class CashPaymentStrategy implements PaymentStrategy {

    @Override
    public String pay(double amount) {
        return String.format("Pagamento in contanti registrato: %.2f EUR", amount);
    }
}
