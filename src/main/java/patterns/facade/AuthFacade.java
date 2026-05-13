package patterns.facade;

import patterns.singleton.AuthenticationService;
import patterns.singleton.UserSession;

/*
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 *
 * @author / @version: Tracciano paternità e manutenzione del file.
 * @param: Definisce i vincoli di input richiesti dal metodo (es. non null, formato atteso).
 * @return: Definisce l'output garantito al chiamante e segnala se può essere null.
 */

/**
 * Facade per l'autenticazione e gestione semplificata della sessione utente.
 * Fornisce metodi per login, logout e interrogazione della sessione corrente.
 * Utilizza il pattern Facade per nascondere la complessità di AuthenticationService
 * e UserSession offrendo un'interfaccia semplice al resto dell'applicazione.
 *
 * @author ParthEll Team
 * @version 1.0
 */
public class AuthFacade {

    private final AuthenticationService authenticationService = AuthenticationService.getInstance(); // Privato: lo usa solo questa classe per controllare il login
    private final UserSession userSession = UserSession.getInstance(); // Privato: tiene la sessione interna, senza accesso diretto da fuori

    /**
     * Autentica l'utente usando le credenziali fornite e imposta la sessione al successo.
     * Se l'autenticazione fallisce, non modifica la sessione.
     *
     * @param email email dell'utente; non deve essere null e dovrebbe essere in formato valido
     * @param password password associata all'email; non deve essere null
     * @return il tipo/ruolo dell'utente autenticato (es. "ADMIN", "CLIENTE")
     *         oppure null se l'autenticazione fallisce
     */
    public String login(String email, String password) {
        // Chiedo al servizio di autenticazione di verificare le credenziali
        String userType = authenticationService.authenticate(email, password);
        // Se l'autenticazione è andata a buon fine, memorizzo l'utente nella sessione
        if (userType != null) {
            userSession.setCurrentUser(email, userType);
        }
        // Ritorno il tipo utente o null per segnalare fallimento
        return userType;
    }

    /**
     * Termina la sessione utente corrente rimuovendo le informazioni memorizzate.
     */
    public void logout() {
        // Svuoto la sessione per effettuare il logout
        userSession.clear();
    }

    /**
     * Restituisce l'email dell'utente attualmente autenticato.
     *
     * @return l'email corrente o null se nessun utente è autenticato
     */
    public String getCurrentEmail() {
        // Prelevo l'email dalla singola istanza di UserSession
        return userSession.getCurrentEmail();
    }

    /**
     * Restituisce il ruolo/tipo dell'utente autenticato.
     *
     * @return il ruolo corrente (es. "CLIENTE") o null se non autenticato
     */
    public String getCurrentRole() {
        // Leggo il ruolo memorizzato nella sessione
        return userSession.getCurrentRole();
    }

    /**
     * Indica se l'utente corrente ha il ruolo "CLIENTE".
     *
     * @return true se l'utente è cliente, false altrimenti (incl. non autenticato)
     */
    public boolean isCliente() {
        // Valuta lo stato della sessione per determinare il ruolo cliente
        return userSession.isCliente();
    }
}