package patterns.command.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Contratto Command (GoF) per eseguire un'operazione DB su una connessione fornita.
 */
public interface DatabaseCommand<T> {

    T execute(Connection connection) throws SQLException;
}
