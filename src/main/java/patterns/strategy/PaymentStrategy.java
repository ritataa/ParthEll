package patterns.strategy;


/*
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * @author / @version: Tracciano paternita e manutenzione della specifica.
 * @param: Definisce i vincoli di input richiesti al chiamante.
 * @return: Definisce l'output garantito per la sicurezza del Client.
 */

/**
 * Definisce il contratto comune per eseguire un pagamento in modo uniforme.
 * Il pattern Strategy separa l'algoritmo di pagamento dal contesto, cosi il comportamento
 * puo cambiare a runtime senza modificare il codice client.
 *
 * @author ParthEll Team
 * @version 1.0
 */
public interface PaymentStrategy {

    /**
     * Esegue il pagamento dell'importo richiesto secondo la strategia concreta selezionata.
     *
     * @param amount importo da pagare; deve essere un valore monetario valido e non negativo.
     * @return esito testuale dell'operazione; non deve essere null per garantire un feedback sicuro al Client.
     */
    // Contratto: ogni strategia concreta deve fornire la propria logica di pagamento.
    String pay(double amount);
}
