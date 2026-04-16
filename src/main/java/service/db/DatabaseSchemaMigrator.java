package service.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Gestisce creazione schema e migrazioni strutturali del database.
 */
public final class DatabaseSchemaMigrator {

    public void migrate(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                CREATE TABLE IF NOT EXISTS amministratore (
                    email TEXT PRIMARY KEY,
                    password TEXT NOT NULL,
                    ruolo TEXT NOT NULL,
                    nome TEXT,
                    cognome TEXT
                )
                """);

            statement.execute("""
                CREATE TABLE IF NOT EXISTS piano_tariffario (
                    nome TEXT PRIMARY KEY,
                    minuti_mensili INTEGER,
                    giga_mensili INTEGER,
                    illimitato_minuti INTEGER NOT NULL DEFAULT 0,
                    illimitato_giga INTEGER NOT NULL DEFAULT 0,
                    costo_mensile REAL NOT NULL
                )
                """);

            statement.execute("""
                CREATE TABLE IF NOT EXISTS abbonato (
                    email TEXT PRIMARY KEY,
                    password TEXT NOT NULL,
                    nome TEXT NOT NULL,
                    cognome TEXT NOT NULL,
                    residenza TEXT,
                    numero_telefono TEXT UNIQUE NOT NULL,
                    piano_tariffario TEXT,
                    conto TEXT,
                    saldo REAL DEFAULT 0
                )
                """);

            statement.execute("""
                CREATE TABLE IF NOT EXISTS promozione (
                    nome TEXT PRIMARY KEY,
                    costo REAL NOT NULL,
                    descrizione TEXT NOT NULL
                )
                """);

            statement.execute("""
                CREATE TABLE IF NOT EXISTS abbonato_promozione (
                    email TEXT NOT NULL,
                    promozione_nome TEXT NOT NULL,
                    attiva_dal TEXT DEFAULT CURRENT_DATE,
                    PRIMARY KEY (email, promozione_nome)
                )
                """);

            statement.execute("""
                CREATE TABLE IF NOT EXISTS pagamenti (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    id_abbonato TEXT NOT NULL,
                    mese TEXT NOT NULL,
                    anno INTEGER NOT NULL,
                    importo REAL NOT NULL,
                    stato TEXT NOT NULL,
                    promo TEXT,
                    FOREIGN KEY (id_abbonato) REFERENCES abbonato(email)
                )
                """);

            statement.execute("""
                CREATE TABLE IF NOT EXISTS utilizzo (
                    numero TEXT PRIMARY KEY,
                    nome TEXT,
                    cognome TEXT,
                    email TEXT,
                    chiamate INTEGER NOT NULL,
                    sms INTEGER NOT NULL,
                    dati INTEGER NOT NULL
                )
                """);
        }

        migratePagamentiPromo(connection);
        migrateLegacyPromoData(connection);
        migrateUtilizzoAnagrafica(connection);
        normalizePianiAbbonati(connection);
    }

    private void migratePagamentiPromo(Connection connection) throws SQLException {
        if (hasColumn(connection, "pagamenti", "promo")) {
            return;
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute("ALTER TABLE pagamenti ADD COLUMN promo TEXT");
        }

        try (PreparedStatement statement = connection.prepareStatement("""
            UPDATE pagamenti
            SET promo = COALESCE((
                SELECT GROUP_CONCAT(ap.promozione_nome, ', ')
                FROM abbonato_promozione ap
                WHERE ap.email = pagamenti.id_abbonato
            ), '')
            WHERE promo IS NULL OR TRIM(promo) = ''
            """)) {
            statement.executeUpdate();
        }
    }

    private void migrateLegacyPromoData(Connection connection) throws SQLException {
        if (!hasColumn(connection, "utilizzo", "promo")) {
            return;
        }

        try (PreparedStatement migratePromoStatement = connection.prepareStatement("""
            INSERT OR IGNORE INTO abbonato_promozione(email, promozione_nome)
            SELECT a.email, TRIM(u.promo)
            FROM utilizzo u
            JOIN abbonato a ON a.numero_telefono = u.numero
            WHERE u.promo IS NOT NULL AND TRIM(u.promo) <> ''
            """)) {
            migratePromoStatement.executeUpdate();
        }

        rebuildUtilizzoAnagrafica(connection);
    }

    private void migrateUtilizzoAnagrafica(Connection connection) throws SQLException {
        if (hasColumn(connection, "utilizzo", "nome")
            && hasColumn(connection, "utilizzo", "cognome")
            && hasColumn(connection, "utilizzo", "email")) {
            return;
        }

        rebuildUtilizzoAnagrafica(connection);
    }

    private void rebuildUtilizzoAnagrafica(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                CREATE TABLE IF NOT EXISTS utilizzo_new (
                    numero TEXT PRIMARY KEY,
                    nome TEXT,
                    cognome TEXT,
                    email TEXT,
                    chiamate INTEGER NOT NULL,
                    sms INTEGER NOT NULL,
                    dati INTEGER NOT NULL
                )
                """);
            statement.execute("""
                INSERT OR REPLACE INTO utilizzo_new(numero, nome, cognome, email, chiamate, sms, dati)
                SELECT u.numero, a.nome, a.cognome, a.email, u.chiamate, u.sms, u.dati
                FROM utilizzo u
                LEFT JOIN abbonato a ON a.numero_telefono = u.numero
                """);
            statement.execute("DROP TABLE utilizzo");
            statement.execute("ALTER TABLE utilizzo_new RENAME TO utilizzo");
        }
    }

    private boolean hasColumn(Connection connection, String tableName, String columnName) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("PRAGMA table_info(" + tableName + ")");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                if (columnName.equalsIgnoreCase(resultSet.getString("name"))) {
                    return true;
                }
            }
            return false;
        }
    }

    private void normalizePianiAbbonati(Connection connection) throws SQLException {
        try (PreparedStatement normalizeStatement = connection.prepareStatement(
            "UPDATE abbonato SET piano_tariffario = LOWER(TRIM(piano_tariffario)) WHERE piano_tariffario IS NOT NULL")) {
            normalizeStatement.executeUpdate();
        }

        try (PreparedStatement defaultStatement = connection.prepareStatement(
            "UPDATE abbonato SET piano_tariffario = 'base' WHERE piano_tariffario IS NULL OR TRIM(piano_tariffario) = ''")) {
            defaultStatement.executeUpdate();
        }
    }
}
