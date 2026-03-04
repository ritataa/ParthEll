package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Gestisce la connessione SQLite locale e l'inizializzazione dello schema.
 */
public final class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:parth.db";
    private static final DatabaseManager INSTANCE = new DatabaseManager();

    private DatabaseManager() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException exception) {
            throw new IllegalStateException("Driver SQLite non disponibile", exception);
        }
    }

    public static DatabaseManager getInstance() {
        return INSTANCE;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public void initializeDatabase() {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
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
                CREATE TABLE IF NOT EXISTS utilizzo (
                    numero TEXT PRIMARY KEY,
                    chiamate INTEGER NOT NULL,
                    sms INTEGER NOT NULL,
                    dati INTEGER NOT NULL
                )
                """);

            migrateLegacyPromoData(connection);
            seedDataIfNeeded(connection);
            normalizePianiAbbonati(connection);
        } catch (SQLException exception) {
            throw new RuntimeException("Errore durante inizializzazione database", exception);
        }
    }

    private void seedDataIfNeeded(Connection connection) throws SQLException {
        if (isTableEmpty(connection, "piano_tariffario")) {
            try (PreparedStatement statement = connection.prepareStatement(
                """
                INSERT INTO piano_tariffario(nome, minuti_mensili, giga_mensili, illimitato_minuti, illimitato_giga, costo_mensile)
                VALUES (?, ?, ?, ?, ?, ?)
                """)) {
                insertPianoTariffarioSeed(statement, "base", 300, 300, false, false, 7.0);
                insertPianoTariffarioSeed(statement, "plus", null, null, true, true, 15.0);
            }
        }

        if (isTableEmpty(connection, "amministratore")) {
            try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO amministratore(email, password, ruolo, nome, cognome) VALUES (?, ?, ?, ?, ?)")) {
                statement.setString(1, "mrossi@parthell.it");
                statement.setString(2, "admin123");
                statement.setString(3, "admin");
                statement.setString(4, "Mario");
                statement.setString(5, "Rossi");
                statement.executeUpdate();
            }
        }

        if (isTableEmpty(connection, "abbonato")) {
            try (PreparedStatement statement = connection.prepareStatement(
                """
                INSERT INTO abbonato(email, password, nome, cognome, residenza, numero_telefono, piano_tariffario, conto, saldo)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """)) {
                insertAbbonatoSeed(statement,
                    "anna@gmail.com", "anna123", "Anna", "Rosa", "Salerno", "3339988776", "plus", "ricaricabile", 10.0);
                insertAbbonatoSeed(statement,
                    "sara@gmail.com", "sara123", "Sara", "Rossi", "Milano", "3364384733", "plus", "ricaricabile", 18.0);
            }
        }

        if (isTableEmpty(connection, "promozione")) {
            try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO promozione(nome, costo, descrizione) VALUES (?, ?, ?)")) {
                insertPromozioneSeed(statement, "Netflix", 9.99, "Abbonamento mensile a Netflix");
                insertPromozioneSeed(statement, "Amazon Prime", 9.99, "Abbonamento mensile ad Amazon Prime");
                insertPromozioneSeed(statement, "Disney+", 9.99, "Abbonamento mensile a Disney+");
                insertPromozioneSeed(statement, "Dazn", 9.99, "Abbonamento mensile a Dazn");
            }
        }

        if (isTableEmpty(connection, "utilizzo")) {
            try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO utilizzo(numero, chiamate, sms, dati) VALUES (?, ?, ?, ?)")) {
                insertUtilizzoSeed(statement, "3339988776", 50, 30, 20);
                insertUtilizzoSeed(statement, "3364384733", 20, 50, 30);
            }
        }

        if (isTableEmpty(connection, "abbonato_promozione")) {
            try (PreparedStatement statement = connection.prepareStatement(
                "INSERT OR IGNORE INTO abbonato_promozione(email, promozione_nome) VALUES (?, ?)")) {
                insertAbbonatoPromozioneSeed(statement, "anna@gmail.com", "Netflix");
                insertAbbonatoPromozioneSeed(statement, "sara@gmail.com", "Amazon Prime");
            }
        }
    }

    private boolean isTableEmpty(Connection connection, String tableName) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM " + tableName);
             ResultSet resultSet = statement.executeQuery()) {
            return resultSet.next() && resultSet.getInt(1) == 0;
        }
    }

    private void insertAbbonatoSeed(
        PreparedStatement statement,
        String email,
        String password,
        String nome,
        String cognome,
        String residenza,
        String numeroTelefono,
        String piano,
        String conto,
        double saldo
    ) throws SQLException {
        statement.setString(1, email);
        statement.setString(2, password);
        statement.setString(3, nome);
        statement.setString(4, cognome);
        statement.setString(5, residenza);
        statement.setString(6, numeroTelefono);
        statement.setString(7, piano);
        statement.setString(8, conto);
        statement.setDouble(9, saldo);
        statement.executeUpdate();
    }

    private void insertPromozioneSeed(PreparedStatement statement, String nome, double costo, String descrizione) throws SQLException {
        statement.setString(1, nome);
        statement.setDouble(2, costo);
        statement.setString(3, descrizione);
        statement.executeUpdate();
    }

    private void insertPianoTariffarioSeed(
        PreparedStatement statement,
        String nome,
        Integer minutiMensili,
        Integer gigaMensili,
        boolean illimitatoMinuti,
        boolean illimitatoGiga,
        double costoMensile
    ) throws SQLException {
        statement.setString(1, nome);
        if (minutiMensili == null) {
            statement.setNull(2, java.sql.Types.INTEGER);
        } else {
            statement.setInt(2, minutiMensili);
        }
        if (gigaMensili == null) {
            statement.setNull(3, java.sql.Types.INTEGER);
        } else {
            statement.setInt(3, gigaMensili);
        }
        statement.setInt(4, illimitatoMinuti ? 1 : 0);
        statement.setInt(5, illimitatoGiga ? 1 : 0);
        statement.setDouble(6, costoMensile);
        statement.executeUpdate();
    }

    private void insertUtilizzoSeed(PreparedStatement statement, String numero, int chiamate, int sms, int dati) throws SQLException {
        statement.setString(1, numero);
        statement.setInt(2, chiamate);
        statement.setInt(3, sms);
        statement.setInt(4, dati);
        statement.executeUpdate();
    }

    private void insertAbbonatoPromozioneSeed(PreparedStatement statement, String email, String nomePromozione) throws SQLException {
        statement.setString(1, email);
        statement.setString(2, nomePromozione);
        statement.executeUpdate();
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

        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                CREATE TABLE IF NOT EXISTS utilizzo_new (
                    numero TEXT PRIMARY KEY,
                    chiamate INTEGER NOT NULL,
                    sms INTEGER NOT NULL,
                    dati INTEGER NOT NULL
                )
                """);
            statement.execute("INSERT OR REPLACE INTO utilizzo_new(numero, chiamate, sms, dati) SELECT numero, chiamate, sms, dati FROM utilizzo");
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
