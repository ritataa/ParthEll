package patterns.command.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/*
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * @author / @version: Tracciano paternita' e manutenzione del contratto esposto.
 * @param: Definisce i vincoli di input attesi per garantire un uso sicuro del metodo.
 * @return: Esplicita il risultato promesso al Client e i limiti di validita' del valore.
 * @throws: Dichiarata le eccezioni gestibili dal chiamante quando l'operazione puo' fallire.
 */

/**
 * Command concreto che inserisce un abbonato nel database.
 * Offre tre costruttori per gestire abbonati con piano Carta di Credito (tutti i dati) o Ricaricabile (senza dati carta).
 * Il pattern Command incapsula l'INSERT SQL dietro un contratto unico, garantendo preparazione sicura dei parametri.
 *
 * @author ParthEll Team
 * @version 1.0
 */

// Risolviamo il "Jolly" <T> dell'interfaccia impostandolo a <Integer>, perché questo specifico comando 
// restituisce il numero di righe inserite (int) come risultato dell'operazione.
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

    /**
     * Costruttore completo per abbonati con dati carta di credito.
     * Accetta tutti i 13 parametri necessari per l'inserimento completo dell'abbonato.
     *
     * @param email indirizzo email unico dell'abbonato; usato come chiave primaria.
     * @param password password dell'abbonato, memorizzata in base di dati.
     * @param nome nome proprio dell'abbonato.
     * @param cognome cognome dell'abbonato.
     * @param residenza indirizzo di residenza dell'abbonato.
     * @param numeroTelefono numero di telefono per contatti.
     * @param pianoTariffario tipo di piano (es. "Carta di Credito", "Ricaricabile").
     * @param conto tipo di conto associato (es. "Fisso", "Corrente").
     * @param saldo saldo iniziale dell'abbonato.
     * @param numeroCarta numero della carta di credito.
     * @param scadenzaCarta data di scadenza della carta (formato MM/YY).
     * @param cvvCarta codice di sicurezza della carta.
     * @param intestatarioCarta intestatario della carta di credito.
     */
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

    /**
     * Costruttore semplificato per abbonati Ricaricabile senza dati carta.
     * Delega al costruttore principale passando null per numeroCarta, scadenzaCarta, cvvCarta e intestatarioCarta.
     *
     * @param email indirizzo email unico dell'abbonato.
     * @param password password dell'abbonato.
     * @param nome nome proprio dell'abbonato.
     * @param cognome cognome dell'abbonato.
     * @param residenza indirizzo di residenza dell'abbonato.
     * @param numeroTelefono numero di telefono per contatti.
     * @param pianoTariffario tipo di piano (es. "Ricaricabile").
     * @param conto tipo di conto (es. "Fisso").
     * @param saldo saldo iniziale dell'abbonato.
     */
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

    /**
     * Costruttore minimalista per abbonati Ricaricabile senza conto esplicito e senza dati carta.
     * Imposta il conto predefinito a "Fisso" e delega al secondo costruttore.
     *
     * @param email indirizzo email unico dell'abbonato.
     * @param password password dell'abbonato.
     * @param nome nome proprio dell'abbonato.
     * @param cognome cognome dell'abbonato.
     * @param residenza indirizzo di residenza dell'abbonato.
     * @param numeroTelefono numero di telefono per contatti.
     * @param pianoTariffario tipo di piano (es. "Ricaricabile").
     * @param saldo saldo iniziale dell'abbonato.
     */
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

    // Sicurezza: @Override verifica che sto rispettando il metodo execute() promesso dall'interfaccia DatabaseCommand.
    @Override
    /**
     * Esegue l'inserimento dell'abbonato nel database con tutti i dati memorizzati.
     * Prepara la query in modo sicuro legando i parametri uno alla volta.
     *
     * @param connection connessione JDBC gia' aperta e valida per eseguire l'INSERT.
     * @return numero di righe inserite (sempre 1 se l'INSERT riesce, 0 se fallisce).
     * @throws SQLException se l'email esiste gia' (violazione vincolo UNIQUE), oppure se la query non puo' essere eseguita.
     */

    // questo blocco serve a evitare problemi di sicurezza come cancellare o rubare i dati (SQL injection) e 
    // a non consumare RAM inutilmente (try-with-resources chiude automaticamente statement e resultSet anche in caso di eccezione).

    public Integer execute(Connection connection) throws SQLException {         // Connection è un'interfaccia di JDBC che rappresenta una connessione al database. Viene passata al comando per eseguire l'operazione sul database.
        // Preparo una sola volta la query e la lego ai valori in modo sicuro per evitare SQL injection.

        try (PreparedStatement statement = connection.prepareStatement(SQL)) {  // PreparedStatement fa parte di JDBC e dice quale "forma" deve avere la variabile ('statement') che stiamo creando. 
                                                                                // In questo caso, ordina di creare un oggetto capace di contenere query SQL in modo sicuro.
                                                                                // prepareStatement è un metodo che si trova dentro l'oggetto Connection e serve a creare un PreparedStatement a partire da una stringa SQL con parametri (?).
            // Lego i 13 parametri in ordine:
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
            // Eseguo l'INSERT: restituisce il numero di righe inserite.
            return statement.executeUpdate();
        }
    }
}
