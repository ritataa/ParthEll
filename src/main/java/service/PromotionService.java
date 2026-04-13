package service;

/**
 * Servizio applicativo per adesione e disdetta promozioni.
 * Dopo ogni operazione aggiorna il pagamento del mese corrente.
 */
public class PromotionService {

    private final TelecomRepository repository;

    public PromotionService(TelecomRepository repository) {
        this.repository = repository;
    }

    /**
     * Prova ad aderire alla promozione indicata.
     *
     * @return risultato con messaggio utente, anche in caso di adesione gia attiva
     */
    public OperationResult aderisci(String email, String nomePromozione) {
        boolean added = repository.aderisciPromozione(email, nomePromozione);
        repository.aggiornaPagamentoMeseCorrente(email);
        if (added) {
            return new OperationResult(true, "Hai aderito alla promozione: " + nomePromozione);
        }
        return new OperationResult(true, "La promozione è già attiva: " + nomePromozione);
    }

    /**
     * Prova a disdire la promozione indicata.
     *
     * @return risultato con messaggio utente, anche in caso di promozione non attiva
     */
    public OperationResult disdici(String email, String nomePromozione) {
        boolean removed = repository.disdiciPromozione(email, nomePromozione);
        repository.aggiornaPagamentoMeseCorrente(email);
        if (removed) {
            return new OperationResult(true, "Hai disdetto la promozione: " + nomePromozione);
        }
        return new OperationResult(true, "La promozione selezionata non risulta attiva.");
    }
}
