package service.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import patterns.command.db.FindPromozioniAttiveCommand;
import patterns.command.db.InsertAbbonatoCommand;
import patterns.command.db.InsertAbbonatoPromozioneCommand;
import patterns.command.db.InsertAmministratoreCommand;
import patterns.command.db.InsertPagamentoCommand;
import patterns.command.db.InsertPianoTariffarioCommand;
import patterns.command.db.InsertPromozioneCommand;
import patterns.command.db.InsertUtilizzoCommand;

/**
 * Gestisce il popolamento iniziale dei dati e i relativi controlli.
 */
public final class DatabaseSeeder {

    public void seedDataIfNeeded(Connection connection) throws SQLException {
        if (isTableEmpty(connection, "piano_tariffario")) {
            new InsertPianoTariffarioCommand("base", 300, 300, false, false, 7.0).execute(connection);
            new InsertPianoTariffarioCommand("plus", null, null, true, true, 15.0).execute(connection);
        }

        if (isTableEmpty(connection, "amministratore")) {
            new InsertAmministratoreCommand("mrossi@parthell.it", "admin123", "admin", "Mario", "Rossi")
                .execute(connection);
        }

        if (isTableEmpty(connection, "abbonato")) {
            new InsertAbbonatoCommand(
                "anna@gmail.com", "anna123", "Anna", "Rosa", "Salerno", "3339988776", "plus", "Ricaricabile", 10.0)
                .execute(connection);
            new InsertAbbonatoCommand(
                "sara@gmail.com", "sara123", "Sara", "Rossi", "Milano", "3364384733", "plus", "Ricaricabile", 18.0)
                .execute(connection);
        }

        if (isTableEmpty(connection, "promozione")) {
            new InsertPromozioneCommand("Netflix", 9.99, "Abbonamento mensile a Netflix").execute(connection);
            new InsertPromozioneCommand("Amazon Prime", 9.99, "Abbonamento mensile ad Amazon Prime").execute(connection);
            new InsertPromozioneCommand("Disney+", 9.99, "Abbonamento mensile a Disney+").execute(connection);
            new InsertPromozioneCommand("Dazn", 9.99, "Abbonamento mensile a Dazn").execute(connection);
        }

        if (isTableEmpty(connection, "utilizzo")) {
            new InsertUtilizzoCommand("3339988776", "Anna", "Rosa", "anna@gmail.com", 50, 30, 20)
                .execute(connection);
            new InsertUtilizzoCommand("3364384733", "Sara", "Rossi", "sara@gmail.com", 20, 50, 30)
                .execute(connection);
        }

        if (isTableEmpty(connection, "abbonato_promozione")) {
            new InsertAbbonatoPromozioneCommand("anna@gmail.com", "Netflix").execute(connection);
            new InsertAbbonatoPromozioneCommand("sara@gmail.com", "Amazon Prime").execute(connection);
        }
    }

    public void seedPagamentiPerTutti(Connection connection) throws SQLException {
        String selectEmailSql = "SELECT email FROM abbonato";
        String countSql = "SELECT COUNT(*) FROM pagamenti WHERE id_abbonato = ?";

        try (PreparedStatement selectStatement = connection.prepareStatement(selectEmailSql);
             ResultSet emailResultSet = selectStatement.executeQuery();
             PreparedStatement countStatement = connection.prepareStatement(countSql)) {

            while (emailResultSet.next()) {
                String email = emailResultSet.getString("email");
                countStatement.setString(1, email);
                try (ResultSet countResultSet = countStatement.executeQuery()) {
                    int count = countResultSet.next() ? countResultSet.getInt(1) : 0;
                    if (count > 0) {
                        continue;
                    }
                }

                String promoSnapshot = new FindPromozioniAttiveCommand(email).execute(connection);

                new InsertPagamentoCommand(email, "Gennaio", 2026, 24.99, "Pagamento confermato", promoSnapshot)
                    .execute(connection);
                new InsertPagamentoCommand(email, "Febbraio", 2026, 24.99, "Pagamento confermato", promoSnapshot)
                    .execute(connection);
                new InsertPagamentoCommand(email, "Marzo", 2026, 24.99, "Pagamento confermato", promoSnapshot)
                    .execute(connection);
                new InsertPagamentoCommand(email, "Aprile", 2026, 24.99, "Da pagare", promoSnapshot)
                    .execute(connection);
            }
        }
    }

    private boolean isTableEmpty(Connection connection, String tableName) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM " + tableName);
             ResultSet resultSet = statement.executeQuery()) {
            return resultSet.next() && resultSet.getInt(1) == 0;
        }
    }
}
