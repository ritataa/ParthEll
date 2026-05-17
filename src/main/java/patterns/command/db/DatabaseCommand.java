package patterns.command.db;

import java.sql.Connection;
import java.sql.SQLException;

/*
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * @author / @version: Tracciano paternita' e manutenzione del contratto esposto.
 * @param: Definisce i vincoli di input attesi per garantire un uso sicuro del metodo.
 * @return: Esplicita il risultato promesso al Client e i limiti di validita' del valore.
 * @throws: Dichiarata le eccezioni gestibili dal chiamante quando l'operazione puo' fallire.
 */

/**
 * Contratto Command (GoF) per eseguire un'operazione DB su una connessione fornita.
 * Questo file definisce l'interfaccia comune per eseguire comandi database in modo uniforme.
 * Il pattern Command separa il richiamo dell'operazione dalla sua esecuzione concreta.
 *
 * @author ParthEll Team
 * @version 1.0
 */
public interface DatabaseCommand<T> {   //POLIMORFISMO PER DATI: DatabaseCommand prende forme diverse adattandosi ai dati che deve restituire (T è generico, può essere qualsiasi tipo).

    /**
     * Esegue il comando sul database usando la connessione fornita.
     * Il risultato dipende dall'implementazione concreta del comando. (ecco perche' usiamo generico <T>).
     *  <T> è un tipo generico, viene usato perché non sappiamo a priori che tipo di risultato restituirà l'operazione sul database (numero, stringa, etc.). 
     *
     * @param connection connessione JDBC gia' aperta e valida per l'operazione.
     * @return risultato dell'operazione, secondo il contratto dell'implementazione concreta.
     * @throws SQLException se l'accesso al database fallisce o la query non puo' essere eseguita.
     */
    T execute(Connection connection) throws SQLException;
}
