package patterns.facade;

import patterns.singleton.AuthenticationService;
import patterns.singleton.UserSession;

public class AuthFacade {

    private final AuthenticationService authenticationService = AuthenticationService.getInstance();
    private final UserSession userSession = UserSession.getInstance();

    public String login(String email, String password) {
        String userType = authenticationService.authenticate(email, password);
        if (userType != null) {
            userSession.setCurrentUser(email, userType);
        }
        return userType;
    }

    public void logout() {
        userSession.clear();
    }

    public String getCurrentEmail() {
        return userSession.getCurrentEmail();
    }

    public String getCurrentRole() {
        return userSession.getCurrentRole();
    }

    public boolean isCliente() {
        return userSession.isCliente();
    }
}