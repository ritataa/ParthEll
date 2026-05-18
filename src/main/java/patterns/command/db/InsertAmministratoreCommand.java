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
 * Questo file inserisce un amministratore nel database in modo sicuro e controllato.
 * Incapsula l'INSERT SQL in un comando riusabile, separando la logica applicativa dall'accesso ai dati.
 * Il pattern Command viene usato per avere un contratto uniforme di esecuzione per le operazioni DB.
 *
 * @author ParthEll Team
 * @version 1.0
 */
public final class InsertAmministratoreCommand implements DatabaseCommand<Integer> {   // Integer = classe wrapper che ha al suo interno solo una variabile di tipo primitivo int

    private static final String SQL = "INSERT INTO amministratore(email, password, ruolo, nome, cognome) VALUES (?, ?, ?, ?, ?)";

    private final String email;
    private final String password;
    private final String ruolo;
    private final String nome;
    private final String cognome;

    /**
     * Costruisce il comando con i dati necessari per inserire un amministratore.
     * I valori vengono salvati e usati poi durante l'esecuzione SQL.
     *
     * @param email email dell'amministratore; deve riferirsi a un valore valido e univoco.
     * @param password password dell'amministratore da memorizzare.
     * @param ruolo ruolo assegnato all'amministratore nel sistema.
     * @param nome nome proprio dell'amministratore.
     * @param cognome cognome dell'amministratore.
     */
    public InsertAmministratoreCommand(String email, String password, String ruolo, String nome, String cognome) {
        // Salvo i parametri nel comando per eseguirli in seguito senza ricostruire la query.
        this.email = email;
        this.password = password;
        this.ruolo = ruolo;
        this.nome = nome;
        this.cognome = cognome;
    }

    // Sicurezza: obbliga Java a verificare che sto davvero implementando execute() promesso all'interfaccia DatabaseCommand, evitando errori di firma.
    @Override
    /**
     * Esegue l'INSERT dell'amministratore usando i valori salvati nel comando.
     * Restituisce quante righe sono state inserite nel database.
     *
     * @param connection connessione JDBC gia' aperta e valida per l'operazione.
     * @return numero di righe inserite: 1 se l'operazione riesce, 0 se non modifica il database.
     * @throws SQLException se la query non puo' essere preparata o eseguita correttamente.
     */

    // questo blocco serve a evitare problemi di sicurezza come cancellare o rubare i dati (SQL injection) e 
    // a non consumare RAM inutilmente (try-with-resources chiude automaticamente statement anche in caso di eccezione).
    public Integer execute(Connection connection) throws SQLException {         // Connection è un'interfaccia di JDBC che rappresenta una connessione al database. Viene passata al comando per eseguire l'operazione sul database.
        System.out.println("[ATTO DB - COMMAND " + this.getClass().getSimpleName().toUpperCase() + "] Eseguo l'operazione SQL incapsulata nel pattern Command.");
        try (PreparedStatement statement = connection.prepareStatement(SQL)) {  // PreparedStatement fa parte di JDBC e dice quale "forma" deve avere la variabile ('statement') che stiamo creando. 
                                                                                // In questo caso, ordina di creare un oggetto capace di contenere query SQL in modo sicuro.
                                                                                // prepareStatement è un metodo che si trova dentro l'oggetto Connection e serve a creare un PreparedStatement a partire da una stringa SQL con parametri (?).
            
            // Lego i 5 parametri in ordine al posto dei punti interrogativi (?).
            statement.setString(1, email);
            statement.setString(2, password);
            statement.setString(3, ruolo);
            statement.setString(4, nome);
            statement.setString(5, cognome);
            
            // Eseguo l'INSERT sul database.
            // executeUpdate() restituisce un intero: in questo caso 1 se l'amministratore viene inserito con successo.
            return statement.executeUpdate();
        }
    }
}
