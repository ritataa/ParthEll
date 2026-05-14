package service;

/*
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * @param: descrive i vincoli e il significato degli input dei metodi pubblici.
 * @return: descrive l'output garantito al chiamante (OperationResult).
 * @author / @version: tracciano paternità e versione del file.
 */

/**
    Servizio applicativo per adesione e disdetta promozioni.
    Incapsula la logica di business relativa alle promozioni e mantiene lo storico pagamenti coerente.
    Implementa l'Application Service Pattern per la gestione delle promozioni.
    Scopo: Contenere la logica di business (adesione/disdetta) in un unico punto, separandola dall'interfaccia grafica.
    Ruolo nell'MVC: M. Fa da ponte. Riceve la richiesta dal Controller, fa i controlli necessari, usa il Repository per salvare i dati e usa il Result Pattern (OperationResult) per restituire al Controller un messaggio chiaro (successo o errore).

    Utilizza:
    - Repository Pattern tramite TelecomRepository, 
    Scopo: Nascondere tutta la complessità delle query SQL (JDBC) al resto del programma.
    Ruolo nell'MVC: M. Agisce come unico punto di accesso al database. I Service interrogano questa classe per salvare o leggere dati, senza mai dover scrivere codice SQL direttamente.
    - Result Pattern tramite OperationResult per comunicare esito operazioni alla UI.
    Scopo: Incapsulare in un unico oggetto sia lo stato (successo/fallimento) che il messaggio di risposta, evitando l'uso improprio delle Eccezioni.
    Ruolo nell'MVC: M. È il "pacchetto di comunicazione" tra il Service e il Controller. Il Service lo riempie, il Controller lo legge per decidere se mostrare un alert verde o rosso all'utente.

 *
 * @author ParthEll Team
 * @version 1.0
 */
public class PromotionService {

    private final TelecomRepository repository;

    public PromotionService(TelecomRepository repository) {
        this.repository = repository;
    }

    /**
     * Prova ad aderire alla promozione indicata e aggiorna il pagamento corrente.
     * Restituisce un `OperationResult` con messaggio adatto all'interfaccia utente.
     *
     * @param email email dell'abbonato; non nulla per eseguire l'operazione.
     * @param nomePromozione nome della promozione da aggiungere.
     * @return `OperationResult` sempre non nullo; in caso di errore di basso livello può propagarsi RuntimeException.
     */
    public OperationResult aderisci(String email, String nomePromozione) {
        System.out.println("[ATTO 3 - 2. PROMOTION SERVICE] Richiesta adesione a '" + nomePromozione + "' per utente " + email + ".");
        // Tenta di aggiungere la promozione e poi riallinea il pagamento mensile.
        boolean added = repository.aderisciPromozione(email, nomePromozione);
        repository.aggiornaPagamentoMeseCorrente(email);
        if (added) {
            // Messaggio per il caso di successo effettivo.
            return new OperationResult(true, "Hai aderito alla promozione: " + nomePromozione);
        }
        // Messaggio neutro quando l'associazione era già presente.
        return new OperationResult(true, "La promozione è già attiva: " + nomePromozione);
    }

    /**
     * Prova a disdire la promozione indicata e aggiorna il pagamento corrente.
     * Restituisce `OperationResult` con messaggio chiaro per la UI.
     *
     * @param email email dell'abbonato; non nulla per eseguire l'operazione.
     * @param nomePromozione nome della promozione da rimuovere.
     * @return `OperationResult` non nullo; può lanciare RuntimeException se il repository fallisce.
     */
    public OperationResult disdici(String email, String nomePromozione) {
        // Tenta la rimozione e riallinea il pagamento, comunicando il risultato.
        boolean removed = repository.disdiciPromozione(email, nomePromozione);
        repository.aggiornaPagamentoMeseCorrente(email);
        if (removed) {
            return new OperationResult(true, "Hai disdetto la promozione: " + nomePromozione);
        }
        return new OperationResult(true, "La promozione selezionata non risulta attiva.");
    }
}
