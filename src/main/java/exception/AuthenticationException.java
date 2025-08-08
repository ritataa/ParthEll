package exception;

/**
 * Eccezione personalizzata lanciata quando le credenziali di autenticazione non sono valide.
 * Estende RuntimeException per semplificare la gestione degli errori di autenticazione.
 * 
 * @author ParthEll Team
 * @version 1.0
 */
public class AuthenticationException extends RuntimeException {
    
    /**
     * Costruttore che crea un'eccezione con un messaggio predefinito.
     */
    public AuthenticationException() {
        super("Credenziali di autenticazione non valide");
    }
    
    /**
     * Costruttore che crea un'eccezione con un messaggio personalizzato.
     * 
     * @param message il messaggio di errore personalizzato
     */
    public AuthenticationException(String message) {
        super(message);
    }
    
    /**
     * Costruttore che crea un'eccezione con un messaggio e una causa.
     * 
     * @param message il messaggio di errore
     * @param cause la causa dell'eccezione
     */
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
