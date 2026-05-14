package patterns.strategy;

/*
LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
@author / @version: Tracciano paternita e manutenzione della classe.
@param: Definisce i vincoli di input richiesti dal metodo al chiamante.
@return: Specifica l'output garantito dal metodo per il Client.
*/

/**
 * Registra un pagamento in contanti e restituisce un messaggio sintetico.
 * La classe incapsula il comportamento del pagamento senza esporre dettagli interni.
 * Usa il pattern Strategy per rendere intercambiabile la modalita di pagamento.
 *
 * @author ParthEll Team
 * @version 1.0
 */
public class CashPaymentStrategy implements PaymentStrategy {

    // Sicurezza: obbliga Java a verificare che sto davvero implementando il metodo pay() promesso dall'interfaccia PaymentStrategy.
    @Override
    /**
     * Converte l'importo ricevuto in un messaggio di conferma per il Client.
     *
     * @param amount importo da registrare; deve essere un valore numerico valido.
     * @return stringa formattata non null con l'esito del pagamento in contanti.
     */
    public String pay(double amount) {
        System.out.println("[ATTO 4 - 11. CASH PAYMENT STRATEGY] Avvio algoritmo di pagamento in contanti.");
        // Formatta il valore con due decimali per una conferma leggibile e uniforme.
        return String.format("Pagamento in contanti registrato: %.2f EUR", amount);
    }
}
