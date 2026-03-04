package service;

/**
 * Singleton per la gestione dell'autenticazione degli utenti.
 * Gestisce la verifica delle credenziali per amministratori e clienti via JDBC.
 * Implementa il pattern Singleton per garantire una sola istanza del servizio di autenticazione.
 * 
 * @author ParthEll Team
 * @version 1.0
 */
public class AuthenticationService {
    
    /** Istanza singleton del servizio di autenticazione */
    private static AuthenticationService instance;
    
    private final TelecomRepository repository = new TelecomRepository();
    
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
    public static synchronized AuthenticationService getInstance() {
        if (instance == null) {
            instance = new AuthenticationService();
        }
        return instance;
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
