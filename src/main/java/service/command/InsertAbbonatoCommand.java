package service.command;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Command concreto che inserisce un abbonato.
 */
public final class InsertAbbonatoCommand implements DatabaseCommand<Integer> {

    private static final String SQL = """
        INSERT INTO abbonato(email, password, nome, cognome, residenza, numero_telefono, piano_tariffario, conto, saldo, numero_carta, scadenza_carta, cvv_carta, intestatario_carta)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

    private final String email;
    private final String password;
    private final String nome;
    private final String cognome;
    private final String residenza;
    private final String numeroTelefono;
    private final String pianoTariffario;
    private final String conto;
    private final double saldo;
    private final String numeroCarta;
    private final String scadenzaCarta;
    private final String cvvCarta;
    private final String intestatarioCarta;

    public InsertAbbonatoCommand(
        String email,
        String password,
        String nome,
        String cognome,
        String residenza,
        String numeroTelefono,
        String pianoTariffario,
        String conto,
        double saldo,
        String numeroCarta,
        String scadenzaCarta,
        String cvvCarta,
        String intestatarioCarta
    ) {
        this.email = email;
        this.password = password;
        this.nome = nome;
        this.cognome = cognome;
        this.residenza = residenza;
        this.numeroTelefono = numeroTelefono;
        this.pianoTariffario = pianoTariffario;
        this.conto = conto;
        this.saldo = saldo;
        this.numeroCarta = numeroCarta;
        this.scadenzaCarta = scadenzaCarta;
        this.cvvCarta = cvvCarta;
        this.intestatarioCarta = intestatarioCarta;
    }

    // Backward compatibility per Ricaricabile (senza dati carta)
    public InsertAbbonatoCommand(
        String email,
        String password,
        String nome,
        String cognome,
        String residenza,
        String numeroTelefono,
        String pianoTariffario,
        String conto,
        double saldo
    ) {
        this(email, password, nome, cognome, residenza, numeroTelefono, pianoTariffario, conto, saldo, null, null, null, null);
    }

    // Backward compatibility per Ricaricabile
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
        this(email, password, nome, cognome, residenza, numeroTelefono, pianoTariffario, "Fisso", saldo, null, null, null, null);
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
            statement.setString(8, conto);
            statement.setDouble(9, saldo);
            statement.setString(10, numeroCarta);
            statement.setString(11, scadenzaCarta);
            statement.setString(12, cvvCarta);
            statement.setString(13, intestatarioCarta);
            return statement.executeUpdate();
        }
    }
}
