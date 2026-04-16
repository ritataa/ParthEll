package service.command;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Command concreto che inserisce una promozione.
 */
public final class InsertPromozioneCommand implements DatabaseCommand<Integer> {

    private static final String SQL = "INSERT INTO promozione(nome, costo, descrizione) VALUES (?, ?, ?)";

    private final String nome;
    private final double costo;
    private final String descrizione;

    public InsertPromozioneCommand(String nome, double costo, String descrizione) {
        this.nome = nome;
        this.costo = costo;
        this.descrizione = descrizione;
    }

    @Override
    public Integer execute(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL)) {
            statement.setString(1, nome);
            statement.setDouble(2, costo);
            statement.setString(3, descrizione);
            return statement.executeUpdate();
        }
    }
}
