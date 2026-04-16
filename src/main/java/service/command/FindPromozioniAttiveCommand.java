package service.command;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Command concreto che legge le promozioni attive di un abbonato come stringa CSV.
 */
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

    @Override
    public String execute(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("promo");
                }
                return "";
            }
        }
    }
}
