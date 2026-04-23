package patterns.singleton;

import java.sql.Connection;
import java.sql.SQLException;
// Connection: interfaccia Java che rappresenta una connessione aperta al database.
// SQLException: eccezione che rappresenta un errore che può accadere mentre parliamo col database.

/**
 * Espone accesso centralizzato alle connessioni DB per i repository.
 */
public final class DatabaseManager {

    private static final DatabaseConnectionManager CONNECTION_MANAGER = DatabaseConnectionManager.getInstance();
    private static final DatabaseManager INSTANCE = new DatabaseManager();

    private DatabaseManager() {
    }

    // Punto di accesso globale all'unica istanza del manager.
    public static DatabaseManager getInstance() {
        return INSTANCE;
    }

    public Connection getConnection() throws SQLException {
        return CONNECTION_MANAGER.getConnection();
    }
}

