package model;

/*
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * @author: Traccia la paternità del file.
 * @version: Indica la versione corrente del file.
 * @param: Definisce i vincoli e il formato degli argomenti dei metodi pubblici.
 * @return: Descrive il valore restituito e le garanzie offerte al chiamante.
 * @throws: Esplicita le eccezioni che il chiamante deve gestire.
 */

/**
 * Tipi di piano tariffario supportati dall'applicazione.
 * Rappresenta le varianti base/plus usate per mapping DB e logica tariffaria.
 * Implementa un enum per centralizzare i valori persistenti e le conversioni.
 *
 * @author ParthEll Team
 * @version 1.0
 */
public enum TipoPiano {
    BASE("base"),
    PLUS("plus");

    private final String dbValue;

    TipoPiano(String dbValue) {
        this.dbValue = dbValue;
    }


    /**
     * Restituisce il valore usato nel database per questo tipo di piano.
     *
     * @return stringa non-null corrispondente al valore persistente.
     */
    public String getDbValue() {
        return dbValue;
    }

    // Sicurezza: obbliga Java a verificare che sto davvero sovrascrivendo toString() definito
    // in java.lang.Enum / java.lang.Object, fornendo una rappresentazione leggibile per logging.
    @Override
    public String toString() {
        return dbValue;
    }

    public static TipoPiano from(String value) {
        /**
         * Converte una stringa libera in un valore `TipoPiano` valido.
         *
         * @param value stringa di input (es. "base", "plus", "premium"); non può essere null.
         * @return il `TipoPiano` corrispondente.
         * @throws IllegalArgumentException se `value` è null o non riconosciuto.
         */
        if (value == null) {
            // Esplicito l'errore per chiarezza al chiamante (contratto di sicurezza).
            throw new IllegalArgumentException("Tipo di abbonato non riconosciuto: null");
        }

        // Normalizzo input: rimuovo spazi e confronto case-insensitive.
        String norm = value.trim().toLowerCase();

        // Accetto alias comuni per maggiore tolleranza verso dati esterni/DB legacy.
        return switch (norm) {
            case "base", "basic" -> BASE;
            case "plus", "premium" -> PLUS;
            default -> throw new IllegalArgumentException("Tipo di abbonato non riconosciuto: " + value);
        };
    }
}
