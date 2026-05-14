package patterns.strategy;

/*
LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
@author / @version: Tracciano paternità e manutenzione
@param: Definisce i vincoli di input
@return: Definisce l'output garantito per la sicurezza del Client
*/

/**
 * Gestisce la registrazione dei pagamenti con carta in modo uniforme.
 * Usa lo Strategy pattern per separare il comportamento di pagamento
 * dal resto dell'applicazione e renderlo facilmente sostituibile.
 *
 * @author ParthEll Team
 * @version 1.0
 */
public class CardPaymentStrategy implements PaymentStrategy {

    /**
     * Registra un pagamento con carta e restituisce un messaggio di conferma.
     *
     * @param amount importo del pagamento; deve essere >= 0.0
     * @return messaggio di conferma formattato con due decimali; non restituisce null
     */
    // Sicurezza: obbliga Java a verificare che sto davvero implementando il metodo pay() promesso all'interfaccia PaymentStrategy, evitando errori di battitura.
    @Override
    public String pay(double amount) {
        System.out.println("[ATTO 4 - 10. CARD PAYMENT STRATEGY] Avvio algoritmo di pagamento con carta di credito.");
        // Formatta l'importo in modo leggibile per il client.
        return String.format("Pagamento con carta registrato: %.2f EUR", amount);
    }
}
