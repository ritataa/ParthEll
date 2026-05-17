package patterns.command.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/*
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * @author / @version: Tracciano paternita' e manutenzione del contratto esposto.
 * @param: Definisce i vincoli di input attesi per garantire un uso sicuro del metodo.
 * @return: Esplicita il risultato promesso al Client e i limiti di validita' del valore.
 * @throws: Esplicita le eccezioni gestibili dal chiamante in caso di errore DB.
 */

/**
 * Questo file inserisce un piano tariffario nel database in modo parametrico e sicuro.
 * Incapsula la logica dell'INSERT dietro un comando riusabile e facile da invocare.
 * Il pattern Command serve a uniformare l'esecuzione delle operazioni DB dietro un contratto comune.
 *
 * @author ParthEll Team
 * @version 1.0
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

    /**
     * Costruisce il comando con i dati necessari per inserire un piano tariffario.
     * I valori vengono salvati e riusati quando il comando viene eseguito.
     *
     * @param nome nome del piano tariffario da inserire.
     * @param minutiMensili minuti mensili previsti; puo' essere null se illimitato.
     * @param gigaMensili giga mensili previsti; puo' essere null se illimitato.
     * @param illimitatoMinuti indica se i minuti sono illimitati.
     * @param illimitatoGiga indica se i giga sono illimitati.
     * @param costoMensile costo mensile del piano.
     */
    public InsertPianoTariffarioCommand(
        String nome,
        Integer minutiMensili,
        Integer gigaMensili,
        boolean illimitatoMinuti,
        boolean illimitatoGiga,
        double costoMensile
    ) {
        // Salvo i parametri nel comando per eseguire l'INSERT in un secondo momento.
        this.nome = nome;
        this.minutiMensili = minutiMensili;
        this.gigaMensili = gigaMensili;
        this.illimitatoMinuti = illimitatoMinuti;
        this.illimitatoGiga = illimitatoGiga;
        this.costoMensile = costoMensile;
    }

    // Sicurezza: obbliga Java a verificare che sto davvero implementando execute() promesso all'interfaccia DatabaseCommand, evitando errori di firma.
    @Override
    /**
     * Esegue l'INSERT del piano tariffario usando i valori memorizzati nel comando.
     * Gestisce i campi opzionali impostando NULL quando i minuti o i giga non sono definiti.
     *
     * @param connection connessione JDBC gia' aperta e valida per l'operazione.
     * @return numero di righe inserite: 1 se l'operazione riesce, 0 se non modifica il database.
     * @throws SQLException se la query non puo' essere preparata o eseguita correttamente.
     */

    // questo blocco serve a evitare problemi di sicurezza come cancellare o rubare i dati (SQL injection) e 
    // a non consumare RAM inutilmente (try-with-resources chiude automaticamente statement anche in caso di eccezione).
    public Integer execute(Connection connection) throws SQLException {         // Connection è un'interfaccia di JDBC che rappresenta una connessione al database. Viene passata al comando per eseguire l'operazione sul database.

        try (PreparedStatement statement = connection.prepareStatement(SQL)) {  // PreparedStatement fa parte di JDBC e dice quale "forma" deve avere la variabile ('statement') che stiamo creando. 
                                                                                // In questo caso, ordina di creare un oggetto capace di contenere query SQL in modo sicuro.
                                                                                // prepareStatement è un metodo che si trova dentro l'oggetto Connection e serve a creare un PreparedStatement a partire da una stringa SQL con parametri (?).
            
            // Lego i 6 parametri in ordine al posto dei punti interrogativi (?).
            statement.setString(1, nome);
            
            // Nota: Se i minuti o i giga sono illimitati (null), uso setNull per avvisare il database.
            // Altrimenti, li inserisco normalmente come numeri interi con setInt.
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
            
            // I database (come SQLite) spesso non hanno i campi "vero/falso", quindi li converto in numeri: 1 se vero, 0 se falso.
            if (illimitatoMinuti) {
                statement.setInt(4, 1);
            } else {
                statement.setInt(4, 0);
            }
            if (illimitatoGiga) {
                statement.setInt(5, 1);
            } else {
                statement.setInt(5, 0);
            }
            
            statement.setDouble(6, costoMensile);
            
            // Eseguo l'INSERT sul database.
            // executeUpdate() restituisce un intero: in questo caso 1 se il piano tariffario viene inserito con successo.
            return statement.executeUpdate();
        }
    }
}
