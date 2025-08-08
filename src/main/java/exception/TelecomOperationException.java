package exception;

/**
 * Eccezione personalizzata per errori durante operazioni telefoniche.
 * Utilizzata per gestire errori specifici del dominio telefonico come
 * numeri non validi, credito insufficiente, ecc.
 * 
 * @author ParthEll Team
 * @version 1.0
 */
public class TelecomOperationException extends Exception {
    
    /**
     * Costruttore che crea un'eccezione con un messaggio predefinito.
     */
    public TelecomOperationException() {
        super("Errore durante l'operazione telecom");
    }
    
    /**
     * Costruttore che crea un'eccezione con un messaggio personalizzato.
     * 
     * @param message il messaggio di errore personalizzato
     */
    public TelecomOperationException(String message) {
        super(message);
    }
    
    /**
     * Costruttore che crea un'eccezione con un messaggio e una causa.
     * 
     * @param message il messaggio di errore
     * @param cause la causa dell'eccezione
     */
    public TelecomOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
