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
 * Questo file inserisce una promozione nel database in modo sicuro e parametrico.
 * Incapsula la logica dell'INSERT dietro un comando riusabile e facile da invocare.
 * Il pattern Command uniforma le operazioni DB dietro un contratto comune di esecuzione.
 *
 * @author ParthEll Team
 * @version 1.0
 */
public final class InsertPromozioneCommand implements DatabaseCommand<Integer> {

    private static final String SQL = "INSERT INTO promozione(nome, costo, descrizione) VALUES (?, ?, ?)";

    private final String nome;
    private final double costo;
    private final String descrizione;

    /**
     * Costruisce il comando con i dati necessari per inserire una promozione.
     * I valori vengono salvati e usati durante l'esecuzione della query.
     *
     * @param nome nome della promozione da inserire.
     * @param costo costo della promozione.
     * @param descrizione descrizione testuale della promozione.
     */
    public InsertPromozioneCommand(String nome, double costo, String descrizione) {
        // Salvo i parametri nel comando per poter eseguire l'INSERT in un secondo momento.
        this.nome = nome;
        this.costo = costo;
        this.descrizione = descrizione;
    }

    // Sicurezza: obbliga Java a verificare che sto davvero implementando execute() promesso all'interfaccia DatabaseCommand, evitando errori di firma.
    @Override
    /**
     * Esegue l'INSERT della promozione usando i valori memorizzati nel comando.
     * Restituisce quante righe sono state inserite nel database.
     *
     * @param connection connessione JDBC gia' aperta e valida per l'operazione.
     * @return numero di righe inserite: 1 se l'operazione riesce, 0 se non modifica il database.
     * @throws SQLException se la query non puo' essere preparata o eseguita correttamente.
     */

    // questo blocco serve a evitare problemi di sicurezza come cancellare o rubare i dati (SQL injection) e 
    // a non consumare RAM inutilmente (try-with-resources chiude automaticamente statement anche in caso di eccezione).
    public Integer execute(Connection connection) throws SQLException {         // Connection è un'interfaccia di JDBC che rappresenta una connessione al database. Viene passata al comando per eseguire l'operazione sul database.

        try (PreparedStatement statement = connection.prepareStatement(SQL)) {  // PreparedStatement fa parte di JDBC e dice quale "forma" deve avere la variabile ('statement') che stiamo creando. 
                                                                                // In questo caso, ordina di creare un oggetto capace di contenere query SQL in modo sicuro.
                                                                                // prepareStatement è un metodo che si trova dentro l'oggetto Connection e serve a creare un PreparedStatement a partire da una stringa SQL con parametri (?).
            
            // Lego i 3 parametri in ordine al posto dei punti interrogativi (?).
            // Nota: per il costo uso setDouble perché è un numero con la virgola.
            statement.setString(1, nome);
            statement.setDouble(2, costo);
            statement.setString(3, descrizione);
            
            // Eseguo l'INSERT sul database.
            // executeUpdate() restituisce un intero: in questo caso 1 se la promozione viene inserita con successo.
            return statement.executeUpdate();
        }
    }
}
