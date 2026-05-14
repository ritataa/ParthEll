package controller;

import java.io.IOException;

import exception.AuthenticationException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import patterns.facade.AuthFacade;

/*
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * @author / @version: Tracciano paternità e manutenzione della classe.
 * @param: Definisce i vincoli di input richiesti dal metodo per un uso corretto.
 * @throws: Esplicita le eccezioni gestibili dal chiamante o documentate dal contratto.
 */

/**
    Gestisce il login, la validazione credenziali e il routing verso le aree admin o cliente.
    Centralizza anche l'apertura della registrazione per mantenere la navigazione coerente. Utilizza: 
    - Facade Pattern tramite AuthFacade per unificare verifiche credenziali e gestione sessione, 
    - MVC Pattern come controller JavaFX per separare logica di login dalla vista.
 *
 * @author ParthEll Team
 * @version 1.0
 */
public class LoginController {

    /** Label per visualizzare messaggi di errore */
    @FXML private Label errorLabel;

    /** Campo di input per l'email dell'utente */
    @FXML private TextField emailField;

    /** Campo di input per la password dell'utente */
    @FXML private PasswordField passwordField;

    /** Facade per autenticazione e sessione */
    private final AuthFacade authFacade = new AuthFacade();

    /**
     * Inizializza la schermata di login e azzera eventuali messaggi precedenti.
     * Esegue anche il logout iniziale per garantire una sessione pulita.
     *
     * @return nessun valore; prepara solo lo stato iniziale della vista.
     */
    public void initialize() {
        // Reset della sessione per evitare credenziali o stato ereditato da schermate precedenti.
        authFacade.logout();
        if (errorLabel != null) {
            errorLabel.setText("");
        }
    }

    /**
     * Valida le credenziali e apre l'area corretta in base al tipo di utente.
     * Se i dati sono mancanti o non validi, mostra un errore senza cambiare scena.
     * 
     * @param event evento UI del pulsante di accesso; deve provenire dal bottone login.
     * @return nessun valore; il risultato viene gestito tramite navigazione o messaggi errore.
     */
    // Collegamento FXML: questo handler viene richiamato dalla vista definita nel file .fxml.
    @FXML
    public void handleLogin(ActionEvent event) {
        System.out.println("[ATTO 2 - 1. LOGIN CONTROLLER] L'utente ha cliccato Accedi. Passo le credenziali alla Facade di autenticazione.");
        String email = emailField.getText();
        String password = passwordField.getText();

        // Controllo minimo per evitare chiamate inutili al backend.
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            errorLabel.setText("Inserisci email e password.");
            return;
        }

        try {
            String userType = authFacade.login(email.trim(), password);

            if (userType == null) {
                throw new AuthenticationException("Email o password non validi.");
            }

            // Pulizia del feedback: se il login riesce, il messaggio precedente non serve più.
            errorLabel.setText("");

            // Routing semplice: ogni profilo riceve la propria schermata dedicata.
            switch (userType) {
                case "admin" -> loadScene(event, "/view/admin.fxml", "ParthEll - Pannello Admin");
                case "cliente" -> loadScene(event, "/view/cliente.fxml", "ParthEll - Area Cliente");
                default -> throw new AuthenticationException("Tipo di utente non riconosciuto: " + userType);
            }

        } catch (AuthenticationException e) {
            errorLabel.setText(e.getMessage());
        } catch (IOException e) {
            errorLabel.setText("Errore nel caricamento della schermata.");
            System.err.println("Errore caricamento schermata: " + e.getMessage());
            logExceptionDetails(e);
        } catch (Exception e) {
            errorLabel.setText("Errore imprevisto durante il login.");
            System.err.println("Errore imprevisto: " + e.getMessage());
            logExceptionDetails(e);
        }
    }

    /**
     * Apre la schermata di registrazione senza alterare la sessione dell'utente.
     * È usato quando il client deve creare un nuovo account prima del login.
     *
     * @param event evento UI del pulsante "registrati".
     * @return nessun valore; in caso di errore mostra un messaggio nella vista.
     */
    // Collegamento FXML: questo handler viene richiamato dalla vista definita nel file .fxml.
    @FXML
    public void handleApriRegistrazione(ActionEvent event) {
        try {
            loadScene(event, "/view/register.fxml", "ParthEll - Registrazione");
        } catch (IOException exception) {
            errorLabel.setText("Impossibile aprire la registrazione.");
        }
    }

    /**
     * Carica una nuova scena nell'applicazione e aggiorna il titolo della finestra.
     * Centralizza il cambio vista per non duplicare il codice di navigazione.
     * 
     * @param event evento UI che fornisce la finestra corrente; deve essere valido.
     * @param fxmlPath percorso dell'FXML da caricare; deve puntare a una vista esistente.
     * @param title titolo da assegnare alla nuova finestra.
     * @throws IOException se il file FXML non può essere caricato correttamente.
     */
    private void loadScene(ActionEvent event, String fxmlPath, String title) throws IOException {
        // Caricamento della vista da FXML: il file decide la struttura, il controller solo il routing.
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();
    }

    private void logExceptionDetails(Exception exception) {
        // Traccia minima per il debug: stampa causa e catena dell'errore su console.
        System.err.println("Dettagli eccezione: " + exception);
        Throwable cause = exception.getCause();
        if (cause != null) {
            System.err.println("Causa: " + cause);
        }
    }

}
