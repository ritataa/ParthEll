package patterns.strategy;

/* LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 - @author / @version: Tracciano paternità e manutenzione
 - @param: Definisce i vincoli di input
 - @return: Definisce l'output garantito per la sicurezza del Client
*/

/**
 * Strategia di pagamento tramite Bancomat.
 * Implementa il pattern Strategy per isolare l'algoritmo di pagamento e
 * permettere la sostituibilità delle modalità di pagamento.
 *
 * @author ParthEll Team
 * @version 1.0
 */
public class BancomatPaymentStrategy implements PaymentStrategy {

    /**
     * Registra un pagamento effettuato con bancomat e restituisce una stringa di
     * conferma formattata.
     *
     * @param amount importo del pagamento in euro (vincolo: >= 0.0)
     * @return messaggio di conferma formattato con due decimali; non restituisce
     *         null e non lancia eccezioni previste dal contratto
     */
    @Override
    public String pay(double amount) {
        System.out.println("[ATTO 4 - 10. BANCOMAT PAYMENT STRATEGY] Avvio algoritmo di pagamento con bancomat.");
        // Format: due decimali per una rappresentazione leggibile dell'importo
        // Restituisce sempre una stringa di conferma; non si propagano eccezioni
        return String.format("Pagamento con bancomat registrato: %.2f EUR", amount);
    }
}
