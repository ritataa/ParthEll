package patterns.factory;

import model.TipoPiano;
import patterns.builder.Abbonato;

/*
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * @author / @version: Tracciano paternita' e manutenzione della classe.
 * @param: Definisce i vincoli di input e le attese sui parametri.
 * @return: Definisce l'output garantito per il Client.
 * @throws: Esplicita le eccezioni gestibili dal chiamante.
 */

/**
 * Factory per la creazione di diversi tipi di abbonati.
 * Implementa il pattern Factory Simple per creare istanze di Abbonato
 * con configurazioni predefinite basate sul tipo di piano tariffario.
 * 
 * 
 * @author ParthEll Team
 * @version 1.0
 */
public class AbbonatoFactory {
    
    /**
     * Crea un abbonato con piano base predefinito.
     *
     * @param nome vincolo: nome da assegnare all'abbonato
     * @param cognome vincolo: cognome da assegnare all'abbonato
     * @param email vincolo: email da assegnare all'abbonato
     * @param residenza vincolo: residenza da assegnare all'abbonato
     * @param numeroTelefono vincolo: numero di telefono da assegnare all'abbonato
     * @return un nuovo abbonato con piano base, mai null
     */
    private static Abbonato createAbbonatoBase(String nome, String cognome, String email, String residenza, String numeroTelefono) {
        // Riusa il builder per mantenere coerenti i campi comuni.
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
     * @param nome vincolo: nome da assegnare all'abbonato
     * @param cognome vincolo: cognome da assegnare all'abbonato
     * @param email vincolo: email da assegnare all'abbonato
     * @param residenza vincolo: residenza da assegnare all'abbonato
     * @param numeroTelefono vincolo: numero di telefono da assegnare all'abbonato
     * @return un nuovo abbonato con piano plus, mai null
     */
    private static Abbonato createAbbonatoPlus(String nome, String cognome, String email, String residenza, String numeroTelefono) {
        // Cambia solo il piano tariffario, non la struttura dell'oggetto.
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
     * Converte una stringa nel piano enum e delega alla factory tipizzata.
     *
     * Prende il tipo di piano sotto forma di testo semplice ("BASE" o "PLUS") e la trasforma nell'oggetto sicuro TipoPiano (l'Enum) 
     * usando il comando TipoPiano.from(tipo) 
     * 
     * @param tipo vincolo: valore testuale del piano, come base o plus
     * @param nome vincolo: nome da assegnare all'abbonato
     * @param cognome vincolo: cognome da assegnare all'abbonato
     * @param email vincolo: email da assegnare all'abbonato
     * @param residenza vincolo: residenza da assegnare all'abbonato
     * @param numeroTelefono vincolo: numero di telefono da assegnare all'abbonato
     * @return un nuovo abbonato del tipo specificato, mai null
     * @throws IllegalArgumentException se tipo e' nullo o non valido
     */
    public static Abbonato createAbbonato(String tipo, String nome, String cognome, String email, String residenza, String numeroTelefono) {
        System.out.println("[ATTO 1 - 2. FACTORY ABBONATO FACTORY] Creo l'abbonato in base al tipo piano richiesto dall'utente.");
        // Normalizza la stringa prima di delegare alla variante tipizzata.
        return createAbbonato(TipoPiano.from(tipo), nome, cognome, email, residenza, numeroTelefono);
    }

    /**
     * Crea un abbonato basato sul tipo di piano specificato.
     *
     * Riceve l'Enum e usa lo switch per decidere cosa chiamare (createAbbonatoBase o createAbbonatoPlus)
     * 
     * @param tipo vincolo: piano richiesto tra BASE e PLUS
     * @param nome vincolo: nome da assegnare all'abbonato
     * @param cognome vincolo: cognome da assegnare all'abbonato
     * @param email vincolo: email da assegnare all'abbonato
     * @param residenza vincolo: residenza da assegnare all'abbonato
     * @param numeroTelefono vincolo: numero di telefono da assegnare all'abbonato
     * @return un nuovo abbonato del tipo specificato, mai null
     * @throws NullPointerException se tipo e' null
     */
    public static Abbonato createAbbonato(TipoPiano tipo, String nome, String cognome, String email, String residenza, String numeroTelefono) {
        // Lo switch seleziona la variante concreta in base al piano.
        return switch (tipo) {
            case BASE -> createAbbonatoBase(nome, cognome, email, residenza, numeroTelefono);
            case PLUS -> createAbbonatoPlus(nome, cognome, email, residenza, numeroTelefono);
        };
    }
}
