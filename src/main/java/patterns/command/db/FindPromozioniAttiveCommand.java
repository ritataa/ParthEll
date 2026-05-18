package patterns.command.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * @author / @version: Tracciano paternita' e manutenzione del contratto esposto.
 * @param: Definisce i vincoli di input attesi per garantire un uso sicuro del metodo.
 * @return: Esplicita il risultato promesso al Client e i limiti di validita' del valore.
 * @throws: Dichiarata le eccezioni gestibili dal chiamante quando l'operazione puo' fallire.
 */

/**
 * Command concreto che legge le promozioni attive di un abbonato come stringa CSV.
 * Recupera dal database l'elenco delle promozioni associate all'email richiesta.
 * Usa il pattern Command per incapsulare la lettura SQL dietro un contratto unico e riusabile.
 *
 * @author ParthEll Team
 * @version 1.0
 */
// Risolviamo il "Jolly" <T> dell'interfaccia impostandolo a <String>, perché 
// questo specifico comando restituisce le promozioni sotto forma di testo unico (CSV).
public final class FindPromozioniAttiveCommand implements DatabaseCommand<String> { 

    private static final String SQL = """
        SELECT COALESCE(GROUP_CONCAT(promozione_nome, ', '), '') AS promo
        FROM abbonato_promozione
        WHERE email = ?
        """;

    private final String email;

    public FindPromozioniAttiveCommand(String email) {
        this.email = email;
    }

    // Sicurezza: @Override verifica che sto rispettando il metodo execute() promesso da DatabaseCommand.
    @Override
    /**
     * Esegue la query che raccoglie le promozioni attive dell'abbonato.
     * Ritorna una stringa CSV, oppure una stringa vuota se non ci sono risultati.
     *
     * @param connection connessione JDBC gia' aperta e valida per eseguire la query.
     * @return elenco promozioni in formato CSV, mai null; vuoto se il cliente non ha promozioni attive.
     * @throws SQLException se la query non puo' essere preparata o eseguita correttamente.
     */

    // questo blocco serve a preparare la query SQL in modo sicuro, evitando problemi di SQL injection, e a gestire il risultato in modo robusto.
    public String execute(Connection connection) throws SQLException {
        System.out.println("[ATTO DB - COMMAND " + this.getClass().getSimpleName().toUpperCase() + "] Eseguo l'operazione SQL incapsulata nel pattern Command.");
        // Preparo la query una sola volta e la lego ai valori dinamici in modo sicuro.
        try (PreparedStatement statement = connection.prepareStatement(SQL)) {          //PreparedStatement fa parte di JDBC e serve per eseguire query SQL in modo sicuro, evitando problemi di SQL injection.
            // Inserisco l'email come parametro, evitando concatenazioni SQL.
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {                      //ResultSet fa parte di JDBC e rappresenta il risultato di una query SQL. Permette di iterare sulle righe restituite dalla query.
                // Se il DB restituisce una riga, estraggo il CSV gia' aggregato.
                if (resultSet.next()) {
                    return resultSet.getString("promo");
                }
                // Nessun record: il contratto garantisce una stringa vuota, non null.
                return "";
            }
        }
    }
}
