package patterns.command.ui_TemplateM;

/*
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * @author / @version: Tracciano paternita e manutenzione del componente.
 * @return: Definisce l'output garantito per la sicurezza del Client.
 */

/**
 * Definisce il contratto minimo dei comandi UI legati alle operazioni di pagamento.
 * Il pattern Command e usato per separare chi richiede l'azione da chi la esegue concretamente.
 * In questo modo la UI resta estendibile, testabile e meno accoppiata alle logiche operative.
 *
 * @author ParthEll Team
 * @version 1.0
 */
public interface PaymentCommand {

    /**
     * Esegue l'operazione di pagamento incapsulata dal comando concreto.
     * La logica effettiva e delegata all'implementazione per mantenere disaccoppiata la UI.
     *
     * @return Nessun valore di ritorno (void); eventuali errori runtime vengono propagati al chiamante.
     */
    // Contratto unico: ogni comando UI deve esporre un punto di esecuzione uniforme.
    void execute();
}
