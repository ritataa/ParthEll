package model;

/**
 * Factory per la creazione di diversi tipi di abbonati.
 * Implementa il pattern Factory Simple per creare istanze di Abbonato
 * con configurazioni predefinite basate sul tipo di piano tariffario.
 * 
 * @author ParthEll Team
 * @version 1.0
 */
public class AbbonatoFactory {
    
    /**
     * Crea un abbonato con piano base predefinito.
     *
     * @param nome il nome dell'abbonato
     * @param cognome il cognome dell'abbonato
     * @param email l'email dell'abbonato
     * @param residenza la residenza dell'abbonato
     * @param numeroTelefono il numero di telefono
     * @return un nuovo abbonato con piano base
     */
    public static Abbonato createAbbonatoBase(String nome, String cognome, String email, String residenza, String numeroTelefono) {
        return Abbonato.builder()
            .nome(nome)
            .cognome(cognome)
            .email(email)
            .residenza(residenza)
            .numeroTelefono(numeroTelefono)
            .pianoTariffario(TipoPiano.BASE)
            .build();
    }
    
    /**
     * Crea un abbonato con piano plus predefinito.
     *
     * @param nome il nome dell'abbonato
     * @param cognome il cognome dell'abbonato
     * @param email l'email dell'abbonato
     * @param residenza la residenza dell'abbonato
     * @param numeroTelefono il numero di telefono
     * @return un nuovo abbonato con piano plus
     */
    public static Abbonato createAbbonatoPlus(String nome, String cognome, String email,
                                              String residenza, String numeroTelefono) {
        return Abbonato.builder()
            .nome(nome)
            .cognome(cognome)
            .email(email)
            .residenza(residenza)
            .numeroTelefono(numeroTelefono)
            .pianoTariffario(TipoPiano.PLUS)
            .build();
    }
    
    /**
     * Crea un abbonato basato sul tipo di piano specificato.
     *
     * @param tipo il tipo di piano (BASE, PLUS)
     * @param nome il nome dell'abbonato
     * @param cognome il cognome dell'abbonato
     * @param email l'email dell'abbonato
     * @param residenza la residenza dell'abbonato
     * @param numeroTelefono il numero di telefono
     * @return un nuovo abbonato del tipo specificato
     */
    public static Abbonato createAbbonato(TipoPiano tipo, String nome, String cognome, String email,
                                          String residenza, String numeroTelefono) {
        return switch (tipo) {
            case BASE -> createAbbonatoBase(nome, cognome, email, residenza, numeroTelefono);
            case PLUS -> createAbbonatoPlus(nome, cognome, email, residenza, numeroTelefono);
        };
    }

    /**
     * Overload di compatibilità: converte una stringa al relativo enum.
     *
     * @param tipo il tipo di piano ("base" oppure "plus")
     * @param nome il nome dell'abbonato
     * @param cognome il cognome dell'abbonato
     * @param email l'email dell'abbonato
     * @param residenza la residenza dell'abbonato
     * @param numeroTelefono il numero di telefono
     * @return un nuovo abbonato del tipo specificato
     */
    public static Abbonato createAbbonato(String tipo, String nome, String cognome, String email,
                                          String residenza, String numeroTelefono) {
        return createAbbonato(TipoPiano.from(tipo), nome, cognome, email, residenza, numeroTelefono);
    }
}
