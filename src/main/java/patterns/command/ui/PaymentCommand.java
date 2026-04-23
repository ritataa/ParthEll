package patterns.command.ui;

/**
 * Contratto base del pattern Command per le azioni di pagamento.
 * Ogni implementazione incapsula una richiesta eseguibile dalla UI.
 */
public interface PaymentCommand {

    /**
     * Esegue l'azione incapsulata dal comando.
     */
    void execute();
}
