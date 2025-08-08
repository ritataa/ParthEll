package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

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

public class LoginController {

    @FXML private Label errorLabel;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;

    // Mappe per memorizzare le credenziali
    private Map<String, String> amministratori = new HashMap<>();
    private Map<String, String> abbonati = new HashMap<>();

    public void initialize() {
        // Carica le credenziali all'avvio
        caricaAmministratori();
        caricaAbbonati();
    }

    private void caricaAmministratori() {
        try (InputStream is = getClass().getResourceAsStream("/data/amministratore.csv");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                // Salta l'header
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    amministratori.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            System.err.println("Errore nel caricamento amministratori: " + e.getMessage());
        }
    }

    private void caricaAbbonati() {
        try (InputStream is = getClass().getResourceAsStream("/data/abbonato.csv");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                // Salta l'header
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    abbonati.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            System.err.println("Errore nel caricamento abbonati: " + e.getMessage());
        }
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Inserisci email e password.");
            return;
        }

        try {
            // Verifica se è un amministratore
            if (amministratori.containsKey(email) && amministratori.get(email).equals(password)) {
                errorLabel.setText("");
                loadScene(event, "/view/admin.fxml", "ParthEll - Pannello Admin");
                return;
            }
            
            // Verifica se è un abbonato/cliente
            if (abbonati.containsKey(email) && abbonati.get(email).equals(password)) {
                errorLabel.setText("");
                loadScene(event, "/view/cliente.fxml", "ParthEll - Area Cliente");
                return;
            }
            
            // Se arriva qui, le credenziali non sono valide
            errorLabel.setText("Email o password non validi.");
            
        } catch (IOException e) {
            errorLabel.setText("Errore nel caricamento della schermata.");
            e.printStackTrace();
        }
    }
    

    private void loadScene(ActionEvent event, String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
