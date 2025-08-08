package controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Abbonato;

public class AdminController {

    @FXML private TextField nomeField;
    @FXML private TextField cognomeField;
    @FXML private TextField emailField;
    @FXML private Button aggiungiAbbonatoButton;
    @FXML private TableView<Abbonato> abbonatiTable;
    @FXML private TableColumn<Abbonato, String> nomeColumn;
    @FXML private TableColumn<Abbonato, String> cognomeColumn;
    @FXML private TableColumn<Abbonato, String> emailColumn;
    @FXML private TableColumn<Abbonato, String> pianoColumn;
    @FXML private Button generaReportButton;
    @FXML private Button logoutButton;

    public void initialize() {
        // Inizializza le colonne della tabella
        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));
        cognomeColumn.setCellValueFactory(new PropertyValueFactory<>("cognome"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        pianoColumn.setCellValueFactory(new PropertyValueFactory<>("pianoTariffario"));
    }

    @FXML
    public void handleAggiungiAbbonato(ActionEvent event) {
        String nome = nomeField.getText();
        String cognome = cognomeField.getText();
        String email = emailField.getText();

        if (nome.isEmpty() || cognome.isEmpty() || email.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Attenzione", "Compila tutti i campi!");
            return;
        }

        // Qui potresti aggiungere l'abbonato al database/file
        // Per ora mostriamo solo un messaggio di conferma
        showAlert(Alert.AlertType.INFORMATION, "Successo", 
                 "Abbonato " + nome + " " + cognome + " aggiunto con successo!");

        // Pulisci i campi
        nomeField.clear();
        cognomeField.clear();
        emailField.clear();
    }

    @FXML
    public void handleGeneraReport(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Report", 
                 "Funzionalità di generazione report in sviluppo!");
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        try {
            // Torna alla schermata di login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("ParthEll - Login");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile tornare al login!");
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
