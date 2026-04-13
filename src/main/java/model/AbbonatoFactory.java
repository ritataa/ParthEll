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
        return Abbonato.builder()
            .nome(nome)
            .cognome(cognome)
            .email(email)
            .residenza(residenza)
            .numeroTelefono(numeroTelefono)
            .pianoTariffario("Basic")
            .build();
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
        return Abbonato.builder()
            .nome(nome)
            .cognome(cognome)
            .email(email)
            .residenza(residenza)
            .numeroTelefono(numeroTelefono)
            .pianoTariffario("Premium")
            .build();
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
        return switch (tipo.toLowerCase()) {
            case "basic" -> createAbbonatoBasic(nome, cognome, email, residenza, numeroTelefono);
            case "premium" -> createAbbonatorPremium(nome, cognome, email, residenza, numeroTelefono);
            case "super" -> Abbonato.builder()
                .nome(nome)
                .cognome(cognome)
                .email(email)
                .residenza(residenza)
                .numeroTelefono(numeroTelefono)
                .pianoTariffario("Super Plus")
                .build();
            default -> throw new IllegalArgumentException("Tipo di abbonato non riconosciuto: " + tipo);
        };
    }
}
