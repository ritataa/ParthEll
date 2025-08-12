package controller;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Promozione;

public class ClienteController {

    @FXML private Label welcomeLabel;
    @FXML private TextField numeroField;
    @FXML private TextField durataField;
    @FXML private Button effettuaChiamataButton;
    @FXML private TextField numeroSmsField;
    @FXML private TextField testoSmsField;
    @FXML private Button inviaSmsButton;
    @FXML private TextField datiField;
    @FXML private Button usaDatiButton;
    @FXML private TableView<Promozione> promozioniTable;
    @FXML private TableColumn<Promozione, String> nomePromozioneColumn;
    @FXML private TableColumn<Promozione, String> descrizioneColumn;
    @FXML private Button aderisciPromozioneButton;
    @FXML private Button pagamentoContantiButton;
    @FXML private Button pagamentoCartaButton;
    @FXML private Button pagamentoBancomatButton;
    @FXML private Button logoutButton;
        @FXML private TextField emailClienteField;
        @FXML private TextField passwordClienteField;
        @FXML private TextField nomeClienteField;
        @FXML private TextField cognomeClienteField;
        @FXML private Button aggiungiClienteButton;

    private ObservableList<Promozione> promozioni = FXCollections.observableArrayList();

    public void initialize() {
        // Inizializza le colonne della tabella promozioni
        nomePromozioneColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));
        descrizioneColumn.setCellValueFactory(new PropertyValueFactory<>("descrizione"));
        
        // Carica le promozioni di esempio
        caricaPromozioni();
        promozioniTable.setItems(promozioni);
    }

    private void caricaPromozioni() {
        promozioni.addAll(
            new Promozione("Super Plus", "1000 minuti, 1000 SMS, 50GB - €25/mese"),
            new Promozione("Basic", "500 minuti, 500 SMS, 20GB - €15/mese"),
            new Promozione("Premium", "Illimitato tutto - €35/mese"),
            new Promozione("Weekend Special", "Minuti illimitati nel weekend - €10/mese")
        );
    }

    @FXML
    public void handleEffettuaChiamata(ActionEvent event) {
        String numero = numeroField.getText();
        String durata = durataField.getText();

        if (numero.isEmpty() || durata.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Attenzione", "Inserisci numero e durata!");
            return;
        }

        try {
            int min = Integer.parseInt(durata);
            showAlert(Alert.AlertType.INFORMATION, "Chiamata", 
                     "Chiamata di " + min + " minuti al numero " + numero + " effettuata con successo!");
            
            // Pulisci i campi
            numeroField.clear();
            durataField.clear();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Errore", "Inserisci una durata valida!");
        }
    }

    @FXML
    public void handleInviaSms(ActionEvent event) {
        String numero = numeroSmsField.getText();
        String testo = testoSmsField.getText();

        if (numero.isEmpty() || testo.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Attenzione", "Inserisci numero e testo!");
            return;
        }

        showAlert(Alert.AlertType.INFORMATION, "SMS", 
                 "SMS inviato al numero " + numero + " con successo!");
        
        // Pulisci i campi
        numeroSmsField.clear();
        testoSmsField.clear();
    }

    @FXML
    public void handleUsaDati(ActionEvent event) {
        String dati = datiField.getText();

        if (dati.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Attenzione", "Inserisci la quantità di dati!");
            return;
        }

        try {
            int mb = Integer.parseInt(dati);
            showAlert(Alert.AlertType.INFORMATION, "Dati", 
                     "Utilizzati " + mb + " MB con successo!");
            
            datiField.clear();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Errore", "Inserisci una quantità valida!");
        }
    }

    @FXML
    public void handleAderisciPromozione(ActionEvent event) {
        Promozione selezionata = promozioniTable.getSelectionModel().getSelectedItem();
        
        if (selezionata == null) {
            showAlert(Alert.AlertType.WARNING, "Attenzione", "Seleziona una promozione!");
            return;
        }

        showAlert(Alert.AlertType.INFORMATION, "Promozione", 
                 "Hai aderito alla promozione: " + selezionata.getNome());
    }

    @FXML
    public void handlePagamentoContanti(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Pagamento", 
                 "Pagamento in contanti registrato!");
    }

    @FXML
    public void handlePagamentoCarta(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Pagamento", 
                 "Pagamento con carta di credito registrato!");
    }

    @FXML
    public void handlePagamentoBancomat(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Pagamento", 
                 "Pagamento con bancomat registrato!");
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

        /**
         * Gestisce l'aggiunta di un nuovo cliente al file CSV.
         */
        @FXML
        public void handleAggiungiCliente(ActionEvent event) {
            String email = emailClienteField.getText();
            String password = passwordClienteField.getText();
            String nome = nomeClienteField.getText();
            String cognome = cognomeClienteField.getText();

            if (email.isEmpty() || password.isEmpty() || nome.isEmpty() || cognome.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Attenzione", "Compila tutti i campi!");
                return;
            }

            String ruolo = "cliente";
            String riga = String.format("%s,%s,%s,%s,%s\n", email, password, ruolo, nome, cognome);
            String path = "src/main/resources/data/abbonato.csv";

            try (java.io.FileWriter fw = new java.io.FileWriter(path, true)) {
                fw.write(riga);
                showAlert(Alert.AlertType.INFORMATION, "Successo", "Cliente aggiunto correttamente!");
                emailClienteField.clear();
                passwordClienteField.clear();
                nomeClienteField.clear();
                cognomeClienteField.clear();
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile aggiungere il cliente!");
                e.printStackTrace();
            }
        }
}
