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
 * Questo file inserisce un pagamento nel database in modo sicuro e parametrico.
 * Incapsula la logica dell'INSERT in un comando riusabile, separando il dettaglio SQL dal resto dell'applicazione.
 * Il pattern Command e' utile per uniformare le operazioni DB dietro un contratto comune di esecuzione.
 *
 * @author ParthEll Team
 * @version 1.0
 */
public final class InsertPagamentoCommand implements DatabaseCommand<Integer> {  // Integer = classe wrapper che ha al suo interno solo una variabile di tipo primitivo int

    private static final String SQL = "INSERT INTO pagamenti(id_abbonato, mese, anno, importo, stato, promo) VALUES (?, ?, ?, ?, ?, ?)";

    private final String idAbbonato;
    private final String mese;
    private final int anno;
    private final double importo;
    private final String stato;
    private final String promo;

    /**
     * Costruisce il comando con i dati necessari per registrare un pagamento.
     * I valori vengono salvati e riusati durante l'esecuzione della query.
     *
     * @param idAbbonato identificativo dell'abbonato associato al pagamento.
     * @param mese mese di riferimento del pagamento.
     * @param anno anno di riferimento del pagamento.
     * @param importo importo del pagamento da registrare.
     * @param stato stato del pagamento, per esempio pagato o pendente.
     * @param promo eventuale promozione applicata al pagamento.
     */
    public InsertPagamentoCommand(String idAbbonato, String mese, int anno, double importo, String stato, String promo) {
        // Salvo i parametri nel comando per eseguire l'INSERT in un secondo momento.
        this.idAbbonato = idAbbonato;
        this.mese = mese;
        this.anno = anno;
        this.importo = importo;
        this.stato = stato;
        this.promo = promo;
    }

    // Sicurezza: obbliga Java a verificare che sto davvero implementando execute() promesso all'interfaccia DatabaseCommand, evitando errori di firma.
    @Override
    /**
     * Esegue l'INSERT del pagamento usando i valori memorizzati nel comando.
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
            
            // Lego i 6 parametri in ordine al posto dei punti interrogativi (?).
            // Nota: per l'anno uso setInt e per l'importo uso setDouble perché sono numeri.
            statement.setString(1, idAbbonato);
            statement.setString(2, mese);
            statement.setInt(3, anno);
            statement.setDouble(4, importo);
            statement.setString(5, stato);
            statement.setString(6, promo);
            
            // Eseguo l'INSERT sul database.
            // executeUpdate() restituisce un intero: in questo caso 1 se il pagamento viene inserito con successo.
            return statement.executeUpdate();
        }
    }
}
