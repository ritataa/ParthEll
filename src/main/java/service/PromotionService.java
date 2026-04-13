package service;

public class PromotionService {

    private final TelecomRepository repository;

    public PromotionService(TelecomRepository repository) {
        this.repository = repository;
    }

    public OperationResult aderisci(String email, String nomePromozione) {
        boolean added = repository.aderisciPromozione(email, nomePromozione);
        repository.aggiornaPagamentoMeseCorrente(email);
        if (added) {
            return new OperationResult(true, "Hai aderito alla promozione: " + nomePromozione);
        }
        return new OperationResult(true, "La promozione è già attiva: " + nomePromozione);
    }

    public OperationResult disdici(String email, String nomePromozione) {
        boolean removed = repository.disdiciPromozione(email, nomePromozione);
        repository.aggiornaPagamentoMeseCorrente(email);
        if (removed) {
            return new OperationResult(true, "Hai disdetto la promozione: " + nomePromozione);
        }
        return new OperationResult(true, "La promozione selezionata non risulta attiva.");
    }
}
