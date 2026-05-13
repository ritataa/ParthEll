package patterns.command.ui_TemplateM;

import controller.ClienteController;

/*
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * - @author / @version: Tracciano paternita e manutenzione del componente nel tempo.
 * - @param: Definisce i vincoli di input e le condizioni attese dal chiamante.
 */

/**
 * Questo file definisce un comando concreto per avviare il pagamento bancomat/POS dalla UI.
 * Incapsula la richiesta verso il controller cliente senza esporre dettagli di schermata al chiamante.
 *
 * Usa il pattern Command per separare l'invocazione dell'azione dalla sua esecuzione concreta.
 * Cosi il flusso di pagamento resta estendibile e coerente con gli altri comandi.
 *
 * @author ParthEll Team
 * @version 1.0
 */
public class BancomatPaymentCommand extends AbstractPaymentCommand {

    private final ClienteController receiver;
    private final double totale;

    /**
     * Crea il comando con il controller destinatario e l'importo da pagare.
     * Il chiamante deve fornire un controller valido e un totale coerente con il pagamento.
     *
     * @param receiver controller che apre la schermata di pagamento; non deve essere nullo.
     * @param totale importo da inoltrare al flusso bancomat; deve essere definito dal client.
     */
    public BancomatPaymentCommand(ClienteController receiver, double totale) {
        this.receiver = receiver;
        this.totale = totale;
    }

    /**
     * Esegue il comando aprendo la schermata di pagamento bancomat tramite il controller.
     * Non restituisce valori e non gestisce direttamente errori di UI del controller.
     */
    // Sicurezza: obbliga Java a verificare che sto davvero sovrascrivendo doExecute()
    // dichiarato in AbstractPaymentCommand, mantenendo intatto il template del comando.
    @Override
    protected void doExecute() {
        receiver.apriSchermataPagamentoBancomat(totale);
    }
}
