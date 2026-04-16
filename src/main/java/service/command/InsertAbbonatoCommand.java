package service.command;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Command concreto che inserisce un abbonato.
 */
public final class InsertAbbonatoCommand implements DatabaseCommand<Integer> {

    private static final String SQL = """
        INSERT INTO abbonato(email, password, nome, cognome, residenza, numero_telefono, piano_tariffario, saldo)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

    private final String email;
    private final String password;
    private final String nome;
    private final String cognome;
    private final String residenza;
    private final String numeroTelefono;
    private final String pianoTariffario;
    private final double saldo;

    public InsertAbbonatoCommand(
        String email,
        String password,
        String nome,
        String cognome,
        String residenza,
        String numeroTelefono,
        String pianoTariffario,
        double saldo
    ) {
        this.email = email;
        this.password = password;
        this.nome = nome;
        this.cognome = cognome;
        this.residenza = residenza;
        this.numeroTelefono = numeroTelefono;
        this.pianoTariffario = pianoTariffario;
        this.saldo = saldo;
    }

    @Override
    public Integer execute(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL)) {
            statement.setString(1, email);
            statement.setString(2, password);
            statement.setString(3, nome);
            statement.setString(4, cognome);
            statement.setString(5, residenza);
            statement.setString(6, numeroTelefono);
            statement.setString(7, pianoTariffario);
            statement.setDouble(8, saldo);
            return statement.executeUpdate();
        }
    }
}
