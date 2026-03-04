package controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import service.TelecomRepository;

public class RegisterController {

    private final TelecomRepository repository = new TelecomRepository();

    @FXML private TextField emailField;
    @FXML private TextField passwordField;
    @FXML private TextField nomeField;
    @FXML private TextField cognomeField;
    @FXML private TextField residenzaField;
    @FXML private TextField numeroField;
    @FXML private ComboBox<String> pianoField;
    @FXML private TextField contoField;

    public void initialize() {
        pianoField.getItems().setAll(repository.findAllPianiTariffari());
        if (!pianoField.getItems().isEmpty()) {
            pianoField.setValue("base");
        }
    }

    @FXML
    public void handleRegistrati(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();
        String nome = nomeField.getText();
        String cognome = cognomeField.getText();
        String residenza = residenzaField.getText();
        String numero = numeroField.getText();
        String piano = pianoField.getValue();
        String conto = contoField.getText();

        if (isBlank(email) || isBlank(password) || isBlank(nome) || isBlank(cognome)
            || isBlank(residenza) || isBlank(numero) || isBlank(piano) || isBlank(conto)) {
            showAlert(Alert.AlertType.WARNING, "Attenzione", "Compila tutti i campi!");
            return;
        }

        try {
            repository.registerCliente(email, password, nome, cognome, residenza, numero, piano, conto);
            showAlert(Alert.AlertType.INFORMATION, "Registrazione", "Account creato con successo!");
            tornaAlLogin(event);
        } catch (RuntimeException exception) {
            showAlert(Alert.AlertType.ERROR, "Errore", "Registrazione non riuscita (email o numero già esistenti).");
        }
    }

    @FXML
    public void handleTornaLogin(ActionEvent event) {
        try {
            tornaAlLogin(event);
        } catch (RuntimeException exception) {
            showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile tornare alla login.");
        }
    }

    private void tornaAlLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("ParthEll - Login");
            stage.show();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
