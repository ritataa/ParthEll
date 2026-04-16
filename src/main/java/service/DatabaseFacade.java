package service;

import java.sql.Connection;
import java.sql.SQLException;

import service.db.DatabaseSchemaMigrator;
import service.db.DatabaseSeeder;

/**
 * Facade GoF per inizializzare il sottosistema database.
 */
public final class DatabaseFacade {

    private final DatabaseConnectionManager connectionManager;

    public DatabaseFacade() {
        this.connectionManager = DatabaseConnectionManager.getInstance();
    }

    public void initSystem() {
        DatabaseSchemaMigrator schemaMigrator = new DatabaseSchemaMigrator();
        DatabaseSeeder seeder = new DatabaseSeeder();

        try (Connection connection = connectionManager.getConnection()) {
            schemaMigrator.migrate(connection);
            seeder.seedDataIfNeeded(connection);
            seeder.seedPagamentiPerTutti(connection);
        } catch (SQLException exception) {
            throw new IllegalStateException("Errore durante inizializzazione del sistema database", exception);
        }
    }
}
