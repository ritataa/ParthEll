package controller.command;

public abstract class AbstractPaymentCommand implements PaymentCommand {

    @Override
    public final void execute() {
        beforeExecute();
        doExecute();
        afterExecute();
    }

    protected void beforeExecute() {
        // Hook opzionale
    }

    protected abstract void doExecute();

    protected void afterExecute() {
        // Hook opzionale
    }
}
