package model.conto;

/**
 * Implementazione di Conto per modello "pay-as-you-go" (fisso/postpagato).
 *
 * Logica:
 * - Non ha un saldo prepagato.
 * - Ogni operazione richiede un pagamento immediato.
 * - L'addebita() non sottrae dal saldo (perché non esiste), ma può registrare
 *   un movimento di fatturazione per audit/logging.
 *
 * Esempio: Piano telefonico postpagato.
 * Ogni acquisto di una promozione richiede un pagamento immediato via Card/Cash/Bancomat.
 */
public class ContoFisso implements Conto {

    /**
     * Crea un conto fisso (pay-as-you-go).
     * Non richiede parametri: non ha saldo interno.
     */
    public ContoFisso() {
    }

    /**
     * Per un conto fisso, OGNI importo richiede un pagamento immediato.
     * Non c'è saldo da scalare.
     */
    @Override
    public boolean richiedePagamentoImmediato(double importo) {
        return true;
    }

    /**
     * Operazione non supportata per un conto fisso.
     * Il pagamento è gestito totalmente via Strategy (Cash, Card, Bancomat).
     * Questo metodo non dovrebbe essere invocato direttamente per ContoFisso,
     * ma se lo fosse, lanciamo un'eccezione descrittiva.
     *
     * @throws UnsupportedOperationException sempre, perché ContoFisso non ha saldo
     */
    @Override
    public void addebita(double importo) {
        throw new UnsupportedOperationException(
            "ContoFisso non supporta addebiti diretti: il pagamento deve avvenire tramite Strategy (Cash, Card, Bancomat)"
        );
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
