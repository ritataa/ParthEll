package patterns.command.ui_TemplateM;

import controller.ClienteController;

/*
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * 
 * @author: Traccia la paternità e la responsabilità della classe per questioni di manutenzione.
 * @version: Documenta la versione del componente per tracciare l'evoluzione e la compatibilità.
 * @param: Definisce i vincoli di input e le condizioni attese dal chiamante.
 * @return: Esplicita l'output garantito e il tipo restituito per la
 */

/**
 * Comando concreto che implementa l'esecuzione del pagamento in contanti.
 * Parte della catena di comandi per i diversi metodi di pagamento (contanti, carta, bonifico).
 * Implementa il pattern Command per incapsulare una richiesta di pagamento come oggetto,
 * permettendo al client di parametrizzare, accodare e ritardare l'esecuzione del pagamento.
 * 
 * @author ParthEll Team
 * @version 1.0
 */
public class CashPaymentCommand extends AbstractPaymentCommand {

    private final ClienteController receiver; // Privato: lo usa solo questo comando per fare il pagamento
    private final double totale; // Privato: tiene l'importo da pagare, senza farlo cambiare da fuori

    /**
     * Costruisce un nuovo comando di pagamento in contanti.
     * Memorizza il controller destinatario e l'importo totale da pagare.
     * 
     * @param receiver Il controller che gestisce le operazioni di pagamento (non deve essere null)
     * @param totale L'importo in euro da pagare in contanti (deve essere positivo)
     */
    public CashPaymentCommand(ClienteController receiver, double totale) {
        this.receiver = receiver;
        this.totale = totale;
    }

    /**
     * Esegue il comando di pagamento delegando al controller l'apertura della schermata di pagamento.
     * Implementa il contratto dell'interfaccia Command astraendo l'azione specifica di pagamento.
     * 
     * @throws IllegalStateException Se il receiver non è disponibile o il totale non è valido
     */
    // Sicurezza: @Override obbliga Java a verificare che sto davvero implementando il metodo 
    // doExecute() promesso dalla classe astratta AbstractPaymentCommand, evitando errori di battitura
    @Override
    protected void doExecute() {
        // Delega l'apertura della schermata di pagamento contanti al controller del cliente
        // Il controller gestisce la UI e la logica di interazione con l'utente per il pagamento
        receiver.apriSchermataPagamentoContanti(totale);
    }
}
