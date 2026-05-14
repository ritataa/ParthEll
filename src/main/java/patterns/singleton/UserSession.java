package patterns.singleton;

/* LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * - @author / @version: Tracciano paternità e manutenzione
 * - @param: Definisce i vincoli di input per i metodi pubblici
 * - @return: Definisce l'output garantito per la sicurezza del Client
 */

/**
 * Mantiene le informazioni dell'utente autenticato nella sessione corrente.
 */
public final class UserSession {

    /**
     * Punto centrale per lo stato dell'utente autenticato in sessione.
     *
     * Ruolo architetturale: espone uno storage in-memory di breve durata
     * per informazioni di autenticazione/ruolo, accessibile globalmente tramite
     * Singleton (eager).
     *
     * @author ParthEll Team
     * @version 1.0
     */

    private static final UserSession INSTANCE = new UserSession(); // creazione dell'unico oggetto globale di sessione utente

    /*  Qui abbiamo in memoria le informazioni dell'utente autenticato, come email e ruolo. 
        Rendendo la sessione un Singleton, possiamo accedere a queste informazioni da qualsiasi parte del programma senza doverle passare come parametri, 
        mantenendo lo stato dell'utente in modo centralizzato e coerente.
    */

    
    private String currentEmail;    // Email corrente dell'utente; può essere null se non autenticato
    
    private String currentRole;     // Ruolo corrente dell'utente; valori attesi: "cliente", "admin", ecc.

    private UserSession() {
        // Costruttore privato per implementare il Singleton
    }

    /**
     * Restituisce l'istanza singleton della sessione utente.
     *
     * @return l'istanza globale di UserSession (non-null)
     */
    public static UserSession getInstance() {
        // Ritorna l'istanza eager
        return INSTANCE;
    }

    /**
     * Imposta l'utente corrente per la sessione.
     * Aggiorna email e ruolo dell'utente autenticato.
     *
     * @param email l'email dell'utente; può essere null per logout temporaneo
     * @param role  il ruolo dell'utente, stringa case-sensitive (es. "cliente")
     */
    public void setCurrentUser(String email, String role) {
        // Salvo i dati dell'utente nella sessione in memoria
        this.currentEmail = email;
        this.currentRole = role;
    }

    /**
     * Ritorna l'email dell'utente corrente.
     *
     * @return l'email corrente o null se nessun utente è autenticato
     */
    public String getCurrentEmail() {
        // Può ritornare null quando la sessione è vuota
        return currentEmail;
    }

    /**
     * Ritorna il ruolo dell'utente corrente.
     *
     * @return il ruolo corrente o null se nessun utente è autenticato
     */
    public String getCurrentRole() {
        // Può ritornare null quando la sessione è vuota
        return currentRole;
    }

    /**
     * Indica se l'utente corrente ha ruolo "cliente".
     *
     * @return true se il ruolo corrente è esattamente "cliente", false altrimenti
     */
    public boolean isCliente() {
        // Confronto sicuro: evita NullPointerException
        return "cliente".equals(currentRole);
    }

    /**
     * Svuota le informazioni dell'utente nella sessione.
     * Reimposta email e ruolo a null.
     */
    public void clear() {
        // Rimuove i riferimenti per terminare la sessione
        this.currentEmail = null;
        this.currentRole = null;
    }
}
