package patterns.command.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Command concreto che inserisce un amministratore.
 */
public final class InsertAmministratoreCommand implements DatabaseCommand<Integer> {

    private static final String SQL = "INSERT INTO amministratore(email, password, ruolo, nome, cognome) VALUES (?, ?, ?, ?, ?)";

    private final String email;
    private final String password;
    private final String ruolo;
    private final String nome;
    private final String cognome;

    public InsertAmministratoreCommand(String email, String password, String ruolo, String nome, String cognome) {
        this.email = email;
        this.password = password;
        this.ruolo = ruolo;
        this.nome = nome;
        this.cognome = cognome;
    }

    @Override
    public Integer execute(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL)) {
            statement.setString(1, email);
            statement.setString(2, password);
            statement.setString(3, ruolo);
            statement.setString(4, nome);
            statement.setString(5, cognome);
            return statement.executeUpdate();
        }
    }
}
