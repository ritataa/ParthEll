package patterns.singleton;

/**
 * Mantiene le informazioni dell'utente autenticato nella sessione corrente.
 */
public final class UserSession {

    private static final UserSession INSTANCE = new UserSession();

    private String currentEmail;
    private String currentRole;

    private UserSession() {
    }

    public static UserSession getInstance() {
        return INSTANCE;
    }

    public void setCurrentUser(String email, String role) {
        this.currentEmail = email;
        this.currentRole = role;
    }

    public String getCurrentEmail() {
        return currentEmail;
    }

    public String getCurrentRole() {
        return currentRole;
    }

    public boolean isCliente() {
        return "cliente".equals(currentRole);
    }

    public void clear() {
        this.currentEmail = null;
        this.currentRole = null;
    }
}
