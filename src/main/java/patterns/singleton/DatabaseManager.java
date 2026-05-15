package patterns.singleton;

import java.sql.Connection;
import java.sql.SQLException;
// Connection: interfaccia Java che rappresenta una connessione aperta al database.
// SQLException: eccezione che rappresenta un errore che può accadere mentre parliamo col database.

/* LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * - @author / @version: Tracciano paternità e manutenzione
 * - @return: Definisce l'output garantito per la sicurezza del Client
 * - @throws: Esplicita le eccezioni gestibili dal chiamante
 */

/**
 * Espone accesso centralizzato alle connessioni al database per i repository.
 *
 * Ruolo architetturale: fa da punto di accesso centralizzato che espone
 * connessioni JDBC ai repository dell'applicazione e nasconde i dettagli di
 * gestione del driver. Implementa il pattern Singleton (eager) per fornire
 * un'unica istanza condivisa tra i client.
 *
 * Separazione delle responsabilità:
 *  DatabaseConnectionManager gestisce i dettagli tecnici del driver e dell'URL SQLite. 
 *  DatabaseManager fornisce un'interfaccia pulita ai Repository.
 * Se un domani decidessimo di passare da SQLite a un database MySQL, dovremmo modificare solo il file tecnico (ConnectionManager)
 * 
 * @author ParthEll Team
 * @version 1.0
 */
public final class DatabaseManager {

    private static final DatabaseConnectionManager CONNECTION_MANAGER = DatabaseConnectionManager.getInstance();    // collegamento a DatabaseConnectionManager (che contiene l'URL e i dettagli tecnici)
    private static final DatabaseManager INSTANCE = new DatabaseManager();

    private DatabaseManager() {
        // Costruttore privato per prevenire istanziazione esterna (Singleton)
    }

    /**
     * Restituisce l'istanza unica del manager.
     * Garantisce accesso globale e coerente ai repository.
     *
     * @return l'istanza singleton di DatabaseManager (non-null)
     */
    public static DatabaseManager getInstance() {
        // Ritorna l'istanza eager già inizializzata
        return INSTANCE;
    }

    /**
     * Fornisce una connessione al database delegando al DatabaseConnectionManager.
     * Lancia SQLException se la connessione non può essere creata.
     *
     * @return una Connection valida al database (non-null)
     * @throws SQLException se la connessione al database fallisce
     */
    public Connection getConnection() throws SQLException {
        // Delega la creazione della Connection al manager responsabile del driver
        return CONNECTION_MANAGER.getConnection();
    }
}

