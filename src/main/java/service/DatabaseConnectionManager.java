package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestisce esclusivamente la connessione SQLite tramite Singleton GoF.
 */
public final class DatabaseConnectionManager {

    // URL del database SQLite locale usato dall'app.
    private static final String DB_URL = "jdbc:sqlite:parth.db";

    // Singleton eager: istanza unica creata al caricamento della classe.
    private static final DatabaseConnectionManager INSTANCE = new DatabaseConnectionManager();

    private DatabaseConnectionManager() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException exception) {
            throw new IllegalStateException("Driver SQLite non disponibile", exception);
        }
    }

    
    public static DatabaseConnectionManager getInstance() {
        return INSTANCE;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}
