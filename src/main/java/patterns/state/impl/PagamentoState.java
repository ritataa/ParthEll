package patterns.state.impl;

/*
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * @author / @version: Tracciano paternita e manutenzione della classe.
 * @return: Definisce l'output garantito del metodo per il Client.
 */
/**
 * Definisce il contratto comune per tutti gli stati di un pagamento.
 * Stabilisce quali operazioni ogni stato deve esporre in modo coerente.
 * Usa il pattern State per separare il comportamento del pagamento dal modello dati.
 *
 * PagamentoState è l'interfaccia (il contratto). 
 * Dichiara tre operazioni che ogni stato deve fornire: getNome(), canBePaid(), pay(). 
 * Non contiene logica concreta, solo le regole che gli stati devono rispettare.
 * 
 * @author ParthEll Team
 * @version 1.0
 */
public interface PagamentoState {

    /**
     * Restituisce il nome leggibile dello stato per la UI.
     * Questo valore viene usato dal client per mostrare lo stato corrente.
     *
     * @return nome stato, mai null se l'implementazione e coerente.
     */
    String getNome();

    /**
     * Indica se il pagamento puo essere ancora saldato.
     * Serve al client per abilitare o disabilitare le azioni disponibili.
     *
     * @return true se il pagamento e ancora pagabile, false altrimenti.
     */
    boolean canBePaid();

    /**
     * Esegue la transizione di stato dopo una conferma pagamento.
     * Ogni implementazione decide il prossimo stato valido del pagamento.
     *
     * @return nuovo stato risultante, mai null nelle implementazioni corrette.
     */
    PagamentoState pay();
}
