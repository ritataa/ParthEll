package patterns.singleton;

import patterns.proxy.TelecomRepositoryProxy;
import service.TelecomRepository;

/*
 * ==========================================================
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * ==========================================================
 * In questo progetto utilizziamo i tag Javadoc per definire i 
 * "contratti" delle API pubbliche, separando chiaramente 
 * COSA fa un metodo da COME è implementato.
 * 
 * - author / version : Tracciano la paternità e la 
 * manutenzione del file nel tempo.
 * - param  : Definisce i vincoli di input (cosa il metodo 
 * si aspetta di ricevere).
 * - return : Definisce l'output garantito (inclusi i casi 
 * di fallimento o null, per la sicurezza del Client).
 * ==========================================================
*/

/**
 * Singleton per la gestione dell'autenticazione degli utenti.
 * Gestisce la verifica delle credenziali per amministratori e clienti via JDBC.
 * JDBC = dato che il programma è scritto in Java ma il database in SQL, usiamo JDBC che è un'API (Application Programming Interface) ufficiale di Java. È un insieme di classi e di Interfacce standard 
 * che servono a far comunicare il codice Java con qualsiasi database relazionale.
 * 
 * Implementa il pattern Singleton per garantire una sola istanza del servizio di autenticazione.
 * 
 * @author ParthEll Team
 * @version 1.0
 */
public class AuthenticationService {
    
    /** Istanza singleton del servizio di autenticazione (inizializzazione eager)
     * 
     * private = non può essere vista e toccata dall'esterno
     * static = è condivisa da tutte le classi che la usano, non serve creare un oggetto per usarla. garantisce che esista una sola istanza in tutto il programma
     * final = è una costante, non può essere modificata dopo l'assegnazione
     */
    
    private static final AuthenticationService INSTANCE = new AuthenticationService();  // creazione dell'unico oggetto globale di autenticazione
    
    private final TelecomRepository repository = new TelecomRepositoryProxy();  // creazione del Proxy che il Singleton utilizza per parlare con i dati senza accedere al database vero
    
    /**
     * Costruttore privato per implementare il pattern Singleton.
     */
    private AuthenticationService() {
    }
    
    /**
     * Restituisce l'istanza singleton del servizio di autenticazione.
     * 
     * @return l'istanza singleton di AuthenticationService
     */
    public static AuthenticationService getInstance() {
        return INSTANCE;
    }
    
    /**
     * Autentica un utente verificando le credenziali.
     * 
     * @param email l'email dell'utente
     * @param password la password dell'utente
     * @return il tipo di utente ("admin", "cliente") o null se non autenticato
     */
    public String authenticate(String email, String password) {
        return repository.authenticate(email, password);
    }
    
    /**
     * Verifica se un utente è un amministratore.
     * 
     * @param email l'email dell'utente
     * @param password la password dell'utente
     * @return true se è un amministratore, false altrimenti
     */
    public boolean isAdmin(String email, String password) {
        return "admin".equals(authenticate(email, password));
    }
    
    /**
     * Verifica se un utente è un cliente.
     * 
     * @param email l'email dell'utente
     * @param password la password dell'utente
     * @return true se è un cliente, false altrimenti
     */
    public boolean isCliente(String email, String password) {
        return "cliente".equals(authenticate(email, password));
    }
}
