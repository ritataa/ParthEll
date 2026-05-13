package patterns.facade;

import java.sql.Connection;
import java.sql.SQLException;

import patterns.singleton.DatabaseConnectionManager;
import service.db.DatabaseSchemaMigrator;
import service.db.DatabaseSeeder;

/*
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 *
 * @author / @version: Tracciano paternità e manutenzione del file.
 * @return: Definisce l'output garantito al chiamante e segnala eventuali valori null o errori gestiti.
 * @throws: Esplicita le eccezioni che il chiamante può intercettare per gestire il flusso in sicurezza.
 */

/**
 * Inizializza il sottosistema database in modo centralizzato e controllato.
 * Esegue migrazione schema e caricamento dati iniziali con un'unica chiamata.
 * Usa il pattern Facade per nascondere la complessità delle operazioni database.
 *
 * @author ParthEll Team
 * @version 1.0
 */
public final class DatabaseFacade {

    private final DatabaseConnectionManager connectionManager; // private perché deve essere usato solo dentro questa classe, così nessun altro può 
                                                               // cambiare direttamente il gestore della connessione e il controllo resta semplice e sicuro.

    /**
     * Crea la facciata e aggancia il gestore unico della connessione al database.
     *
     * @return una nuova facciata pronta a inizializzare il sistema database
     */
    public DatabaseFacade() {
        // Uso l'istanza unica per lavorare sempre con la stessa gestione delle connessioni
        this.connectionManager = DatabaseConnectionManager.getInstance();
    }

    /**
     * Avvia la preparazione del database creando schema e dati base.
     * In caso di errore SQL, blocca il flusso con una eccezione chiara.
     *
     * @throws IllegalStateException se l'inizializzazione del database fallisce
     */
    public void initSystem() {
        // Creo i due componenti che preparano schema e dati iniziali
        DatabaseSchemaMigrator schemaMigrator = new DatabaseSchemaMigrator();
        DatabaseSeeder seeder = new DatabaseSeeder();

        // Apro una connessione e la chiudo automaticamente a fine blocco
        try (Connection connection = connectionManager.getConnection()) {
            // Prima aggiorno lo schema, poi carico i dati necessari
            schemaMigrator.migrate(connection);
            seeder.seedDataIfNeeded(connection);
            seeder.seedPagamentiPerTutti(connection);
        } catch (SQLException exception) {
            // Traduco l'errore tecnico in un messaggio più chiaro per chi usa la facciata
            throw new IllegalStateException("Errore durante inizializzazione del sistema database", exception);
        }
    }
}
