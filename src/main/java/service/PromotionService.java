package service;

/*
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * @param: descrive i vincoli e il significato degli input dei metodi pubblici.
 * @return: descrive l'output garantito al chiamante (OperationResult).
 * @author / @version: tracciano paternità e versione del file.
 */

/**
 * Servizio applicativo per adesione e disdetta promozioni.
 * Incapsula la logica di business relativa alle promozioni e mantiene lo storico pagamenti coerente.
 * Implementa un semplice application-service per separare il dominio dal repository.
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
