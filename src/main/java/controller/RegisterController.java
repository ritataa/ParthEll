package controller;

import java.io.IOException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import service.FormInputValidator;
import service.TelecomRepository;
import patterns.proxy.TelecomRepositoryProxy;

public class RegisterController {

    private final TelecomRepository repository = new TelecomRepositoryProxy();
    private final FormInputValidator validator = new FormInputValidator();

    @FXML private TextField emailField;
    @FXML private TextField passwordField;
    @FXML private TextField nomeField;
    @FXML private TextField cognomeField;
    @FXML private TextField residenzaField;
    @FXML private TextField numeroField;
    @FXML private ComboBox<String> pianoField;
    @FXML private ChoiceBox<String> contoField;
    @FXML private Label cartaLabel;
    @FXML private TextField numeroCartaField;
    @FXML private TextField scadenzaCartaField;
    @FXML private TextField cvvCartaField;
    @FXML private TextField intestatarioCartaField;

    public void initialize() {
        pianoField.getItems().setAll(repository.findAllPianiTariffari());
        if (!pianoField.getItems().isEmpty()) {
            pianoField.setValue("base");
        }

        contoField.getItems().setAll("Ricaricabile", "Fisso");
        contoField.setValue("Fisso");

        // Mostra/nascondi i campi della carta in base al conto selezionato
        contoField.valueProperty().addListener(new ContoSelectionListener());

        // Inizialmente nascondi se il default è Ricaricabile (anche se il default è Fisso)
        boolean isFisso = "Fisso".equals(contoField.getValue());
        cartaLabel.setVisible(isFisso);
        numeroCartaField.setVisible(isFisso);
        scadenzaCartaField.setVisible(isFisso);
        cvvCartaField.setVisible(isFisso);
        intestatarioCartaField.setVisible(isFisso);
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
        String conto = contoField.getValue();

        if (isBlank(email) || isBlank(password) || isBlank(nome) || isBlank(cognome)
            || isBlank(residenza) || isBlank(numero) || isBlank(piano) || isBlank(conto)) {
            showAlert(Alert.AlertType.WARNING, "Attenzione", "Compila tutti i campi!");
            return;
        }

        // Se Fisso, validare i dati della carta
        if ("Fisso".equals(conto)) {
            String numeroCarta = numeroCartaField.getText().trim();
            String scadenza = scadenzaCartaField.getText().trim();
            String cvv = cvvCartaField.getText().trim();
            String intestatario = intestatarioCartaField.getText().trim();

            if (isBlank(numeroCarta) || isBlank(scadenza) || isBlank(cvv) || isBlank(intestatario)) {
                showAlert(Alert.AlertType.WARNING, "Attenzione", "Per il conto Fisso, inserisci tutti i dati della carta!");
                return;
            }

            if (!validator.isValidCardData(intestatario, numeroCarta, scadenza, cvv)) {
                showAlert(Alert.AlertType.WARNING, "Dati carta non validi", "Numero 16 cifre, scadenza MM/AA, CVV 3 cifre.");
                return;
            }

            try {
                repository.registerCliente(email, password, nome, cognome, residenza, numero, piano, conto, numeroCarta, scadenza, cvv, intestatario);
                repository.inizializzaStoricoNuovoUtente(email);
                showAlert(Alert.AlertType.INFORMATION, "Registrazione", "Account creato con successo!");
                tornaAlLogin(event);
            } catch (RuntimeException exception) {
                showAlert(Alert.AlertType.ERROR, "Errore", "Registrazione non riuscita (email o numero già esistenti).");
            }
        } else {
            // Ricaricabile: niente dati carta
            try {
                repository.registerCliente(email, password, nome, cognome, residenza, numero, piano, conto);
                repository.inizializzaStoricoNuovoUtente(email);
                showAlert(Alert.AlertType.INFORMATION, "Registrazione", "Account creato con successo!");
                tornaAlLogin(event);
            } catch (RuntimeException exception) {
                showAlert(Alert.AlertType.ERROR, "Errore", "Registrazione non riuscita (email o numero già esistenti).");
            }
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

    // Listener nominale per aggiornare visibilita' e contenuto campi carta in base al tipo conto.
    private class ContoSelectionListener implements ChangeListener<String> {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            boolean isFisso = "Fisso".equals(newValue);
            cartaLabel.setVisible(isFisso);
            numeroCartaField.setVisible(isFisso);
            scadenzaCartaField.setVisible(isFisso);
            cvvCartaField.setVisible(isFisso);
            intestatarioCartaField.setVisible(isFisso);

            if (!isFisso) {
                // Se il conto diventa ricaricabile, svuota i dati carta per evitare valori stale.
                numeroCartaField.clear();
                scadenzaCartaField.clear();
                cvvCartaField.clear();
                intestatarioCartaField.clear();
            }
        }
    }
}
