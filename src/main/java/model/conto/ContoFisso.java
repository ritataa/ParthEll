package model.conto;

/**
 * Implementazione di Conto per modello "pay-as-you-go" (fisso/postpagato).
 *
 * Logica:
 * - Non ha un saldo prepagato.
 * - Le spese accumulate pagabili il 30 del mese.
 * - L'addebita() registra un movimento nel database (il tracking non avviene nello stato dell'oggetto).
 *
 * Esempio: Piano telefonico postpagato.
 * Ogni acquisto di una promozione viene accumulato e pagato in una sola soluzione il 30 di ogni mese.
 */
public class ContoFisso implements Conto {

    /**
     * Crea un conto fisso (pay-as-you-go).
     * Non richiede parametri: non ha saldo interno.
     */
    public ContoFisso() {
    }

    /**
     * Per un conto fisso, le spese si accumulano per il pagamento del 30.
     * Non richiede pagamento immediato.
     */
    @Override
    public boolean richiedePagamentoImmediato(double importo) {
        return false;
    }

    /**
     * Registra un movimento di addebito per il pagamento differito del 30.
     * Il tracking delle spese avviene nel database tramite i pagamenti.
     * Questo metodo non modifica lo stato dell'oggetto (che non ha saldo).
     *
     * @param importo importo da registrare
     */
    @Override
    public void addebita(double importo) {
        // No-op: il tracking delle spese avviene nel database.
        // Quando il cliente fisso compra una promo, la spesa è registrata nel DB
        // e resa disponibile per il pagamento del 30 del mese.
    }

    /**
     * Un conto fisso non ha saldo prepagato.
     * Ritorna sempre 0.0.
     */
    @Override
    public double getSaldo() {
        return 0.0;
    }

    @Override
    public String getTipo() {
        return "Fisso";
    }

    @Override
    public String toString() {
        return "ContoFisso{pay-as-you-go}";
    }
}
