package patterns.command.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Command concreto che inserisce un pagamento.
 */
public final class InsertPagamentoCommand implements DatabaseCommand<Integer> {

    private static final String SQL = "INSERT INTO pagamenti(id_abbonato, mese, anno, importo, stato, promo) VALUES (?, ?, ?, ?, ?, ?)";

    private final String idAbbonato;
    private final String mese;
    private final int anno;
    private final double importo;
    private final String stato;
    private final String promo;

    public InsertPagamentoCommand(String idAbbonato, String mese, int anno, double importo, String stato, String promo) {
        this.idAbbonato = idAbbonato;
        this.mese = mese;
        this.anno = anno;
        this.importo = importo;
        this.stato = stato;
        this.promo = promo;
    }

    @Override
    public Integer execute(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL)) {
            statement.setString(1, idAbbonato);
            statement.setString(2, mese);
            statement.setInt(3, anno);
            statement.setDouble(4, importo);
            statement.setString(5, stato);
            statement.setString(6, promo);
            return statement.executeUpdate();
        }
    }
}
