package model.conto;

/**
 * Contratto polimorfico per la gestione dei conti dell'abbonato.
 * Implementa il Polimorfismo per distinguere tra conti ricaricabili (con saldo)
 * e conti fissi (pay-as-you-go).
 *
 * Principi SOLID applicati:
 * - Single Responsibility: ogni sottoclasse ha una sola ragione di cambiamento
 * - Liskov Substitution: sottoclassi sostituibili senza rompere il contratto
 * - Interface Segregation: interfaccia focalizzata su operazioni essenziali
 * - Dependency Inversion: dipendere dall'astrazione, non dalle implementazioni concrete
 */
public interface Conto {

    /**
     * Determina se il cliente deve effettuare un pagamento immediato
     * per sostenere l'importo richiesto.
     *
     * @param importo importo richiesto per l'operazione (es. costo promozione)
     * @return true se il pagamento è richiesto ora, false se l'importo può essere scalato dal saldo
     */
    boolean richiedePagamentoImmediato(double importo);

    /**
     * Addebita l'importo sul conto.
     * Per un conto ricaricabile: sottrae dal saldo.
     * Per un conto fisso: può lanciare eccezione o no-op (il pagamento è gestito a parte).
     *
     * @param importo importo da addebitare
     * @throws IllegalStateException se l'operazione non è consentita per questo tipo di conto
     */
    void addebita(double importo);

    /**
     * Restituisce il saldo disponibile sul conto.
     * Per un conto ricaricabile: il saldo in euro.
     * Per un conto fisso: sempre 0.0 (nessun saldo pre-caricato).
     *
     * @return saldo disponibile
     */
    double getSaldo();

    /**
     * Restituisce il tipo di conto (Fisso o Ricaricabile).
     * Utile per la UI e per logging.
     *
     * @return tipo conto
     */
    String getTipo();
}
