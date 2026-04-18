package model.conto;

/**
 * Implementazione di Conto per modello "prepagato" (ricaricabile).
 *
 * Logica:
 * - Ha un saldo prepagato gestito internamente.
 * - Il pagamento è richiesto SOLO se l'importo supera il saldo attuale.
 * - L'addebita() sottrae l'importo dal saldo.
 *
 * Esempio: Carta prepagata ricaricabile da 50€.
 * Se importo = 30€ e saldo = 50€ -> richiedePagamentoImmediato() = false
 * Se importo = 60€ e saldo = 50€ -> richiedePagamentoImmediato() = true
 */
public class ContoRicaricabile implements Conto {

    private double saldo;

    /**
     * Crea un conto ricaricabile con saldo iniziale.
     *
     * @param saldoIniziale saldo prepagato in euro
     * @throws IllegalArgumentException se saldo negativo
     */
    public ContoRicaricabile(double saldoIniziale) {
        if (saldoIniziale < 0) {
            throw new IllegalArgumentException("Il saldo iniziale non può essere negativo");
        }
        this.saldo = saldoIniziale;
    }

    /**
     * Verifica se l'importo eccede il saldo disponibile.
     * Se l'importo > saldo: è richiesto un pagamento immediato.
        * Se l'importo &lt;= saldo: può essere addebitato direttamente.
     */
    @Override
    public boolean richiedePagamentoImmediato(double importo) {
        return importo > saldo;
    }

    /**
     * Addebita l'importo dal saldo.
     * Il saldo non può diventare negativo.
     *
     * @param importo importo da addebitare
     * @throws IllegalArgumentException se importo negativo o se riduce saldo sotto zero
     */
    @Override
    public void addebita(double importo) {
        if (importo < 0) {
            throw new IllegalArgumentException("L'importo da addebitare non può essere negativo");
        }
        if (importo > saldo) {
            throw new IllegalStateException(
                String.format("Saldo insufficiente: saldo=%.2f€, importo richiesto=%.2f€", saldo, importo)
            );
        }
        this.saldo -= importo;
    }

    /**
     * Incrementa il saldo (operazione di ricarica).
     * Metodo ausiliario per la logica di ricarica del conto.
     *
     * @param importo importo di ricarica
     * @throws IllegalArgumentException se importo negativo
     */
    public void ricarica(double importo) {
        if (importo < 0) {
            throw new IllegalArgumentException("L'importo di ricarica non può essere negativo");
        }
        this.saldo += importo;
    }

    @Override
    public double getSaldo() {
        return saldo;
    }

    @Override
    public String getTipo() {
        return "Ricaricabile";
    }

    @Override
    public String toString() {
        return String.format("ContoRicaricabile{saldo=%.2f€}", saldo);
    }
}
