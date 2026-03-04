package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Abbonato;
import model.PianoTariffario;
import model.Promozione;
import model.Utilizzo;

/**
 * Repository JDBC per accesso ai dati applicativi.
 */
public class TelecomRepository {

    private final DatabaseManager databaseManager = DatabaseManager.getInstance();

    public String authenticate(String email, String password) {
        String adminSql = "SELECT 1 FROM amministratore WHERE email = ? AND password = ?";
        if (exists(adminSql, email, password)) {
            return "admin";
        }

        String clienteSql = "SELECT 1 FROM abbonato WHERE email = ? AND password = ?";
        if (exists(clienteSql, email, password)) {
            return "cliente";
        }

        return null;
    }

    public List<Abbonato> findAllAbbonati() {
        List<Abbonato> result = new ArrayList<>();
        String sql = """
            SELECT nome, cognome, email, residenza, numero_telefono, piano_tariffario, conto
            FROM abbonato
            ORDER BY cognome, nome
            """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                result.add(new Abbonato(
                    rs.getString("nome"),
                    rs.getString("cognome"),
                    rs.getString("email"),
                    rs.getString("residenza"),
                    rs.getString("numero_telefono"),
                    rs.getString("piano_tariffario"),
                    rs.getString("conto")
                ));
            }
            return result;
        } catch (SQLException exception) {
            throw new RuntimeException("Errore lettura abbonati", exception);
        }
    }

    public List<Utilizzo> findAllUtilizzi() {
        List<Utilizzo> result = new ArrayList<>();
        String sql = """
            SELECT
                u.numero,
                u.chiamate,
                u.sms,
                u.dati,
                COALESCE(GROUP_CONCAT(ap.promozione_nome, ', '), '') AS promo
            FROM utilizzo u
            LEFT JOIN abbonato a ON a.numero_telefono = u.numero
            LEFT JOIN abbonato_promozione ap ON ap.email = a.email
            GROUP BY u.numero, u.chiamate, u.sms, u.dati
            ORDER BY u.numero
            """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                result.add(new Utilizzo(
                    rs.getString("numero"),
                    rs.getInt("chiamate"),
                    rs.getInt("sms"),
                    rs.getInt("dati"),
                    rs.getString("promo")
                ));
            }
            return result;
        } catch (SQLException exception) {
            throw new RuntimeException("Errore lettura utilizzi", exception);
        }
    }

    public List<Promozione> findAllPromozioni() {
        List<Promozione> result = new ArrayList<>();
        String sql = "SELECT nome, costo, descrizione FROM promozione ORDER BY nome";

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                result.add(new Promozione(
                    rs.getString("nome"),
                    rs.getString("descrizione"),
                    rs.getDouble("costo")
                ));
            }
            return result;
        } catch (SQLException exception) {
            throw new RuntimeException("Errore lettura promozioni", exception);
        }
    }

    public List<String> findAllPianiTariffari() {
        List<String> result = new ArrayList<>();
        String sql = "SELECT nome FROM piano_tariffario ORDER BY nome";

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                result.add(rs.getString("nome"));
            }
            return result;
        } catch (SQLException exception) {
            throw new RuntimeException("Errore lettura piani tariffari", exception);
        }
    }

    public void addCliente(String email, String password, String nome, String cognome) {
        String sql = """
            INSERT INTO abbonato(email, password, nome, cognome, residenza, numero_telefono, piano_tariffario, conto, saldo)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            String numeroTelefono = generateNumeroTelefono(connection);
            statement.setString(1, email);
            statement.setString(2, password);
            statement.setString(3, nome);
            statement.setString(4, cognome);
            statement.setString(5, "N/D");
            statement.setString(6, numeroTelefono);
            statement.setString(7, "base");
            statement.setString(8, "ricaricabile");
            statement.setDouble(9, 0.0);
            statement.executeUpdate();
            createUtilizzoIfMissing(connection, numeroTelefono);
        } catch (SQLException exception) {
            throw new RuntimeException("Errore inserimento cliente", exception);
        }
    }

    public void registerCliente(
        String email,
        String password,
        String nome,
        String cognome,
        String residenza,
        String numeroTelefono,
        String pianoTariffario,
        String conto
    ) {
        String sql = """
            INSERT INTO abbonato(email, password, nome, cognome, residenza, numero_telefono, piano_tariffario, conto, saldo)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            String pianoNormalizzato = pianoTariffario.trim().toLowerCase();
            if (!existsPianoTariffario(connection, pianoNormalizzato)) {
                throw new RuntimeException("Piano tariffario non valido");
            }

            statement.setString(1, email.trim());
            statement.setString(2, password);
            statement.setString(3, nome.trim());
            statement.setString(4, cognome.trim());
            statement.setString(5, residenza.trim());
            statement.setString(6, numeroTelefono.trim());
            statement.setString(7, pianoNormalizzato);
            statement.setString(8, conto.trim());
            statement.setDouble(9, 0.0);
            statement.executeUpdate();
            createUtilizzoIfMissing(connection, numeroTelefono.trim());
        } catch (SQLException exception) {
            throw new RuntimeException("Errore registrazione cliente", exception);
        }
    }

    public boolean aderisciPromozione(String email, String nomePromozione) {
        String checkPromoSql = "SELECT 1 FROM promozione WHERE nome = ?";
        String insertSql = "INSERT OR IGNORE INTO abbonato_promozione(email, promozione_nome) VALUES (?, ?)";

        try (Connection connection = databaseManager.getConnection()) {
            try (PreparedStatement checkStatement = connection.prepareStatement(checkPromoSql)) {
                checkStatement.setString(1, nomePromozione);
                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (!resultSet.next()) {
                        throw new RuntimeException("Promozione non trovata");
                    }
                }
            }

            try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
                insertStatement.setString(1, email);
                insertStatement.setString(2, nomePromozione);
                return insertStatement.executeUpdate() > 0;
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Errore adesione promozione", exception);
        }
    }

    public Utilizzo findUtilizzoByEmail(String email) {
        String sql = """
            SELECT
                u.numero,
                u.chiamate,
                u.sms,
                u.dati,
                COALESCE(GROUP_CONCAT(ap.promozione_nome, ', '), '') AS promo
            FROM abbonato a
            LEFT JOIN utilizzo u ON u.numero = a.numero_telefono
            LEFT JOIN abbonato_promozione ap ON ap.email = a.email
            WHERE a.email = ?
            GROUP BY u.numero, u.chiamate, u.sms, u.dati
            """;

        try (Connection connection = databaseManager.getConnection()) {
            ensureUtilizzoByEmail(connection, email);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, email);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        return new Utilizzo(
                            rs.getString("numero"),
                            rs.getInt("chiamate"),
                            rs.getInt("sms"),
                            rs.getInt("dati"),
                            rs.getString("promo")
                        );
                    }
                }
            }
            return new Utilizzo("", 0, 0, 0, "");
        } catch (SQLException exception) {
            throw new RuntimeException("Errore lettura utilizzo cliente", exception);
        }
    }

    public List<String> findPromozioniAttiveByEmail(String email) {
        String sql = "SELECT promozione_nome FROM abbonato_promozione WHERE email = ? ORDER BY promozione_nome";
        List<String> result = new ArrayList<>();

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    result.add(rs.getString("promozione_nome"));
                }
            }
            return result;
        } catch (SQLException exception) {
            throw new RuntimeException("Errore lettura promozioni attive", exception);
        }
    }

    public String findNomeByEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }

        String sql = "SELECT nome FROM abbonato WHERE email = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("nome");
                }
                return null;
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Errore lettura nome cliente", exception);
        }
    }

    public PianoTariffario findPianoTariffarioByEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }

        String sql = """
            SELECT p.nome, p.minuti_mensili, p.giga_mensili, p.illimitato_minuti, p.illimitato_giga, p.costo_mensile
            FROM abbonato a
            JOIN piano_tariffario p ON p.nome = a.piano_tariffario
            WHERE a.email = ?
            """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new PianoTariffario(
                        resultSet.getString("nome"),
                        getNullableInteger(resultSet, "minuti_mensili"),
                        getNullableInteger(resultSet, "giga_mensili"),
                        resultSet.getInt("illimitato_minuti") == 1,
                        resultSet.getInt("illimitato_giga") == 1,
                        resultSet.getDouble("costo_mensile")
                    );
                }
                return null;
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Errore lettura piano tariffario", exception);
        }
    }

    public void registraChiamata(String email, int minuti) {
        if (minuti <= 0) {
            return;
        }
        String sql = """
            UPDATE utilizzo
            SET chiamate = chiamate + ?
            WHERE numero = (SELECT numero_telefono FROM abbonato WHERE email = ?)
            """;
        incrementUtilizzo(email, sql, minuti);
    }

    public void registraSms(String email) {
        String sql = """
            UPDATE utilizzo
            SET sms = sms + ?
            WHERE numero = (SELECT numero_telefono FROM abbonato WHERE email = ?)
            """;
        incrementUtilizzo(email, sql, 1);
    }

    public void registraDati(String email, int mb) {
        if (mb <= 0) {
            return;
        }
        String sql = """
            UPDATE utilizzo
            SET dati = dati + ?
            WHERE numero = (SELECT numero_telefono FROM abbonato WHERE email = ?)
            """;
        incrementUtilizzo(email, sql, mb);
    }

    private boolean exists(String sql, String email, String password) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Errore autenticazione", exception);
        }
    }

    private void incrementUtilizzo(String email, String updateSql, int value) {
        try (Connection connection = databaseManager.getConnection()) {
            ensureUtilizzoByEmail(connection, email);
            try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
                statement.setInt(1, value);
                statement.setString(2, email);
                statement.executeUpdate();
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Errore aggiornamento utilizzo", exception);
        }
    }

    private void ensureUtilizzoByEmail(Connection connection, String email) throws SQLException {
        String sql = """
            INSERT OR IGNORE INTO utilizzo(numero, chiamate, sms, dati)
            SELECT numero_telefono, 0, 0, 0
            FROM abbonato
            WHERE email = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            statement.executeUpdate();
        }
    }

    private void createUtilizzoIfMissing(Connection connection, String numeroTelefono) throws SQLException {
        String sql = "INSERT OR IGNORE INTO utilizzo(numero, chiamate, sms, dati) VALUES (?, 0, 0, 0)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, numeroTelefono);
            statement.executeUpdate();
        }
    }

    private String generateNumeroTelefono(Connection connection) throws SQLException {
        String sql = "SELECT COUNT(*) FROM abbonato";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            int count = resultSet.next() ? resultSet.getInt(1) : 0;
            return "39" + String.format("%08d", count + 1);
        }
    }

    private boolean existsPianoTariffario(Connection connection, String nomePiano) throws SQLException {
        String sql = "SELECT 1 FROM piano_tariffario WHERE nome = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nomePiano);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private Integer getNullableInteger(ResultSet resultSet, String columnName) throws SQLException {
        int value = resultSet.getInt(columnName);
        return resultSet.wasNull() ? null : value;
    }
}
