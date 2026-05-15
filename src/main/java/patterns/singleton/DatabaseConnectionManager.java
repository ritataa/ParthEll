package patterns.singleton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/* LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * - @author / @version: Tracciano paternità e manutenzione
 * - @return: Definisce l'output garantito per la sicurezza del Client
 * - @throws: Esplicita le eccezioni gestibili dal chiamante
 */

/**
 * Gestisce esclusivamente la connessione SQLite tramite il pattern Singleton (Gang of Four).
 * 
 * Ruolo architetturale: fornisce un punto centrale e unico di accesso al database SQLite,
 * garantendo che esista una sola istanza durante l'intero ciclo di vita dell'applicazione.
 * Implementa il pattern con eager initialization per garantire thread-safety senza sincronizzazione.
 * 
 * Separazione delle responsabilità:
 *  DatabaseConnectionManager gestisce i dettagli tecnici del driver e dell'URL SQLite. 
 *  DatabaseManager fornisce un'interfaccia pulita ai Repository.
 * Se un domani decidessimo di passare da SQLite a un database MySQL, dovremmo modificare solo il file tecnico (ConnectionManager)
 * 
 * @author ParthEll Team
 * @version 1.0
 */
public final class DatabaseConnectionManager {

    // stringa fissa che dice l'URL del database SQLite locale usato dall'app.
    private static final String DB_URL = "jdbc:sqlite:parth.db";

    // instanza statica e finale: garantisce che ci sia solo un'istanza di DatabaseConnectionManager.
    // Singleton eager: istanza unica creata al caricamento della classe (thread-safe per design).
    private static final DatabaseConnectionManager INSTANCE = new DatabaseConnectionManager();


    // L'oggetto INSTANCE legge il testo contenuto in DB_URL  per sapere esattamente quale file deve aprire quando il resto 
    // del programma gli chiede una connessione.

    /**
     * Costruttore privato che impedisce l'uso di "new" dall'esterno.
     * Non è vuoto perché viene sfruttato per il setup iniziale: siccome viene eseguito 
     * una sola volta al lancio dell'app, viene usato per caricare e verificare 
     * l'esistenza del driver SQLite, fallendo subito in caso di problemi.
     */
    private DatabaseConnectionManager() {
        try {
            // Caricamento del driver SQLite: necessario prima di usare DriverManager
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException exception) {
            // Lancia exception immediata invece che rimandare il problema a getConnection()
            throw new IllegalStateException("Driver SQLite non disponibile", exception);
        }
    }

    /**
     * Ritorna l'istanza unica globale di DatabaseConnectionManager.
     * Garantisce a tutti i client la stessa istanza (mai null).
     * Non tocca il database.
     * 
     * @return l'unica istanza del Singleton (non-null)
     */
    public static DatabaseConnectionManager getInstance() {
        // Ritorno dell'istanza statica già costruita al caricamento della classe
        return INSTANCE;
    }

    /**
     * Ottiene una nuova connessione al database SQLite.
     * Non ritorna mai null: lancia SQLException se la connessione fallisce.
     * 
     * Usa l'indirizzo DB_URL per connettersi al database locale "parth.db".
     * 
     * @return una Connection valida al database SQLite (non-null)
     * @throws SQLException se il database è inaccessibile, corrotto, o l'URL è malformato
     */
    public Connection getConnection() throws SQLException {
        // DriverManager.getConnection() crea una nuova Connection ogni volta
        return DriverManager.getConnection(DB_URL);
    }
}
