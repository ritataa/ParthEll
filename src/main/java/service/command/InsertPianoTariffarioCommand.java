package service.command;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Command concreto che inserisce un piano tariffario.
 */
public final class InsertPianoTariffarioCommand implements DatabaseCommand<Integer> {

    private static final String SQL = """
        INSERT INTO piano_tariffario(nome, minuti_mensili, giga_mensili, illimitato_minuti, illimitato_giga, costo_mensile)
        VALUES (?, ?, ?, ?, ?, ?)
        """;

    private final String nome;
    private final Integer minutiMensili;
    private final Integer gigaMensili;
    private final boolean illimitatoMinuti;
    private final boolean illimitatoGiga;
    private final double costoMensile;

    public InsertPianoTariffarioCommand(
        String nome,
        Integer minutiMensili,
        Integer gigaMensili,
        boolean illimitatoMinuti,
        boolean illimitatoGiga,
        double costoMensile
    ) {
        this.nome = nome;
        this.minutiMensili = minutiMensili;
        this.gigaMensili = gigaMensili;
        this.illimitatoMinuti = illimitatoMinuti;
        this.illimitatoGiga = illimitatoGiga;
        this.costoMensile = costoMensile;
    }

    @Override
    public Integer execute(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL)) {
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
            return statement.executeUpdate();
        }
    }
}
