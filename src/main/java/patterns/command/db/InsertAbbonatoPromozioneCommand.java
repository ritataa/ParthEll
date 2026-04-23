package patterns.command.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Command concreto che associa una promozione a un abbonato.
 */
public final class InsertAbbonatoPromozioneCommand implements DatabaseCommand<Integer> {

    private static final String SQL = "INSERT OR IGNORE INTO abbonato_promozione(email, promozione_nome) VALUES (?, ?)";

    private final String email;
    private final String promozioneNome;

    public InsertAbbonatoPromozioneCommand(String email, String promozioneNome) {
        this.email = email;
        this.promozioneNome = promozioneNome;
    }

    @Override
    public Integer execute(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL)) {
            statement.setString(1, email);
            statement.setString(2, promozioneNome);
            return statement.executeUpdate();
        }
    }
}
