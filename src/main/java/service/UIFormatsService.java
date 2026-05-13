package service;

/*
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * @param: definisce i vincoli e il significato degli input dei metodi pubblici.
 * @return: definisce l'output garantito o il fallback restituito al chiamante.
 * @author / @version: tracciano paternità e versione del file.
 */

/**
 * Servizio helper per formattazioni UI semplici (valori monetari e testi per celle).
 * Centralizza piccoli adattamenti di presentazione per mantenere la UI coerente.
 * Utilizza metodi puri e senza stato per facilità di test e riuso.
 *
 * @author ParthEll Team
 * @version 1.0
 */
public class UIFormatsService {

    /**
     * Formatta un importo in euro con due decimali e suffisso "EUR".
     *
     * @param amount importo numerico; può essere positivo, negativo o zero.
     * @return stringa formattata come "12.34 EUR".
     */
    public String formatEuro(double amount) {
        // Uso String.format per garantire sempre due decimali.
        return String.format("%.2f EUR", amount);
    }

    /**
     * Converte la lista di promozioni in un testo adatto a una cella tabellare.
     * Se il valore è nullo o vuoto restituisce il fallback "Nessuna promo".
     *
     * @param promo stringa con nomi promozioni separati da ", ".
     * @return testo con ogni promozione su nuova riga, o "Nessuna promo" come fallback.
     */
    public String formatPromoForCell(String promo) {
        if (promo == null || promo.isBlank()) {
            return "Nessuna promo";
        }
        // Sostituisco la virgola con una nuova linea per una cella multilinea.
        return promo.replace(", ", "\n");
    }

    /**
     * Determina se lo stato corrisponde a "Da pagare" (case-insensitive).
     *
     * @param stato stato testuale del pagamento; può essere anche null.
     * @return true se lo stato indica un pagamento da saldare, false altrimenti.
     */
    public boolean isDaPagare(String stato) {
        // Trim e confronto case-insensitive per robustezza contro input sporchi.
        return stato != null && "Da pagare".equalsIgnoreCase(stato.trim());
    }

    /**
     * Restituisce una rappresentazione leggibile dei dettagli promozionali.
     * Se il valore è nullo o vuoto restituisce il fallback "Nessuna promo".
     *
     * @param promo testo dettagli promozionali; può essere null.
     * @return il testo originale o il fallback se assente.
     */
    public String formatPromoDetails(String promo) {
        if (promo == null || promo.isBlank()) {
            return "Nessuna promo";
        }
        return promo;
    }
}
