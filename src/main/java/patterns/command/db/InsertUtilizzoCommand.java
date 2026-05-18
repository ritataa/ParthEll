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
 * Questo file inserisce i dati di utilizzo traffico nel database in modo sicuro e parametrico.
 * Incapsula la logica dell'INSERT dietro un comando riusabile e facile da invocare.
 * Il pattern Command uniforma le operazioni DB dietro un contratto comune di esecuzione.
 *
 * @author ParthEll Team
 * @version 1.0
 */
public final class InsertUtilizzoCommand implements DatabaseCommand<Integer> {  // Integer = classe wrapper che ha al suo interno solo una variabile di tipo primitivo int

    private static final String SQL = "INSERT INTO utilizzo(numero, nome, cognome, email, chiamate, sms, dati) VALUES (?, ?, ?, ?, ?, ?, ?)";

    private final String numero;
    private final String nome;
    private final String cognome;
    private final String email;
    private final int chiamate;
    private final int sms;
    private final int dati;

    /**
     * Costruisce il comando con i dati necessari per registrare l'utilizzo traffico.
     * I valori vengono salvati e usati durante l'esecuzione della query.
     *
     * @param numero numero dell'utenza associata ai consumi.
     * @param nome nome dell'intestatario.
     * @param cognome cognome dell'intestatario.
     * @param email email dell'intestatario.
     * @param chiamate numero di chiamate effettuate.
     * @param sms numero di SMS inviati.
     * @param dati quantitativo di dati consumati.
     */
    public InsertUtilizzoCommand(String numero, String nome, String cognome, String email, int chiamate, int sms, int dati) {
        // Salvo i parametri nel comando per eseguire l'INSERT in un secondo momento.
        this.numero = numero;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.chiamate = chiamate;
        this.sms = sms;
        this.dati = dati;
    }

    // Sicurezza: obbliga Java a verificare che sto davvero implementando execute() promesso all'interfaccia DatabaseCommand, evitando errori di firma.
    @Override
    /**
     * Esegue l'INSERT dei dati di utilizzo usando i valori memorizzati nel comando.
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
            
            // Lego i 7 parametri in ordine al posto dei punti interrogativi (?).
            // Nota: per chiamate, sms e dati uso setInt perché sono numeri interi.
            statement.setString(1, numero);
            statement.setString(2, nome);
            statement.setString(3, cognome);
            statement.setString(4, email);
            statement.setInt(5, chiamate);
            statement.setInt(6, sms);
            statement.setInt(7, dati);
            
            // Eseguo l'INSERT sul database.
            // executeUpdate() restituisce un intero: in questo caso 1 se i dati di utilizzo vengono inseriti con successo.
            return statement.executeUpdate();
        }
    }
}
