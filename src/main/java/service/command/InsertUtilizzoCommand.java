package service.command;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Command concreto che inserisce i dati di utilizzo traffico.
 */
public final class InsertUtilizzoCommand implements DatabaseCommand<Integer> {

    private static final String SQL = "INSERT INTO utilizzo(numero, nome, cognome, email, chiamate, sms, dati) VALUES (?, ?, ?, ?, ?, ?, ?)";

    private final String numero;
    private final String nome;
    private final String cognome;
    private final String email;
    private final int chiamate;
    private final int sms;
    private final int dati;

    public InsertUtilizzoCommand(String numero, String nome, String cognome, String email, int chiamate, int sms, int dati) {
        this.numero = numero;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.chiamate = chiamate;
        this.sms = sms;
        this.dati = dati;
    }

    @Override
    public Integer execute(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL)) {
            statement.setString(1, numero);
            statement.setString(2, nome);
            statement.setString(3, cognome);
            statement.setString(4, email);
            statement.setInt(5, chiamate);
            statement.setInt(6, sms);
            statement.setInt(7, dati);
            return statement.executeUpdate();
        }
    }
}
