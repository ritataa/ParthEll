package model;

/**
 * Factory per la creazione di diversi tipi di abbonati.
 * Implementa il pattern Factory Method per creare istanze di Abbonato
 * con configurazioni predefinite basate sul tipo di piano tariffario.
 * 
 * @author ParthEll Team
 * @version 1.0
 */
public class AbbonatoFactory {
    
    /**
     * Crea un abbonato con piano Basic predefinito.
     * 
     * @param nome il nome dell'abbonato
     * @param cognome il cognome dell'abbonato
     * @param email l'email dell'abbonato
     * @param residenza la residenza dell'abbonato
     * @param numeroTelefono il numero di telefono
     * @return un nuovo abbonato con piano Basic
     */
    public static Abbonato createAbbonatoBasic(String nome, String cognome, String email, 
                                             String residenza, String numeroTelefono) {
        return new Abbonato(nome, cognome, email, residenza, numeroTelefono, "Basic", "ricaricabile");
    }
    
    /**
     * Crea un abbonato con piano Premium predefinito.
     * 
     * @param nome il nome dell'abbonato
     * @param cognome il cognome dell'abbonato
     * @param email l'email dell'abbonato
     * @param residenza la residenza dell'abbonato
     * @param numeroTelefono il numero di telefono
     * @return un nuovo abbonato con piano Premium
     */
    public static Abbonato createAbbonatorPremium(String nome, String cognome, String email, 
                                                String residenza, String numeroTelefono) {
        return new Abbonato(nome, cognome, email, residenza, numeroTelefono, "Premium", "fisso");
    }
    
    /**
     * Crea un abbonato basato sul tipo di piano specificato.
     * 
     * @param tipo il tipo di piano ("basic", "premium", "super")
     * @param nome il nome dell'abbonato
     * @param cognome il cognome dell'abbonato
     * @param email l'email dell'abbonato
     * @param residenza la residenza dell'abbonato
     * @param numeroTelefono il numero di telefono
     * @return un nuovo abbonato del tipo specificato
     * @throws IllegalArgumentException se il tipo non è riconosciuto
     */
    public static Abbonato createAbbonato(String tipo, String nome, String cognome, String email, 
                                        String residenza, String numeroTelefono) {
        switch (tipo.toLowerCase()) {
            case "basic":
                return createAbbonatoBasic(nome, cognome, email, residenza, numeroTelefono);
            case "premium":
                return createAbbonatorPremium(nome, cognome, email, residenza, numeroTelefono);
            case "super":
                return new Abbonato(nome, cognome, email, residenza, numeroTelefono, "Super Plus", "ricaricabile");
            default:
                throw new IllegalArgumentException("Tipo di abbonato non riconosciuto: " + tipo);
        }
    }
}
