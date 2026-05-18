package patterns.command.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/*
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * @author / @version: Tracciano paternita' e manutenzione del contratto esposto.
 * @param: Definisce i vincoli di input attesi per garantire un uso sicuro del metodo.
 * @return: Esplicita il risultato promesso al Client e i limiti di validita' del valore.
 * @throws: Esplicita le eccezioni gestibili dal chiamante in caso di errore DB.
 */

/**
 * Questo file associa una promozione a un abbonato nella tabella di relazione.
 * L'operazione SQL e' incapsulata in un comando riusabile e isolato.
 * Il pattern Command separa chi invoca l'azione da chi esegue il dettaglio SQL.
 *
 * @author ParthEll Team
 * @version 1.0
 */
public final class InsertAbbonatoPromozioneCommand implements DatabaseCommand<Integer> {

    private static final String SQL = "INSERT OR IGNORE INTO abbonato_promozione(email, promozione_nome) VALUES (?, ?)";

    private final String email;
    private final String promozioneNome;

    /**
     * Costruisce il comando con i dati minimi per creare l'associazione abbonato-promozione.
     * I parametri vengono salvati e usati al momento dell'esecuzione.
     *
     * @param email email dell'abbonato; deve riferirsi a un record valido.
     * @param promozioneNome nome promozione da associare all'abbonato.
     */
    public InsertAbbonatoPromozioneCommand(String email, String promozioneNome) {
        // Salvo i dati nel comando per poterlo eseguire in un secondo momento.
        this.email = email;
        this.promozioneNome = promozioneNome;
    }

    // Sicurezza: obbliga Java a verificare che sto davvero implementando execute() promesso all'interfaccia DatabaseCommand, evitando errori di firma.
    @Override
    /**
     * Esegue l'INSERT OR IGNORE dell'associazione tra abbonato e promozione.
     * Non restituisce mai null: ritorna 1 se inserisce, 0 se la coppia era gia' presente.
     *
     * @param connection connessione JDBC gia' aperta e valida per l'operazione.
     * @return numero di righe modificate: 1 nuova associazione, 0 nessuna modifica.
     * @throws SQLException se la preparazione o l'esecuzione della query fallisce.
     */

    // questo blocco serve a preparare la query SQL in modo sicuro, evitando problemi di SQL injection, e a gestire il risultato in modo robusto.
   public Integer execute(Connection connection) throws SQLException {         // Connection è un'interfaccia di JDBC che rappresenta una connessione al database. Viene passata al comando per eseguire l'operazione sul database.

    System.out.println("[ATTO DB - COMMAND " + this.getClass().getSimpleName().toUpperCase() + "] Eseguo l'operazione SQL incapsulata nel pattern Command.");
    
        try (PreparedStatement statement = connection.prepareStatement(SQL)) {  // PreparedStatement fa parte di JDBC e dice quale "forma" deve avere la variabile ('statement') che stiamo creando. 
                                                                                // In questo caso, ordina di creare un oggetto capace di contenere query SQL in modo sicuro.
                                                                                // prepareStatement è un metodo che si trova dentro l'oggetto Connection e serve a creare un PreparedStatement a partire da una stringa SQL con parametri (?).
            
            // Lego i 2 parametri in ordine al posto dei punti interrogativi (?).
            statement.setString(1, email);
            statement.setString(2, promozioneNome);
            
            // Eseguo l'INSERT OR IGNORE sul database.
            // executeUpdate() restituisce un intero: in questo caso 1 se la riga viene aggiunta, oppure 0 se l'utente aveva già questa promozione (perché SQL 'IGNORE' blocca i doppioni).
            return statement.executeUpdate();
        }
    }
}