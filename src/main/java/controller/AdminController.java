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
    @FXML private TableView<model.Utilizzo> tabStatistiche;
    @FXML private TableColumn<model.Utilizzo, String> numClienteStat;
    @FXML private TableColumn<model.Utilizzo, Integer> chiamateClienteStat;
    @FXML private TableColumn<model.Utilizzo, Integer> smsClienteStat;
    @FXML private TableColumn<model.Utilizzo, Integer> datiClienteStat;
    @FXML private TableColumn<model.Utilizzo, String> promoClienteStat;

    @FXML private TableView<model.Promozione> tabPromo;
    @FXML private TableColumn<model.Promozione, String> colNomePromo;
    @FXML private TableColumn<model.Promozione, Double> colCostoPromo;
    @FXML private TableColumn<model.Promozione, String> colDescrizionePromo;

    @FXML private TextField nomeField;
    @FXML private TextField cognomeField;
    @FXML private TextField emailField;
    @FXML private Button aggiungiAbbonatoButton;
    @FXML private TableView<Abbonato> abbonatiTable;
    @FXML private TableColumn<Abbonato, String> nomeColumn;
    @FXML private TableColumn<Abbonato, String> cognomeColumn;
    @FXML private TableColumn<Abbonato, String> numColumn;
    @FXML private TableColumn<Abbonato, String> emailColumn;
    @FXML private TableColumn<Abbonato, String> pianoColumn;
    @FXML private Button generaReportButton;
    @FXML private Button logoutButton;

        // ...altri campi...

        /**
         * Carica gli abbonati dal file CSV e li inserisce nella tabella.
         */
        private void caricaAbbonati() {
            javafx.collections.ObservableList<model.Abbonato> lista = javafx.collections.FXCollections.observableArrayList();
            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("src/main/resources/data/abbonato.csv"))) {
                String line;
                br.readLine(); // salta intestazione
                while ((line = br.readLine()) != null) {
                    String[] campi = line.split(",");
                    if (campi.length >= 7) {
                        model.Abbonato a = new model.Abbonato(
                            campi[2], // nome
                            campi[3], // cognome
                            campi[0], // email
                            campi[4], // residenza
                            campi[5], // numeroTelefono
                            campi[6], // pianoTariffario
                            campi[7]  // conto
                        );
                        lista.add(a);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            abbonatiTable.setItems(lista);
        }
    public void initialize() {
        // Inizializza le colonne della tabella
    nomeColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));
    cognomeColumn.setCellValueFactory(new PropertyValueFactory<>("cognome"));
    numColumn.setCellValueFactory(new PropertyValueFactory<>("numeroTelefono"));
    emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
    pianoColumn.setCellValueFactory(new PropertyValueFactory<>("pianoTariffario"));
        caricaAbbonati();

        // Colonne tabStatistiche
    if (numClienteStat != null) numClienteStat.setCellValueFactory(new PropertyValueFactory<>("numero"));
    if (chiamateClienteStat != null) chiamateClienteStat.setCellValueFactory(new PropertyValueFactory<>("chiamate"));
    if (smsClienteStat != null) smsClienteStat.setCellValueFactory(new PropertyValueFactory<>("sms"));
    if (datiClienteStat != null) datiClienteStat.setCellValueFactory(new PropertyValueFactory<>("dati"));
    if (promoClienteStat != null) promoClienteStat.setCellValueFactory(new PropertyValueFactory<>("promo"));
        caricaUtilizzo();

        // Colonne tabPromo
    if (colNomePromo != null) colNomePromo.setCellValueFactory(new PropertyValueFactory<>("nome"));
    if (colCostoPromo != null) colCostoPromo.setCellValueFactory(new PropertyValueFactory<>("prezzo"));
    if (colDescrizionePromo != null) colDescrizionePromo.setCellValueFactory(new PropertyValueFactory<>("descrizione"));
        caricaPromozioni();
        }
    /**
     * Carica i dati di utilizzo.csv nella tabella tabStatistiche
     */
    private void caricaUtilizzo() {
        javafx.collections.ObservableList<model.Utilizzo> lista = javafx.collections.FXCollections.observableArrayList();
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("src/main/resources/data/utilizzo.csv"))) {
            String line;
            br.readLine(); // salta intestazione
            while ((line = br.readLine()) != null) {
                String[] campi = line.split(",");
                if (campi.length >= 5) {
                    model.Utilizzo u = new model.Utilizzo(
                        campi[0],
                        Integer.parseInt(campi[1]),
                        Integer.parseInt(campi[2]),
                        Integer.parseInt(campi[3]),
                        campi[4]
                    );
                    lista.add(u);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (tabStatistiche != null) tabStatistiche.setItems(lista);
    }

    /**
     * Carica le promozioni da promozioni.csv nella tabella tabPromo
     */
    private void caricaPromozioni() {
        javafx.collections.ObservableList<model.Promozione> lista = javafx.collections.FXCollections.observableArrayList();
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("src/main/resources/data/promozioni.csv"))) {
            String line;
            br.readLine(); // salta intestazione
            while ((line = br.readLine()) != null) {
                String[] campi = line.split(",");
                if (campi.length >= 3) {
                    double costo = 0.0;
                    try { costo = Double.parseDouble(campi[1]); } catch (Exception ex) {}
                    model.Promozione p = new model.Promozione(
                        campi[0],
                        campi[2],
                        costo
                    );
                    lista.add(p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (tabPromo != null) tabPromo.setItems(lista);
        // fine metodo, NON chiudere la classe qui
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
