package controller;

import java.io.IOException;

import exception.AuthenticationException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import service.AuthenticationService;

/**
 * Controller per la gestione dell'interfaccia di login dell'applicazione ParthEll.
 * Gestisce l'autenticazione degli utenti (amministratori e clienti) e il routing
 * verso le rispettive interfacce.
 * 
 * Utilizza il pattern Singleton per il servizio di autenticazione e
 * implementa una gestione robusta delle eccezioni.
 * 
 * @author ParthEll Team
 * @version 1.0
 * @since 1.0
 */
public class LoginControllerNew {

    /** Label per visualizzare messaggi di errore */
    @FXML private Label errorLabel;
    
    /** Campo di input per l'email dell'utente */
    @FXML private TextField emailField;
    
    /** Campo di input per la password dell'utente */
    @FXML private PasswordField passwordField;
    
    /** Bottone per effettuare il login */
    @FXML private Button loginButton;

    /** Servizio di autenticazione singleton */
    private final AuthenticationService authService = AuthenticationService.getInstance();

    /**
     * Metodo di inizializzazione chiamato automaticamente da JavaFX.
     * Imposta lo stato iniziale dell'interfaccia.
     */
    public void initialize() {
        // Pulisce eventuali messaggi di errore all'avvio
        if (errorLabel != null) {
            errorLabel.setText("");
        }
    }

    /**
     * Gestisce l'evento di login quando l'utente preme il bottone di accesso.
     * Valida le credenziali e reindirizza l'utente all'interfaccia appropriata.
     * 
     * @param event l'evento scatenato dal bottone di login
     * @throws AuthenticationException se le credenziali non sono valide
     */
    @FXML
    public void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        // Validazione input
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            errorLabel.setText("Inserisci email e password.");
            return;
        }

        try {
            // Usa il servizio di autenticazione singleton
            String userType = authService.authenticate(email.trim(), password);
            
            if (userType == null) {
                throw new AuthenticationException("Email o password non validi.");
            }
            
            // Pulisce il messaggio di errore
            errorLabel.setText("");
            
            // Routing basato sul tipo di utente
            switch (userType) {
                case "admin":
                    loadScene(event, "/view/admin.fxml", "ParthEll - Pannello Admin");
                    break;
                case "cliente":
                    loadScene(event, "/view/cliente.fxml", "ParthEll - Area Cliente");
                    break;
                default:
                    throw new AuthenticationException("Tipo di utente non riconosciuto: " + userType);
            }
            
        } catch (AuthenticationException e) {
            errorLabel.setText(e.getMessage());
        } catch (IOException e) {
            errorLabel.setText("Errore nel caricamento della schermata.");
            // Log dell'errore per debugging (in produzione usare un logger)
            System.err.println("Errore caricamento schermata: " + e.getMessage());
        } catch (Exception e) {
            errorLabel.setText("Errore imprevisto durante il login.");
            System.err.println("Errore imprevisto: " + e.getMessage());
        }
    }

    /**
     * Carica una nuova scena nell'applicazione.
     * Metodo helper per gestire il cambio di interfaccia dopo il login.
     * 
     * @param event l'evento che ha scatenato il cambio di scena
     * @param fxmlPath il percorso del file FXML da caricare
     * @param title il titolo della nuova finestra
     * @throws IOException se il file FXML non può essere caricato
     */
    private void loadScene(ActionEvent event, String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();
    }

    /**
     * Mostra un dialog di alert all'utente.
     * Metodo helper per visualizzare messaggi di errore o informazione.
     * 
     * @param alertType il tipo di alert (ERROR, WARNING, INFORMATION)
     * @param title il titolo del dialog
     * @param message il messaggio da visualizzare
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
