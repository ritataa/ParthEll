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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Abbonato;
import service.AuthFacade;
import service.TelecomRepository;

public class AdminController {
    private final TelecomRepository repository = new TelecomRepository();
    private final AuthFacade authFacade = new AuthFacade();

    @FXML private TableView<model.Utilizzo> tabStatistiche;
    @FXML private TableColumn<model.Utilizzo, String> numClienteStat;
    @FXML private TableColumn<model.Utilizzo, String> nomeClienteStat;
    @FXML private TableColumn<model.Utilizzo, String> cognomeClienteStat;
    @FXML private TableColumn<model.Utilizzo, String> emailClienteStat;
    @FXML private TableColumn<model.Utilizzo, Integer> chiamateClienteStat;
    @FXML private TableColumn<model.Utilizzo, Integer> smsClienteStat;
    @FXML private TableColumn<model.Utilizzo, Integer> datiClienteStat;
    @FXML private TableColumn<model.Utilizzo, String> promoClienteStat;

    @FXML private TableView<model.Promozione> tabPromo;
    @FXML private TableColumn<model.Promozione, String> colNomePromo;
    @FXML private TableColumn<model.Promozione, Double> colCostoPromo;
    @FXML private TableColumn<model.Promozione, String> colDescrizionePromo;
    @FXML private TextField searchFieldPromozioni;
    @FXML private TextField nomePromozione;
    @FXML private TextField costoPromozione;
    @FXML private TextArea descrizionePromozione;

    @FXML private TableView<Abbonato> abbonatiTable;
    @FXML private TableColumn<Abbonato, String> nomeColumn;
    @FXML private TableColumn<Abbonato, String> cognomeColumn;
    @FXML private TableColumn<Abbonato, String> numColumn;
    @FXML private TableColumn<Abbonato, String> pianoColumn;
    @FXML private TextField searchFieldAbbonati;

    private javafx.collections.ObservableList<Abbonato> abbonatiCompleti = javafx.collections.FXCollections.observableArrayList();
    private javafx.collections.ObservableList<model.Promozione> promozioniComplete = javafx.collections.FXCollections.observableArrayList();

        // ...altri campi...

        private void caricaAbbonati() {
            abbonatiCompleti = javafx.collections.FXCollections.observableArrayList(repository.findAllAbbonati());
            if (abbonatiTable != null) {
                abbonatiTable.setItems(abbonatiCompleti);
            }
        }
    public void initialize() {
        // Inizializza le colonne della tabella
    if (nomeColumn != null) nomeColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));
    if (cognomeColumn != null) cognomeColumn.setCellValueFactory(new PropertyValueFactory<>("cognome"));
    if (numColumn != null) numColumn.setCellValueFactory(new PropertyValueFactory<>("numeroTelefono"));
    if (pianoColumn != null) pianoColumn.setCellValueFactory(new PropertyValueFactory<>("pianoTariffario"));
        caricaAbbonati();
        if (searchFieldAbbonati != null) {
            searchFieldAbbonati.textProperty().addListener((obs, oldValue, newValue) -> filtraAbbonatiPerNumero(newValue));
        }

        // Colonne tabStatistiche
    if (numClienteStat != null) numClienteStat.setCellValueFactory(new PropertyValueFactory<>("numero"));
    if (nomeClienteStat != null) nomeClienteStat.setCellValueFactory(new PropertyValueFactory<>("nome"));
    if (cognomeClienteStat != null) cognomeClienteStat.setCellValueFactory(new PropertyValueFactory<>("cognome"));
    if (emailClienteStat != null) emailClienteStat.setCellValueFactory(new PropertyValueFactory<>("email"));
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
        if (searchFieldPromozioni != null) {
            searchFieldPromozioni.textProperty().addListener((obs, oldValue, newValue) -> filtraPromozioniPerNome(newValue));
        }
        }
    private void caricaUtilizzo() {
        javafx.collections.ObservableList<model.Utilizzo> lista = javafx.collections.FXCollections.observableArrayList(repository.findAllUtilizzi());
        if (tabStatistiche != null) tabStatistiche.setItems(lista);
    }

    private void caricaPromozioni() {
        promozioniComplete = javafx.collections.FXCollections.observableArrayList(repository.findAllPromozioni());
        if (tabPromo != null) tabPromo.setItems(promozioniComplete);
        // fine metodo, NON chiudere la classe qui
    }

    private void filtraAbbonatiPerNumero(String filtroNumero) {
        if (abbonatiTable == null) {
            return;
        }
        if (filtroNumero == null || filtroNumero.isBlank()) {
            abbonatiTable.setItems(abbonatiCompleti);
            return;
        }
        String filtro = filtroNumero.trim();
        javafx.collections.ObservableList<Abbonato> filtrati = javafx.collections.FXCollections.observableArrayList();
        for (Abbonato abbonato : abbonatiCompleti) {
            if (abbonato.getNumeroTelefono() != null && abbonato.getNumeroTelefono().contains(filtro)) {
                filtrati.add(abbonato);
            }
        }
        abbonatiTable.setItems(filtrati);
    }

    private void filtraPromozioniPerNome(String filtroNome) {
        if (tabPromo == null) {
            return;
        }
        if (filtroNome == null || filtroNome.isBlank()) {
            tabPromo.setItems(promozioniComplete);
            return;
        }

        String filtro = filtroNome.trim().toLowerCase();
        javafx.collections.ObservableList<model.Promozione> filtrate = javafx.collections.FXCollections.observableArrayList();
        for (model.Promozione promozione : promozioniComplete) {
            String nome = promozione.getNome();
            if (nome != null && nome.toLowerCase().contains(filtro)) {
                filtrate.add(promozione);
            }
        }
        tabPromo.setItems(filtrate);
    }

    @FXML
    public void handleAggiungiPromozione(ActionEvent event) {
        String nome = nomePromozione == null ? "" : nomePromozione.getText();
        String costo = costoPromozione == null ? "" : costoPromozione.getText();
        String descrizione = descrizionePromozione == null ? "" : descrizionePromozione.getText();

        if (nome == null || nome.isBlank() || costo == null || costo.isBlank() || descrizione == null || descrizione.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Attenzione", "Compila tutti i campi della promozione.");
            return;
        }

        try {
            double prezzo = Double.parseDouble(costo.trim());
            if (prezzo <= 0) {
                showAlert(Alert.AlertType.WARNING, "Attenzione", "Il costo deve essere maggiore di zero.");
                return;
            }
            repository.addPromozione(nome.trim(), prezzo, descrizione.trim());
            caricaPromozioni();
            filtraPromozioniPerNome(searchFieldPromozioni == null ? "" : searchFieldPromozioni.getText());
            nomePromozione.clear();
            costoPromozione.clear();
            descrizionePromozione.clear();
            showAlert(Alert.AlertType.INFORMATION, "Promozioni", "Promozione aggiunta correttamente.");
        } catch (NumberFormatException exception) {
            showAlert(Alert.AlertType.ERROR, "Errore", "Inserisci un costo numerico valido.");
        } catch (RuntimeException exception) {
            showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile aggiungere la promozione.");
        }
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        try {
            // Torna alla schermata di login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent root = loader.load();
            authFacade.logout();

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
