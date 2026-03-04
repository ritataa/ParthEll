package controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Abbonato;
import service.TelecomRepository;
import service.UserSession;

public class AdminController {
    private final TelecomRepository repository = new TelecomRepository();

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

    @FXML private TableView<Abbonato> abbonatiTable;
    @FXML private TableColumn<Abbonato, String> nomeColumn;
    @FXML private TableColumn<Abbonato, String> cognomeColumn;
    @FXML private TableColumn<Abbonato, String> numColumn;
    @FXML private TableColumn<Abbonato, String> pianoColumn;

        // ...altri campi...

        private void caricaAbbonati() {
            javafx.collections.ObservableList<model.Abbonato> lista = javafx.collections.FXCollections.observableArrayList(repository.findAllAbbonati());
            if (abbonatiTable != null) {
                abbonatiTable.setItems(lista);
            }
        }
    public void initialize() {
        // Inizializza le colonne della tabella
    if (nomeColumn != null) nomeColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));
    if (cognomeColumn != null) cognomeColumn.setCellValueFactory(new PropertyValueFactory<>("cognome"));
    if (numColumn != null) numColumn.setCellValueFactory(new PropertyValueFactory<>("numeroTelefono"));
    if (pianoColumn != null) pianoColumn.setCellValueFactory(new PropertyValueFactory<>("pianoTariffario"));
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
    private void caricaUtilizzo() {
        javafx.collections.ObservableList<model.Utilizzo> lista = javafx.collections.FXCollections.observableArrayList(repository.findAllUtilizzi());
        if (tabStatistiche != null) tabStatistiche.setItems(lista);
    }

    private void caricaPromozioni() {
        javafx.collections.ObservableList<model.Promozione> lista = javafx.collections.FXCollections.observableArrayList(repository.findAllPromozioni());
        if (tabPromo != null) tabPromo.setItems(lista);
        // fine metodo, NON chiudere la classe qui
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
            UserSession.getInstance().clear();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("ParthEll - Login");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile tornare al login!");
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
