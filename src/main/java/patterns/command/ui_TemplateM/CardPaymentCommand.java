package patterns.command.ui_TemplateM;

import controller.ClienteController;

/*
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * 
 * @author: Traccia la paternità e la responsabilità del codice
 * @version: Identifica la versione semantica per la manutenzione
 * @param: Definisce i vincoli di input e il tipo di dato atteso
 * @return: Esplicita l'output garantito e il tipo restituito per la sicurezza del Client
 */

/**
 * Comando concreto per eseguire il pagamento con carta bancaria.
 * Implementa il pattern Command per incapsulare una richiesta di pagamento
 * come oggetto riutilizzabile nel flusso di transazione.
 * 
 * @author ParthEll Team
 * @version 1.0
 */
public class CardPaymentCommand extends AbstractPaymentCommand {

    private final ClienteController receiver; // Privato: lo usa solo questo comando per fare il pagamento
    private final double totale; // Privato: tiene l'importo da pagare, senza farlo cambiare da fuori

    /**
     * Costruisce un comando di pagamento con carta specifico.
     * 
     * @param receiver il controller responsabile della gestione dell'interfaccia utente
     *                 di pagamento (non null). Riceve la delega per aprire la schermata
     * @param totale   l'importo in euro da pagare (deve essere > 0). Rappresenta il
     *                 vincolo economico della transazione
     */
    public CardPaymentCommand(ClienteController receiver, double totale) {
        this.receiver = receiver;
        this.totale = totale;
    }

    /**
     * Esegue il pagamento con carta delegando l'apertura della schermata al receiver.
     * Il metodo implementa il template method del pattern Command.
     * 
     * @return void - questa operazione non restituisce risultati diretti;
     *         il risultato è propagato tramite callback del controller
     */
    // @Override: Sicurezza di compilazione - Java verifica che doExecute() sia
    // effettivamente dichiarato in AbstractPaymentCommand, prevenendo errori di
    // battitura nel nome del metodo e garantendo il rispetto del contratto ereditario
    @Override
    protected void doExecute() {
        System.out.println("[ATTO 4 - 13. COMMAND CARD PAYMENT COMMAND] Invoco il receiver per aprire la schermata di pagamento con carta.");
        // Delega al controller l'apertura della schermata di pagamento con carta,
        // passando l'importo totale come parametro vincolo della transazione
        receiver.apriSchermataPagamentoCarta(totale);
    }
}
