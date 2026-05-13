package service;

/*
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * @param: definisce i vincoli e il significato degli input.
 * @return: definisce l'output garantito o nullo per il chiamante.
 * @throws: esplicita le eccezioni gestibili o propagabili dal chiamante.
 */

/**
 * Centralizza i controlli di validazione dei campi di input più usati dalla UI.
 * Riduce la duplicazione delle regole di controllo e rende i messaggi più coerenti.
 * Usa un helper stateless per riutilizzare le stesse regole in più schermate senza dipendenze.
 *
 * @author ParthEll Team
 * @version 1.0
 */
public class FormInputValidator {

    /**
     * Verifica se una stringa è nulla, vuota o composta solo da spazi.
     *
     * @param value testo da controllare.
     * @return true se il valore non contiene contenuto utile, false altrimenti.
     */
    public boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Verifica che tutti i valori ricevuti siano compilati.
     *
     * @param values insieme di stringhe da controllare; se nullo o vuoto il risultato è false.
     * @return true solo se ogni valore è compilato, false nei casi non validi.
     */
    public boolean areFilled(String... values) {
        if (values == null || values.length == 0) {
            return false;
        }
        // Controllo ogni campo in sequenza e interrompo subito al primo errore.
        for (String value : values) {
            if (isBlank(value)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Verifica che il testo rappresenti un intero positivo maggiore di zero.
     *
     * @param value numero in formato testo da validare.
     * @return true se il valore è un intero positivo, false nei casi non validi.
     */
    public boolean isPositiveInteger(String value) {
        if (isBlank(value)) {
            return false;
        }
        try {
            // Uso il parsing numerico come verifica finale della forma del dato.
            return Integer.parseInt(value.trim()) > 0;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    /**
     * Converte il testo in intero senza applicare controlli aggiuntivi.
     *
     * @param value testo numerico da convertire.
     * @return intero convertito dal valore ricevuto.
     * @throws NumberFormatException se il testo non rappresenta un numero valido.
     * @throws NullPointerException se value è nullo.
     */
    public int parseInteger(String value) {
        // La conversione è diretta perché il chiamante deve aver già validato l'input.
        return Integer.parseInt(value.trim());
    }

    /**
     * Verifica la presenza e il formato dei dati carta essenziali.
     *
     * @param intestatario nome dell'intestatario, non vuoto.
     * @param numeroCarta numero carta di 16 cifre.
     * @param scadenza data di scadenza nel formato MM/AA.
     * @param cvv codice CVV di 3 cifre.
     * @return true solo se tutti i controlli formali sono superati.
     */
    public boolean isValidCardData(String intestatario, String numeroCarta, String scadenza, String cvv) {
        if (isBlank(intestatario)) {
            return false;
        }
        // Valido ogni campo con una regola strutturale precisa per ridurre gli errori di inserimento.
        return numeroCarta != null
            && numeroCarta.matches("\\d{16}")   //Il numero della carta deve avere esattamente 16 cifre
            && scadenza != null
            && scadenza.matches("(0[1-9]|1[0-2])/\\d{2}")  //Il mese di scadenza deve essere tra 01 e 12 (gennaio-dicembre) e l'anno di scadenza deve avere 2 cifre (es: 26 per 2026)
            && cvv != null
            && cvv.matches("\\d{3}"); //	Il CVV deve avere esattamente 3 cifre
    }
}
