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
import patterns.proxy.TelecomRepositoryProxy;
import service.FormInputValidator;
import service.TelecomRepository;

/*
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * @author / @version: Tracciano paternità e manutenzione della classe.
 * @param: Definisce i vincoli di input richiesti dal metodo per un uso corretto.
 * @return: Esplicita l'output garantito o l'assenza di risultato, così il Client sa cosa può usare.
 * @throws: Esplicita le eccezioni gestibili dal chiamante o previste dal contratto.
 */

/**
 * Gestisce la registrazione di nuovi utenti e la raccolta dei dati necessari al loro profilo.
 * Coordina validazione input, scelta del tipo di conto e creazione dell'account sul repository.
 * Usa un controller JavaFX perché il flusso dipende dalla visibilità dinamica dei campi della carta.
 *
 * @author ParthEll Team
 * @version 1.0
 */
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

    /**
     * Prepara i valori iniziali della schermata di registrazione.
     * Imposta piano, tipo di conto e visibilità dei campi carta in modo coerente.
     *
     * @return nessun valore; inizializza solo la vista.
     */
    public void initialize() {
        // Carico i piani disponibili dal repository prima di mostrare la form.
        pianoField.getItems().setAll(repository.findAllPianiTariffari());
        if (!pianoField.getItems().isEmpty()) {
            pianoField.setValue("base");
        }

        contoField.getItems().setAll("Ricaricabile", "Fisso");
        contoField.setValue("Fisso");

        // Aggiorno la UI quando cambia il tipo di conto.
        contoField.valueProperty().addListener(new ContoSelectionListener());

        // Allineo subito i campi carta al valore selezionato iniziale.
        boolean isFisso = "Fisso".equals(contoField.getValue());
        cartaLabel.setVisible(isFisso);
        numeroCartaField.setVisible(isFisso);
        scadenzaCartaField.setVisible(isFisso);
        cvvCartaField.setVisible(isFisso);
        intestatarioCartaField.setVisible(isFisso);
    }

    /**
     * Registra un nuovo account dopo aver validato i campi obbligatori.
     * Se il conto è fisso, verifica anche i dati della carta prima di salvare.
     *
     * @param event evento UI del pulsante di registrazione; deve provenire dalla form.
     * @return nessun valore; l'esito viene comunicato con alert e navigazione.
     */
    // Collegamento FXML: questo handler viene richiamato dalla vista definita nel file .fxml.
    @FXML
    public void handleRegistrati(ActionEvent event) {
        System.out.println("[ATTO 1 - 1. REGISTER CONTROLLER] L'utente ha cliccato Registrati. Avvio validazione dati e flusso di creazione account.");
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

        // Il conto fisso richiede i dati della carta, il ricaricabile no.
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
                // Persisto anche i dati carta perché il conto fisso li usa per l'addebito automatico.
                repository.registerCliente(email, password, nome, cognome, residenza, numero, piano, conto, numeroCarta, scadenza, cvv, intestatario);
                repository.inizializzaStoricoNuovoUtente(email);
                showAlert(Alert.AlertType.INFORMATION, "Registrazione", "Account creato con successo!");
                tornaAlLogin(event);
            } catch (RuntimeException exception) {
                showAlert(Alert.AlertType.ERROR, "Errore", "Registrazione non riuscita (email o numero già esistenti).");
            }
        } else {
            // Il conto ricaricabile evita i campi carta per ridurre gli input richiesti.
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

    /**
     * Riporta l'utente alla schermata di login senza creare un account.
     * In caso di problemi, mostra un messaggio e lascia la vista corrente invariata.
     *
     * @param event evento UI del pulsante "torna alla login".
     * @return nessun valore; eventuali errori sono mostrati tramite alert.
     */
    // Collegamento FXML: questo handler viene richiamato dalla vista definita nel file .fxml.
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
            // Cambio scena centralizzato per non duplicare la logica di navigazione.
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
        // Sicurezza: obbliga Java a verificare che sto davvero implementando changed() dell'interfaccia ChangeListener<String>, evitando errori di battitura.
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            // Il testo selezionato decide se la carta serve oppure no.
            boolean isFisso = "Fisso".equals(newValue);
            cartaLabel.setVisible(isFisso);
            numeroCartaField.setVisible(isFisso);
            scadenzaCartaField.setVisible(isFisso);
            cvvCartaField.setVisible(isFisso);
            intestatarioCartaField.setVisible(isFisso);

            if (!isFisso) {
                // Se il conto non usa la carta, svuoto i campi per evitare dati non coerenti.
                numeroCartaField.clear();
                scadenzaCartaField.clear();
                cvvCartaField.clear();
                intestatarioCartaField.clear();
            }
        }
    }
}
