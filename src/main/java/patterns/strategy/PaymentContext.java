package patterns.strategy;

public class PaymentContext {

    private PaymentStrategy strategy;

    public void setStrategy(PaymentStrategy strategy) {
        this.strategy = strategy;
    }

    public String executePayment(double amount) {
        if (strategy == null) {
            throw new IllegalStateException("Strategia di pagamento non impostata");
        }
        return strategy.pay(amount);
    }
}
